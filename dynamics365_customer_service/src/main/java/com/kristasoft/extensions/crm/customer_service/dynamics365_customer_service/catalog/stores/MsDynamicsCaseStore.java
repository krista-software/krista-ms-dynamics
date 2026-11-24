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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.stores;

import app.krista.extension.executor.QueryClause;
import app.krista.extension.executor.SearchCondition;
import app.krista.extension.executor.SearchQuery;
import app.krista.extension.util.EntityStore;
import com.google.gson.JsonSyntaxException;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.CatalogTypes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsCase;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.CaseDaoImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.CaseImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsCase;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Case;
import org.codehaus.jettison.json.JSONException;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.*;

@Service
public class MsDynamicsCaseStore implements EntityStore<MsDynamicsCase> {

    public static final String CASE_TITLE = "Case Title";
    public static final String CASE_NUMBER = "Case Number";
    public static final String CASE_ID = "Case Id";
    public static final String ACCOUNTID = "accountId";
    public static final String TITLE = "title";
    public static final String TICKETNUMBER = "ticketnumber";
    public static final String INCIDENTID = "incidentid";
    public static final String CUSTOMERID_VALUE = "_customerid_value";
    public static final String OWNERID_VALUE = "_ownerid_value";
    public static final String CUSTOMER_ID = "Customer Id";
    private static final String QUERY_FIELDS = "title, ticketnumber, incidentid, _customerid_value, _subjectid_value," +
            "_productid_value, description, statuscode, prioritycode";
    private final CaseDaoImpl caseDao;

    @Inject
    private MsDynamicsCaseStore(CaseDaoImpl caseDao) {
        this.caseDao = caseDao;
    }

    /**
     * Creates a new case in Dynamics 365.
     * Validates entity definition and delegates to CaseDaoImpl for creation.
     *
     * @param entityDefinition Map containing case field values
     * @return MsDynamicsCase entity representing the created case
     * @throws IOException if API communication fails
     * @throws IllegalArgumentException if entity definition is null or empty
     * @throws IllegalStateException if case data format is invalid
     */
    @Override
    public MsDynamicsCase create(Map<String, Object> entityDefinition) throws IOException {
        if (entityDefinition == null || entityDefinition.isEmpty()) {
            throw new IllegalArgumentException(Constants.FAILED_TO_CREATE + Constants.EMPTY_ENTITY);
        }
        try {
            Case createdCase = new CaseImpl(
                    Constants.GSON.fromJson(caseDao.createCase(entityDefinition), DynamicsCase.class)
            );
            return CatalogTypes.fromDynamicsToKristaEntity(createdCase);
        } catch (JsonSyntaxException | JSONException cause) {
            throw new IllegalStateException("Invalid case data format. Verify all field values match the expected data types and structure.", cause);
        }
    }

