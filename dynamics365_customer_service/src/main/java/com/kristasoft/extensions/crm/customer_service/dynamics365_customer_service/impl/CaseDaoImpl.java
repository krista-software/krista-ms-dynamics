/*
 * Ms Dynamics Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Incidents;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.WorkOrders;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsCase;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.SystemStatusMapping;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Case;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.CaseDao;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getCaseStatusFromKey;
import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getPriorityFromKey;

@Service
public class CaseDaoImpl implements CaseDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDaoImpl.class);

    public static final String PRIORITY_CODE = "prioritycode";
    public static final String STATUS_CODE = "statuscode";
    public static final String SUBJECT_ID = "Subject Id";
    public static final String CASE_STATUS = "Case Status";
    public static final String PRIORITY = "Priority";
    public static final String SUBJECTID_ODATA_BIND = "subjectid@odata.bind";
    public static final String CUSTOMERID_CONTACT_ODATA_BIND = "customerid_contact@odata.bind";
    private final RestClientProvider provider;

    @Inject
    public CaseDaoImpl(RestClientProviderFactory providerFactory) {
        this(providerFactory.create());
    }

    public CaseDaoImpl(RestClientProvider provider) {
        this.provider = provider;
    }

    /**
     * Retrieves a single case/incident by its unique identifier from Dynamics 365.
     *
     * @param id The incident GUID (incidentid) to retrieve
     * @return Case object containing the case details
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public Case getCaseById(String id) throws MalformedURLException {
        return new CaseImpl(Constants.GSON.fromJson(provider
                        .getRestClient()
                        .get(Constants.INCIDENTS_API + "(" + id + ")", new HashMap<>()),
                DynamicsCase.class));
    }

    /**
     * Retrieves all cases without filtering. Delegates to getCasesByQuery with empty parameters.
     *
     * @param pageSize Pagination size (currently unused, returns all results)
     * @param pageIndex Page index (currently unused)
     * @param orderByField Field name for sorting (currently unused)
     * @param orderByOperand Sort direction (currently unused)
     * @return List of all Case objects
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public List<Case> getCases(long pageSize, long pageIndex, String orderByField, String orderByOperand) throws MalformedURLException {
        return getCasesByQuery(new HashMap<>());
    }

    /**
     * Retrieves cases based on OData query parameters.
     * Executes GET request to Dynamics 365 incidents API and maps results to Case objects.
     *
     * @param queryParams Map of OData query parameters (e.g., $filter, $select, $orderby)
     * @return List of Case objects matching the query, or empty list if no results
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public List<Case> getCasesByQuery(Map<String, Object> queryParams) throws MalformedURLException {
        String result = provider.getRestClient().get(Constants.INCIDENTS_API, queryParams);
        JsonArray jsonArrayResult = Constants.GSON.fromJson(result, JsonObject.class).getAsJsonArray(Constants.VALUE);

        Type listType = new TypeToken<List<DynamicsCase>>() {
        }.getType();
        List<DynamicsCase> cases = Constants.GSON.fromJson(jsonArrayResult.toString(), listType);

        if (!cases.isEmpty()) {
            return cases.stream().map(CaseImpl::new).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Creates a new case/incident in Dynamics 365.
     * Constructs the payload from entity definition and posts to the incidents API.
     *
     * @param entityDefinition Map containing case field values
     * @return JSON response string from Dynamics 365 containing the created case details
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws JSONException if JSON construction or parsing fails
     */
    @Override
    public String createCase(Map<String, Object> entityDefinition) throws MalformedURLException, JSONException {
        return provider.getRestClient().post("incidents", getCreateCasePayload(entityDefinition));

    }

    /**
     * Constructs the JSON payload for creating a new case in Dynamics 365.
     * Maps entity definition fields to Dynamics 365 API field names and handles OData bindings.
     * Converts priority and status values from display names to numeric codes.
     *
     * @param entityDefinition Map containing case field values with keys matching catalog entity field names
     * @return JSONObject formatted for Dynamics 365 incident creation API
     * @throws JSONException if JSON construction fails
     */
    private JSONObject getCreateCasePayload(Map<String, Object> entityDefinition) throws JSONException {
        JSONObject target = new JSONObject();

        target.put("title", entityDefinition.get("Case Title"));
        target.put(CUSTOMERID_CONTACT_ODATA_BIND, "/accounts(" + entityDefinition.get("Contact Id") + ")");
        if (entityDefinition.get(SUBJECT_ID) != null && !entityDefinition.get(SUBJECT_ID).toString().isBlank()) {
            target.put(SUBJECTID_ODATA_BIND, "/subjects(" + entityDefinition.get(SUBJECT_ID) + ")");
        }
        target.put("description", entityDefinition.get("Description"));
        String priorityValue = (String) entityDefinition.get(PRIORITY);
        if (priorityValue != null && !priorityValue.isBlank()) {
            target.put(PRIORITY_CODE, getPriorityFromKey(priorityValue));
        }
        String caseStatusValue = (String) entityDefinition.get(CASE_STATUS);
        if (caseStatusValue != null && !caseStatusValue.isBlank()) {
            target.put(STATUS_CODE, getCaseStatusFromKey(caseStatusValue));
        }

        return target;
    }

    /**
     * Updates an existing case in Dynamics 365.
     * Compares old and new case states, sends only changed fields via PATCH request.
     *
     * @param oldCase Case object containing updated field values
     * @return Updated Case object with latest values from Dynamics 365
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws IllegalAccessException if reflection access to fields fails
     */
    @Override
    public Case updateCase(Case oldCase) throws MalformedURLException, IllegalAccessException {
        DynamicsCase updatedCase = DynamicsCase.from(oldCase);
        DynamicsCase oldDynamicsCase = DynamicsCase.from(getCaseById(oldCase.getCaseId()));
        Map<String, String> differenceInCases = getDifference(oldDynamicsCase, updatedCase);
        return new CaseImpl(Constants.GSON.fromJson(provider
                        .getRestClient()
                        .patch(Constants.INCIDENTS_API + "(" + oldCase.getCaseId() + ")",
                                Constants.GSON.toJson(differenceInCases)),
                DynamicsCase.class));
    }

    /**
     * Compares two DynamicsCase objects and returns a map of changed fields.
     * Uses reflection to iterate through all fields and identify differences.
     *
     * @param oldDynamicsCase Original case state from Dynamics 365
     * @param updatedCase New case state with changes
     * @return Map of field names to new values for fields that changed (excluding caseId)
     * @throws IllegalAccessException if reflection access to fields fails
     */
    private Map<String, String> getDifference(DynamicsCase oldDynamicsCase, DynamicsCase updatedCase) throws IllegalAccessException {
        Map<String, String> differences = new HashMap<>();
        Field[] fields = DynamicsCase.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            Object oldValue = field.get(oldDynamicsCase);
            oldValue = (oldValue == null) ? "" : oldValue;

            Object updatedValue = field.get(updatedCase);
            updatedValue = (updatedValue == null) ? "" : updatedValue;

            if ((!Objects.equals(oldValue, updatedValue)) &&
                    (!field.getName().equalsIgnoreCase("caseId"))) {
                String payloadName = getPayloadName(field);
                if (payloadName != null && !payloadName.isBlank()) {
                    differences.put(payloadName, getPayloadValue(payloadName, (String) updatedValue));
                }
            }
        }

        return differences;
    }

    /**
     * Maps Java field names to Dynamics 365 API payload field names for cases.
     * Handles special cases for relationship fields and option set fields.
     *
     * @param field The Java Field object to map
     * @return Dynamics 365 API field name or OData binding name
     */
    private String getPayloadName(Field field) {

        if (field.getName().equalsIgnoreCase("contactId")) {
            return CUSTOMERID_CONTACT_ODATA_BIND;
        } else if (field.getName().equalsIgnoreCase("subjectId")) {
            return SUBJECTID_ODATA_BIND;
        }

        SerializedName payloadFieldName = field.getAnnotation(SerializedName.class);
        if (payloadFieldName == null) {
            if (field.getName().equalsIgnoreCase("priority")) {
                return PRIORITY_CODE;
            } else if (field.getName().equalsIgnoreCase("caseStatus")) {
                return STATUS_CODE;
            }
        } else {
            return payloadFieldName.value();
        }
        return "";
    }

    /**
     * Formats field values for Dynamics 365 API payload for cases.
     * Converts relationship IDs to OData binding format and display values to numeric codes.
     *
     * @param field The field name being updated
     * @param updatedValue The new value for the field
     * @return Formatted value (OData binding for relationships, numeric code for option sets, raw value otherwise)
     */
    private String getPayloadValue(String field, String updatedValue) {
        if (field.equalsIgnoreCase(STATUS_CODE)) {
            return getCaseStatusFromKey(updatedValue);
        } else if (field.equalsIgnoreCase(PRIORITY_CODE)) {
            return getPriorityFromKey(updatedValue);
        } else if (field.equalsIgnoreCase(CUSTOMERID_CONTACT_ODATA_BIND)) {
            return "/accounts(" + updatedValue + ")";
        } else if (field.equalsIgnoreCase(SUBJECTID_ODATA_BIND)) {
            return "/subjects(" + updatedValue + ")";
        }
        return updatedValue;
    }

    /**
     * Deletes a case from Dynamics 365 by its unique identifier.
     *
     * @param id The incident GUID (incidentid) to delete
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public void deleteCaseById(String id) throws MalformedURLException {
        provider.getRestClient().delete(Constants.INCIDENTS_API + "(" + id + ")");
    }

    /**
     * Retrieves all available subjects from Dynamics 365 for case categorization.
     * Subjects are used to classify cases into categories.
     *
     * @return List of maps containing "Subject Title" and "Subject Id" for each subject
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws IllegalStateException if JSON processing fails
     */
    @Override
    public List<Map<String, Object>> getSubjects() throws MalformedURLException {
        try {
            final String subjects = provider.getRestClient().get("subjects", new HashMap<>());
            JsonArray subjectsArray = Constants.GSON.fromJson(subjects, JsonObject.class)
                    .getAsJsonArray(Constants.VALUE);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonArray = objectMapper.readTree(subjectsArray.toString());
            List<Map<String, Object>> result = new ArrayList<>();
            for (JsonNode node : jsonArray) {
                Map<String, Object> eachSubject = new HashMap<>();
                eachSubject.put("Subject Title", node.get("title").asText());
                eachSubject.put(SUBJECT_ID, node.get("subjectid").asText());
                result.add(eachSubject);
            }
            return result;
        } catch (JsonProcessingException cause) {
            throw new IllegalStateException("Failed to get subjects list.", cause);
        }
    }

    /**
     * Searches for accounts by name with specific filtering criteria.
     * Only returns active accounts with account numbers starting with 'S'.
     * Sanitizes input to prevent OData injection attacks.
     *
     * @param name The account name to search for (partial match supported)
     * @return List of maps containing "Account Name", "Address", and "Account Number" for matching accounts
     * @throws RuntimeException if API endpoint is malformed
     * @throws IllegalStateException if account retrieval fails
     */
    public List<Map<String, String>> getAccountByName(String name) {
        LOGGER.info("Getting accounts by name: {}", name);
        try {
            List<Map<String, String>> accountList = new ArrayList<>();
            Map<String, Object> queryParams = new HashMap<>();
            String sanitizeName = name.replace("'", "''");
            String filterQuery = "contains(name, '" + sanitizeName + "')";
            queryParams.put("$filter", filterQuery);
            final String response = provider.getRestClient().get("accounts", queryParams);
            LOGGER.debug("Received response from Dynamics API for accounts query");

            // ✅ Use Gson only (no ObjectMapper)
            JsonObject responseJson = Constants.GSON.fromJson(response, JsonObject.class);
            JsonArray accountsArray = responseJson.getAsJsonArray(Constants.VALUE);
            int totalAccounts = accountsArray.size();
            LOGGER.debug("Processing {} accounts from API response", totalAccounts);
            for (JsonElement element : accountsArray) {
                JsonObject accountObj = element.getAsJsonObject();
                if (accountObj.has("accountnumber") && accountObj.has("accountid")) {
                    String accountNumber = accountObj.get("accountnumber").getAsString();
                    String activeStatus = accountObj.has("rsm_accountstatus")
                            ? accountObj.get("rsm_accountstatus").getAsString()
                            : "";

                    if (accountNumber.startsWith("S") && "Active".equals(activeStatus)) {
                        Map<String, String> eachAccount = new HashMap<>();
                        eachAccount.put("Account Name",
                                accountObj.has("name") ? accountObj.get("name").getAsString() : "N/A");
                        eachAccount.put("Address",
                                accountObj.has("address1_line1") ? accountObj.get("address1_line1").getAsString() : "N/A");
                        eachAccount.put("Account Number",
                                accountObj.has("accountnumber") ? accountObj.get("accountnumber").getAsString() : "N/A");
                        accountList.add(eachAccount);
                    }
                }
            }
            return accountList;
        } catch (MalformedURLException cause) {
            LOGGER.error("Malformed URL while calling Dynamics API {}", cause);
            throw new RuntimeException("Invalid API endpoint configuration. Verify the base URL in extension attributes is correct.", cause);
        } catch (Exception cause) {
            LOGGER.error("Failed to get accounts list. {} ", cause);
            throw new IllegalStateException("Unable to retrieve accounts list. Verify your permissions and API connectivity.", cause);
        }
    }

    /**
     * Retrieves work orders for a specific account within a date range.
     * Defaults to last 90 days if dates not specified. Maximum range is 6 months (183 days).
     * Includes timeline details (annotations) for each work order.
     *
     * @param accountId The account GUID to retrieve work orders for
     * @param startDate Start date in epoch milliseconds (null defaults to 90 days ago)
     * @param endDate End date in epoch milliseconds (null defaults to now)
     * @return Map containing "Work Orders" list, "Total Count", and "Date Range Applied" information
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws IllegalArgumentException if accountId is null/empty or date range exceeds 6 months
     * @throws RuntimeException if work order retrieval fails
     */
    public Map<String, Object> getWorkOrderByAccountId(String accountId, Long startDate, Long endDate) throws MalformedURLException {
        LOGGER.info("Getting work orders by account ID: {}, startDate: {}, endDate: {}", accountId, startDate, endDate);
        try {
            validateAccountIdForWorkOrders(accountId);
            DateRange dateRange = buildWorkOrderDateRange(startDate, endDate);

            Map<String, Object> queryParams = buildWorkOrderQuery(accountId, dateRange);
            JsonArray workOrdersArray = fetchWorkOrdersFromApi(queryParams);

            List<WorkOrders> workOrders = processWorkOrdersData(workOrdersArray);

            return buildWorkOrderResponse(workOrders, dateRange);
        } catch (Exception cause) {
            LOGGER.error("Error occurred while getting work order by account id: {} - {}", accountId, cause.getMessage());
            throw new RuntimeException("Error retrieving work orders: " + cause.getMessage(), cause);
        }
    }

    /**
     * Validates that the account ID is not null or empty for work order queries.
     *
     * @param accountId The account ID to validate
     * @throws IllegalArgumentException if accountId is null or empty
     */
    private void validateAccountIdForWorkOrders(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            LOGGER.info("Account ID is required but was null or empty");
            throw new IllegalArgumentException("Account ID is required");
        }
    }

    /**
     * Builds a date range for work order queries with validation.
     * Defaults to last 90 days if dates not provided. Enforces 6-month maximum range.
     *
     * @param startDate Start date in epoch milliseconds (null defaults to 90 days ago)
     * @param endDate End date in epoch milliseconds (null defaults to now)
     * @return DateRange object containing start, end, and days between
     * @throws IllegalArgumentException if date range exceeds 183 days (6 months)
     */
    private DateRange buildWorkOrderDateRange(Long startDate, Long endDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = startDate != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault()) :
                now.minusDays(90);
        LocalDateTime endDateTime = endDate != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), ZoneId.systemDefault()) :
                now;

        long daysBetween = ChronoUnit.DAYS.between(startDateTime, endDateTime);
        LOGGER.debug("Date range: {} to {} ({} days)", startDateTime, endDateTime, daysBetween);

        if (daysBetween > 183) {
            LOGGER.info("Date range exceeds 6 months limit: {} days", daysBetween);
            throw new IllegalArgumentException("Date range cannot exceed 6 months");
        }

        return new DateRange(startDateTime, endDateTime, daysBetween);
    }

    /**
     * Constructs OData query parameters for work order retrieval.
     * Filters by service account and creation date range.
     *
     * @param accountId The account GUID to filter by
     * @param dateRange The date range to filter by
     * @return Map of OData query parameters with $filter clause
     */
    private Map<String, Object> buildWorkOrderQuery(String accountId, DateRange dateRange) {
        DateTimeFormatter oDataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedStartDate = dateRange.startDateTime.format(oDataFormatter);
        String formattedEndDate = dateRange.endDateTime.format(oDataFormatter);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("$filter", "_msdyn_serviceaccount_value eq '" + accountId +
                "' and createdon ge " + formattedStartDate +
                " and createdon le " + formattedEndDate);

        return queryParams;
    }

    /**
     * Fetches work orders from Dynamics 365 API using provided query parameters.
     *
     * @param queryParams OData query parameters for filtering
     * @return JsonArray containing work order data
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    private JsonArray fetchWorkOrdersFromApi(Map<String, Object> queryParams) throws MalformedURLException {
        String response = provider.getRestClient().get("msdyn_workorders", queryParams);
        JsonObject jsonResponse = Constants.GSON.fromJson(response, JsonObject.class);
        return jsonResponse.getAsJsonArray("value");
    }

    /**
     * Processes raw work order JSON data into WorkOrders entity objects.
     * Enriches each work order with timeline details (annotations).
     *
     * @param workOrdersArray JsonArray of work order data from API
     * @return List of WorkOrders entities with timeline details
     */
    private List<WorkOrders> processWorkOrdersData(JsonArray workOrdersArray) {
        List<WorkOrders> workOrders = new ArrayList<>();

        for (JsonElement element : workOrdersArray) {
            JsonObject workOrder = element.getAsJsonObject();
            WorkOrders workOrderEntity = createWorkOrderEntity(workOrder);
            addTimelineDetailsToWorkOrder(workOrderEntity, workOrder);
            workOrders.add(workOrderEntity);
        }

        return workOrders;
    }

    /**
     * Creates a WorkOrders entity from JSON data.
     * Maps all work order fields from Dynamics 365 API response to entity properties.
     *
     * @param workOrder JsonObject containing work order data
     * @return WorkOrders entity populated with data
     */
    private WorkOrders createWorkOrderEntity(JsonObject workOrder) {
        WorkOrders workOrderEntity = new WorkOrders();
        workOrderEntity.workOrderID = getJsonValue(workOrder, "msdyn_workorderid");
        workOrderEntity.createDate = formatDate(getJsonValue(workOrder, "createdon"));
        workOrderEntity.systemStatus = getFormattedOptionSetValue(workOrder, "msdyn_systemstatus");
        workOrderEntity.subStatus = getJsonValue(workOrder, "_msdyn_substatus_value");
        workOrderEntity.tripChargeWaived = getJsonValue(workOrder, "gua_waivetripcharge");
        workOrderEntity.wOSummary = getJsonValue(workOrder, "msdyn_workordersummary");
        workOrderEntity.primaryIncidentType = getJsonValue(workOrder, "_msdyn_primaryincidenttype_value");
        workOrderEntity.primaryIncidentCustomerAssetDescription = getJsonValue(workOrder, "msdyn_primaryincidentdescription");
        workOrderEntity.servicePlan = getJsonValue(workOrder, "_gua_serviceplan_value");
        workOrderEntity.actualProblem = getJsonValue(workOrder, "_gua_actualproblemid_value");
        workOrderEntity.actualResolution = getJsonValue(workOrder, "_gua_resolutionid_value");
        workOrderEntity.promiseFrom = formatDate(getJsonValue(workOrder, "msdyn_timefrompromised"));
        workOrderEntity.promiseTo = formatDate(getJsonValue(workOrder, "msdyn_timetopromised"));

        String associatedCaseId = getJsonValue(workOrder, "_msdyn_servicerequest_value");
        workOrderEntity.associatedCase = associatedCaseId;

        return workOrderEntity;
    }

    /**
     * Enriches a work order entity with timeline details (annotations).
     * Fetches annotations for the work order and formats them as a single string.
     *
     * @param workOrderEntity The WorkOrders entity to enrich
     * @param workOrder JsonObject containing work order data with ID
     */
    private void addTimelineDetailsToWorkOrder(WorkOrders workOrderEntity, JsonObject workOrder) {
        String workOrderId = getJsonValue(workOrder, "msdyn_workorderid");
        if (workOrderId != null) {
            LOGGER.debug("Fetching timeline details for work order ID: {}", workOrderId);
            List<Map<String, Object>> timelineDetails = getTimelineDetails(workOrderId);
            if (!timelineDetails.isEmpty()) {
                StringBuilder timelineBuilder = new StringBuilder();
                for (Map<String, Object> note : timelineDetails) {
                    timelineBuilder.append("Subject: ").append(note.get("Subject"))
                            .append(", Note: ").append(note.get("Note"))
                            .append(", Created On: ").append(note.get("Created On"))
                            .append(", Created By: ").append(note.get("Created By"))
                            .append("\n");
                }
                workOrderEntity.timelineDetails = timelineBuilder.toString();
            }
        }
    }

    /**
     * Constructs the final response map for work order queries.
     * Includes work orders list, total count, and date range information.
     *
     * @param workOrders List of WorkOrders entities
     * @param dateRange DateRange object with query date range
     * @return Map containing "Work Orders", "Total Count", and "Date Range Applied"
     */
    private Map<String, Object> buildWorkOrderResponse(List<WorkOrders> workOrders, DateRange dateRange) {
        Map<String, Object> result = new HashMap<>();
        result.put("Work Orders", workOrders);
        result.put("Total Count", workOrders.size());

        Map<String, Object> dateRangeInfo = new HashMap<>();
        dateRangeInfo.put("Start Date", dateRange.startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateRangeInfo.put("End Date", dateRange.endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateRangeInfo.put("Days Covered", String.valueOf(dateRange.daysBetween + 1));

        result.put("Date Range Applied", dateRangeInfo);
        return result;
    }

    /**
     * Retrieves timeline details (annotations) for a work order.
     * Fetches non-document annotations ordered by creation date descending.
     * Returns empty list if retrieval fails.
     *
     * @param workOrderId The work order GUID to retrieve annotations for
     * @return List of maps containing "Subject", "Note", "Created On", and "Created By" for each annotation
     */
    private List<Map<String, Object>> getTimelineDetails(String workOrderId) {
        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("$filter", "_objectid_value eq '" + workOrderId + "' and isdocument eq false");
            queryParams.put("$select", "subject,notetext,createdon,_createdby_value");
            queryParams.put("$orderby", "createdon desc");

            String response = provider.getRestClient().get("annotations", queryParams);
            JsonObject jsonResponse = Constants.GSON.fromJson(response, JsonObject.class);
            JsonArray notesArray = jsonResponse.getAsJsonArray("value");

            List<Map<String, Object>> notes = new ArrayList<>();
            for (JsonElement element : notesArray) {
                JsonObject note = element.getAsJsonObject();
                Map<String, Object> noteMap = new HashMap<>();

                noteMap.put("Subject", getJsonValue(note, "subject"));
                noteMap.put("Note", getJsonValue(note, "notetext"));
                noteMap.put("Created On", formatDate(getJsonValue(note, "createdon")));

                String createdBy = getJsonValue(note, "_createdby_value");
                noteMap.put("Created By", createdBy);

                notes.add(noteMap);
            }
            return notes;
        } catch (Exception e) {
            LOGGER.error("Error occurred while getting timeline details {}", e.getMessage());
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Helper method to get a value from a JsonObject
     */
    private String getJsonValue(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    /**
     * Helper method to format a date string
     */
    private String formatDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        return dateString;
    }

    /**
     * Helper method to get a formatted option set value
     */
    private String getFormattedOptionSetValue(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            int value = obj.get(key).getAsInt();

            if (key.equals("msdyn_systemstatus")) {
                return SystemStatusMapping.getSystemStatusDescription(value);
            }
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * Retrieves incidents (cases) for a specific account within a date range.
     * Defaults to last 90 days if dates not specified. Maximum range is 6 months (183 days).
     * Optionally includes timeline details (annotations) for each incident.
     *
     * @param accountId The account GUID to retrieve incidents for
     * @param startDate Start date in epoch milliseconds (null defaults to 90 days ago)
     * @param endDate End date in epoch milliseconds (null defaults to now)
     * @param includeTimelineDetails "true" to include timeline details, any other value excludes them
     * @return Map containing "Incidents" list, "Total Count", and "Date Range Applied" information
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws IllegalArgumentException if accountId is null/empty or date range exceeds 6 months
     * @throws RuntimeException if incident retrieval fails
     */
    public Map<String, Object> getIncidentsByAccountId(String accountId, Long startDate, Long endDate, String includeTimelineDetails) throws MalformedURLException {
        LOGGER.info("Getting incidents by account ID: {}, startDate: {}, endDate: {}, includeTimelineDetails: {}",
                accountId, startDate, endDate, includeTimelineDetails);
        try {
            validateAccountId(accountId);
            DateRange dateRange = buildDateRange(startDate, endDate);

            Map<String, Object> queryParams = buildIncidentsQuery(accountId, dateRange);
            JsonArray incidentsArray = fetchIncidentsFromApi(queryParams);

            List<Incidents> incidents = processIncidentsData(incidentsArray, includeTimelineDetails);

            return buildIncidentsResponse(incidents, dateRange);
        } catch (Exception cause) {
            LOGGER.error("Error retrieving incidents: {} {}", cause.getMessage(), cause);
            throw new RuntimeException("Unable to retrieve incidents. Verify the account ID exists and you have read permissions. Error: " + cause.getMessage(), cause);
        }
    }

    /**
     * Validates that the account ID is not null or empty for incident queries.
     *
     * @param accountId The account ID to validate
     * @throws IllegalArgumentException if accountId is null or empty
     */
    private void validateAccountId(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            LOGGER.info("Account ID is required");
            throw new IllegalArgumentException("Account ID is required. Please provide a valid account identifier.");
        }
    }

    /**
     * Builds a date range for incident queries with validation.
     * Defaults to last 90 days if dates not provided. Enforces 6-month maximum range.
     *
     * @param startDate Start date in epoch milliseconds (null defaults to 90 days ago)
     * @param endDate End date in epoch milliseconds (null defaults to now)
     * @return DateRange object containing start, end, and days between
     * @throws IllegalArgumentException if date range exceeds 183 days (6 months)
     */
    private DateRange buildDateRange(Long startDate, Long endDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = startDate != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault()) :
                now.minusDays(90);
        LocalDateTime endDateTime = endDate != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), ZoneId.systemDefault()) :
                now;

        long daysBetween = ChronoUnit.DAYS.between(startDateTime, endDateTime);
        LOGGER.debug("Date range: {} to {} ({} days)", startDateTime, endDateTime, daysBetween);

        if (daysBetween > 183) {
            LOGGER.error("Date range exceeds 6 months limit: {} days", daysBetween);
            throw new IllegalArgumentException("Date range cannot exceed 6 months");
        }

        return new DateRange(startDateTime, endDateTime, daysBetween);
    }

    /**
     * Constructs OData query parameters for incident retrieval.
     * Filters by customer account and creation date range.
     *
     * @param accountId The account GUID to filter by
     * @param dateRange The date range to filter by
     * @return Map of OData query parameters with $filter clause
     */
    private Map<String, Object> buildIncidentsQuery(String accountId, DateRange dateRange) {
        DateTimeFormatter oDataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedStartDate = dateRange.startDateTime.format(oDataFormatter);
        String formattedEndDate = dateRange.endDateTime.format(oDataFormatter);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("$filter", "_customerid_value eq '" + accountId +
                "' and createdon ge '" + formattedStartDate +
                "' and createdon le '" + formattedEndDate + "'");

        return queryParams;
    }

    /**
     * Fetches incidents from Dynamics 365 API using provided query parameters.
     *
     * @param queryParams OData query parameters for filtering
     * @return JsonArray containing incident data
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    private JsonArray fetchIncidentsFromApi(Map<String, Object> queryParams) throws MalformedURLException {
        String response = provider.getRestClient().get("incidents", queryParams);
        JsonObject jsonResponse = Constants.GSON.fromJson(response, JsonObject.class);
        return jsonResponse.getAsJsonArray("value");
    }

    /**
     * Processes raw incident JSON data into Incidents entity objects.
     * Optionally enriches each incident with timeline details based on includeTimelineDetails parameter.
     *
     * @param incidentsArray JsonArray of incident data from API
     * @param includeTimelineDetails "true" to include timeline details, any other value excludes them
     * @return List of Incidents entities, optionally with timeline details
     */
    private List<Incidents> processIncidentsData(JsonArray incidentsArray, String includeTimelineDetails) {
        List<Incidents> incidents = new ArrayList<>();

        for (JsonElement element : incidentsArray) {
            JsonObject incident = element.getAsJsonObject();
            Incidents incidentEntity = createIncidentEntity(incident);

            if (Boolean.parseBoolean(includeTimelineDetails) || "true".equalsIgnoreCase(includeTimelineDetails)) {
                addTimelineDetailsToIncident(incidentEntity, incident);
            }

            incidents.add(incidentEntity);
        }

        return incidents;
    }

    /**
     * Creates an Incidents entity from JSON data.
     * Maps all incident fields from Dynamics 365 API response to entity properties.
     * Handles expanded navigation properties for incident type, actual problem, and resolution.
     *
     * @param incident JsonObject containing incident data
     * @return Incidents entity populated with data
     */
    private Incidents createIncidentEntity(JsonObject incident) {
        Incidents incidentEntity = new Incidents();
        incidentEntity.incidentID = getJsonValue(incident, "incidentid");
        incidentEntity.createDate = formatDate(getJsonValue(incident, "createdon"));
        incidentEntity.caseTitle = getJsonValue(incident, "title");
        incidentEntity.description = getJsonValue(incident, "description");

        // Get primary incident type name if available
        if (incident.has("msdyn_incidenttype") && !incident.get("msdyn_incidenttype").isJsonNull()) {
            JsonObject incidentType = incident.getAsJsonObject("msdyn_incidenttype");
            incidentEntity.primaryIncident = getJsonValue(incidentType, "msdyn_name");
        } else {
            incidentEntity.primaryIncident = getJsonValue(incident, "_msdyn_incidenttype_value");
        }

        incidentEntity.equipmentSystemDescription = getJsonValue(incident, "_gua_equipment_value");
        incidentEntity.servicePlan = getJsonValue(incident, "_gua_serviceplan_value");

        // Get actual problem name if available
        if (incident.has("rsm_actualproblem") && !incident.get("rsm_actualproblem").isJsonNull()) {
            JsonObject actualProblem = incident.getAsJsonObject("rsm_actualproblem");
            incidentEntity.actualProblem = getJsonValue(actualProblem, "rsm_name");
        } else {
            incidentEntity.actualProblem = getJsonValue(incident, "_rsm_actualproblem_value");
        }

        // Get resolution name if available
        if (incident.has("rsm_resolutionlu") && !incident.get("rsm_resolutionlu").isJsonNull()) {
            JsonObject resolution = incident.getAsJsonObject("rsm_resolutionlu");
            incidentEntity.resolution = getJsonValue(resolution, "rsm_name");
        } else {
            incidentEntity.resolution = getJsonValue(incident, "_rsm_resolutionlu_value");
        }

        return incidentEntity;
    }

    /**
     * Enriches an incident entity with timeline details (annotations).
     * Fetches annotations for the incident and formats them as a single string.
     *
     * @param incidentEntity The Incidents entity to enrich
     * @param incident JsonObject containing incident data with ID
     */
    private void addTimelineDetailsToIncident(Incidents incidentEntity, JsonObject incident) {
        String incidentId = getJsonValue(incident, "incidentid");
        if (incidentId != null) {
            List<Map<String, Object>> timelineDetails = getIncidentTimelineDetails(incidentId);
            if (!timelineDetails.isEmpty()) {
                StringBuilder timelineBuilder = new StringBuilder();
                for (Map<String, Object> note : timelineDetails) {
                    timelineBuilder.append("Subject: ").append(note.get("Subject"))
                            .append(", Note: ").append(note.get("Note"))
                            .append(", Created On: ").append(note.get("Created On"))
                            .append(", Created By: ").append(note.get("Created By"))
                            .append("\n");
                }
                incidentEntity.timelineDetails = timelineBuilder.toString();
            }
        }
    }

    /**
     * Constructs the final response map for incident queries.
     * Includes incidents list, total count, and date range information.
     *
     * @param incidents List of Incidents entities
     * @param dateRange DateRange object with query date range
     * @return Map containing "Incidents", "Total Count", and "Date Range Applied"
     */
    private Map<String, Object> buildIncidentsResponse(List<Incidents> incidents, DateRange dateRange) {
        Map<String, Object> result = new HashMap<>();
        result.put("Incidents", incidents);
        result.put("Total Count", incidents.size());

        Map<String, Object> dateRangeInfo = new HashMap<>();
        dateRangeInfo.put("Start Date", dateRange.startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateRangeInfo.put("End Date", dateRange.endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateRangeInfo.put("Days Covered", String.valueOf(dateRange.daysBetween + 1));

        result.put("Date Range Applied", dateRangeInfo);

        LOGGER.info("Successfully retrieved {} incidents for account ID within date range", incidents.size());
        return result;
    }

    /**
     * Retrieves timeline details (annotations) for an incident.
     * Fetches non-document annotations ordered by creation date descending.
     * Returns empty list if retrieval fails.
     *
     * @param incidentId The incident GUID to retrieve annotations for
     * @return List of maps containing "Subject", "Note", "Created On", and "Created By" for each annotation
     */
    private List<Map<String, Object>> getIncidentTimelineDetails(String incidentId) {
        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("$filter", "_objectid_value eq '" + incidentId + "' and isdocument eq false");
            queryParams.put("$select", "subject,notetext,createdon,_createdby_value");
            queryParams.put("$orderby", "createdon desc");

            String response = provider.getRestClient().get("annotations", queryParams);
            JsonObject jsonResponse = Constants.GSON.fromJson(response, JsonObject.class);
            JsonArray notesArray = jsonResponse.getAsJsonArray("value");

            List<Map<String, Object>> notes = new ArrayList<>();
            for (JsonElement element : notesArray) {
                JsonObject note = element.getAsJsonObject();
                Map<String, Object> noteMap = new HashMap<>();

                noteMap.put("Subject", getJsonValue(note, "subject"));
                noteMap.put("Note", getJsonValue(note, "notetext"));
                noteMap.put("Created On", formatDate(getJsonValue(note, "createdon")));
                noteMap.put("Created By", getJsonValue(note, "_createdby_value"));

                notes.add(noteMap);
            }

            return notes;
        } catch (Exception cause) {
            LOGGER.error("Error retrieving incident timeline details: {} {} ", cause.getMessage(), cause);
            return new ArrayList<>();
        }
    }

    private static class DateRange {
        final LocalDateTime startDateTime;
        final LocalDateTime endDateTime;
        final long daysBetween;

        DateRange(LocalDateTime startDateTime, LocalDateTime endDateTime, long daysBetween) {
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.daysBetween = daysBetween;
        }
    }
}