    /**
     * Retrieves a case from Dynamics 365 by its unique identifier.
     *
     * @param primaryKey The case GUID (incidentid)
     * @return MsDynamicsCase entity representing the case
     * @throws IOException if API communication fails
     * @throws IllegalArgumentException if primary key is null or blank
     * @throws IllegalStateException if case ID format is invalid
     */
    @Override
    public MsDynamicsCase get(String primaryKey) throws IOException {
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalArgumentException(Constants.PRIMARY_KEY_IS_EMPTY);
        }
        try {
            return CatalogTypes.fromDynamicsToKristaEntity(caseDao.getCaseById(primaryKey));
        } catch (MalformedURLException cause) {
            throw new IllegalStateException("Invalid case ID format. Verify the case ID is a valid GUID.", cause);
        }
    }

    /**
     * Updates an existing case in Dynamics 365.
     * Compares old and new states, sends only changed fields.
     *
     * @param dynamicsCase MsDynamicsCase entity with updated field values
     * @return MsDynamicsCase entity representing the updated case
     * @throws IllegalArgumentException if dynamicsCase is null
     * @throws RuntimeException if case update fails or case ID doesn't exist
     */
    @Override
    public MsDynamicsCase update(MsDynamicsCase dynamicsCase) {
        if (dynamicsCase == null) {
            throw new IllegalArgumentException(Constants.FAILED_TO_UPDATE + Constants.EMPTY_ENTITY);
        }
        try {
            return CatalogTypes.fromDynamicsToKristaEntity(caseDao.updateCase(
                    new CaseImpl(DynamicsCase.from(dynamicsCase))
            ));
        } catch (IllegalAccessException | MalformedURLException cause) {
            throw new RuntimeException("Invalid case update data. Verify the case ID exists and all field values are valid.", cause);
        }
    }

    /**
     * Deletes a case from Dynamics 365 by its unique identifier.
     *
     * @param primaryKey The case GUID (incidentid) to delete
     * @throws IllegalArgumentException if primary key is null or blank
     * @throws IllegalStateException if case ID format is invalid
     */
    @Override
    public void delete(String primaryKey) {
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalArgumentException(Constants.FAILED_TO_DELETE + Constants.PRIMARY_KEY_IS_EMPTY);
        }
        try {
            caseDao.deleteCaseById(primaryKey);
        } catch (MalformedURLException cause) {
            throw new IllegalStateException("Invalid case ID format. Verify the case ID is a valid GUID.", cause);
        }

    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<MsDynamicsCase> search(List<SearchCondition> list, long l, int i) {
        return Collections.emptyList();
    }

    /**
     * Searches for cases in Dynamics 365 based on search conditions and query clauses.
     * Supports filtering by case title, case number, case ID, and account ID.
     * Supports ordering and pagination.
     *
     * @param searchQuery SearchQuery containing search conditions and query clauses
     * @param pageIndex Page number (1-based) for pagination
     * @param pageSize Number of results per page
     * @return List of MsDynamicsCase entities matching the search criteria
     * @throws IllegalArgumentException if search conditions are null or empty
     * @throws RuntimeException if API communication fails
     */
    @Override
    public List<MsDynamicsCase> search(SearchQuery searchQuery, long pageIndex, int pageSize) {
        final List<SearchCondition> conditions = getSearchConditions(searchQuery);
        final List<QueryClause> queryClauses = getQueryClauses(searchQuery);
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Failed to get Cases. " + Constants.CONDITIONS_NOT_FOUND);
        }
        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put(Constants.SELECT, QUERY_FIELDS);
            queryParams.put(Constants.ORDER_BY, getOrderByQueryParams(queryClauses));

            if (conditions.size() != 1) {
                String filterQuery = getFilterByQueryParams(conditions);
                if (!filterQuery.isBlank()) {
                    queryParams.put(Constants.FILTER, filterQuery);
                }
            }
            return getFilteredCases(pageIndex, pageSize, queryParams);
        } catch (MalformedURLException cause) {
            throw new RuntimeException(cause.getMessage(), cause.getCause());
        }
    }

    /**
     * Retrieves filtered cases from Dynamics 365 and applies pagination.
     *
     * @param pageIndex Page number (1-based) for pagination
     * @param pageSize Number of results per page
     * @param queryParams OData query parameters ($select, $filter, $orderby)
     * @return List of MsDynamicsCase entities for the requested page
     * @throws MalformedURLException if API endpoint URL is invalid
     */
    private List<MsDynamicsCase> getFilteredCases(long pageIndex, int pageSize, Map<String, Object> queryParams) throws MalformedURLException {
        List<Case> allCases = caseDao.getCasesByQuery(queryParams);
        return allCases.stream().skip(Math.max((pageIndex - 1) * pageSize, 0)).limit(pageSize)
                .map(CatalogTypes::fromDynamicsToKristaEntity).collect(Collectors.toList());

    }

    /**
     * Builds OData $filter query parameter from search conditions.
     * Supports filtering by case title, case number, case ID, and account ID.
     *
     * @param conditions List of SearchCondition objects
     * @return OData filter string (e.g., "contains(title, 'test') and incidentid eq guid")
     */
    private String getFilterByQueryParams(List<SearchCondition> conditions) {
        String caseTitle = getSearchConditionValue(conditions, CASE_TITLE);
        String caseNumber = getSearchConditionValue(conditions, CASE_NUMBER);
        String caseId = getSearchConditionValue(conditions, CASE_ID);
        String accountId = getSearchConditionValue(conditions, ACCOUNTID);
        String caseTitleOperator = getSearchConditionStringOperator(conditions, CASE_TITLE);
        String caseNumberOperator = getSearchConditionStringOperator(conditions, CASE_NUMBER);

        List<String> filters = new ArrayList<>();

        if (caseTitle != null && !caseTitle.isEmpty()) {
            filters.add(caseTitleOperator + "(title, '" + caseTitle + "')");
        }

        if (caseNumber != null && !caseNumber.isEmpty()) {
            filters.add(caseNumberOperator + "(ticketnumber, '" + caseNumber + "')");
        }

        if (caseId != null && !caseId.isEmpty()) {
            filters.add("incidentid eq " + caseId);
        }

        if (accountId != null && !accountId.isEmpty()) {
            filters.add("_accountid_value eq " + accountId);
        }

        return String.join(" and ", filters);
    }

    /**
     * Builds OData $orderby query parameter from query clauses.
     * Defaults to "ticketnumber DESC" if no clauses provided.
     *
     * @param queryClauses List of QueryClause objects specifying sort order
     * @return OData orderby string (e.g., "ticketnumber desc")
     */
    private String getOrderByQueryParams(List<QueryClause> queryClauses) {
        String orderByField = queryClauses.isEmpty() ? CASE_NUMBER : queryClauses.get(0).getFieldName();
        String orderByOperand = queryClauses.isEmpty() ? "DESC" : queryClauses.get(0).getOperand().toString();
        return getSupportedParameterName(orderByField) + " " + orderByOperand.toLowerCase();
    }

    /**
     * Maps catalog field names to Dynamics 365 API field names for ordering.
     *
     * @param orderByField Catalog field name (e.g., "Case Number", "Case Title")
     * @return Dynamics 365 API field name (e.g., "ticketnumber", "title")
     */
    private String getSupportedParameterName(String orderByField) {
        if (orderByField.equalsIgnoreCase(CASE_NUMBER)) {
            return TICKETNUMBER;
        } else if (orderByField.equalsIgnoreCase(CASE_TITLE)) {
            return TITLE;
        } else if (orderByField.equalsIgnoreCase(CASE_ID)) {
            return INCIDENTID;
        } else if (orderByField.equalsIgnoreCase(CUSTOMER_ID)) {
            return CUSTOMERID_VALUE;
        }
        return OWNERID_VALUE;
    }

    @Override
    public long count(List<SearchCondition> list) {
        return 0;
    }

    @Override
    public List<String> lookup(Map<String, Object> map, List<String> list) {
        return Collections.emptyList();
    }
}
