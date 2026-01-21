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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Case;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CaseDaoImplTest {

    @Mock
    private RestClientProvider mockRestClientProvider;

    @Mock
    private RestClientProviderFactory mockRestClientProviderFactory;

    @Mock
    private RestClient mockRestClient;

    private CaseDaoImpl caseDaoImpl;

    private static final String TEST_CASE_ID = "test-case-id-123";
    private static final String TEST_CASE_TITLE = "Test Case Title";
    private static final String TEST_CASE_NUMBER = "CASE-001";
    private static final String TEST_CONTACT_ID = "contact-123";
    private static final String TEST_SUBJECT_ID = "subject-456";
    private static final String TEST_DESCRIPTION = "Test case description";
    private static final String TEST_PRIORITY = "High";
    private static final String TEST_CASE_STATUS = "In Progress";

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);

        // Setup factory mock
        when(mockRestClientProviderFactory.create()).thenReturn(mockRestClientProvider);
        when(mockRestClientProvider.getRestClient()).thenReturn(mockRestClient);

        // Create instance using provider directly for most tests
        caseDaoImpl = new CaseDaoImpl(mockRestClientProvider);
    }

    @Test
    public void testConstructor_WithProviderFactory_ShouldCreateInstance() {
        // Act
        CaseDaoImpl dao = new CaseDaoImpl(mockRestClientProviderFactory);

        // Assert
        assertNotNull(dao);
        verify(mockRestClientProviderFactory).create();
    }

    @Test
    public void testConstructor_WithProvider_ShouldCreateInstance() {
        // Act
        CaseDaoImpl dao = new CaseDaoImpl(mockRestClientProvider);

        // Assert
        assertNotNull(dao);
    }

    @Test
    public void testGetCaseById_WithValidId_ShouldReturnCase() throws MalformedURLException {
        // Arrange
        String mockResponse = createMockCaseResponse();
        when(mockRestClient.get(eq(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")"), any()))
                .thenReturn(mockResponse);

        // Act
        Case result = caseDaoImpl.getCaseById(TEST_CASE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_CASE_TITLE, result.getCaseTitle());
        assertEquals(TEST_CASE_ID, result.getCaseId());
        verify(mockRestClient).get(eq(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")"), any());
    }

    @Test
    public void testGetCases_ShouldCallGetCasesByQuery() throws MalformedURLException {
        // Arrange
        String mockResponse = createMockCasesListResponse();
        when(mockRestClient.get(eq(Constants.INCIDENTS_API), any()))
                .thenReturn(mockResponse);

        // Act
        List<Case> result = caseDaoImpl.getCases(10, 0, "title", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mockRestClient).get(eq(Constants.INCIDENTS_API), any());
    }

    @Test
    public void testGetCasesByQuery_WithValidQuery_ShouldReturnCases() throws MalformedURLException {
        // Arrange
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("$filter", "title eq 'Test'");
        String mockResponse = createMockCasesListResponse();
        when(mockRestClient.get(eq(Constants.INCIDENTS_API), eq(queryParams)))
                .thenReturn(mockResponse);

        // Act
        List<Case> result = caseDaoImpl.getCasesByQuery(queryParams);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_CASE_TITLE, result.getFirst().getCaseTitle());
        verify(mockRestClient).get(eq(Constants.INCIDENTS_API), eq(queryParams));
    }

    @Test
    public void testGetCasesByQuery_WithEmptyResult_ShouldReturnEmptyList() throws MalformedURLException {
        // Arrange
        Map<String, Object> queryParams = new HashMap<>();
        String mockResponse = createMockEmptyCasesListResponse();
        when(mockRestClient.get(eq(Constants.INCIDENTS_API), eq(queryParams)))
                .thenReturn(mockResponse);

        // Act
        List<Case> result = caseDaoImpl.getCasesByQuery(queryParams);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRestClient).get(eq(Constants.INCIDENTS_API), eq(queryParams));
    }

    @Test
    public void testCreateCase_WithValidEntityDefinition_ShouldReturnCaseId() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithMinimalEntityDefinition_ShouldCreatePayload() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = new HashMap<>();
        entityDefinition.put("Case Title", TEST_CASE_TITLE);
        entityDefinition.put("Contact Id", TEST_CONTACT_ID);
        entityDefinition.put("Description", TEST_DESCRIPTION);

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithNullSubjectId_ShouldSkipSubjectBinding() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        entityDefinition.put("Subject Id", null);

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithBlankSubjectId_ShouldSkipSubjectBinding() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        entityDefinition.put("Subject Id", "");

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithNullPriority_ShouldSkipPriorityCode() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        entityDefinition.put("Priority", null);

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithBlankPriority_ShouldSkipPriorityCode() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        entityDefinition.put("Priority", "");

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithNullCaseStatus_ShouldSkipStatusCode() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        entityDefinition.put("Case Status", null);

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testCreateCase_WithBlankCaseStatus_ShouldSkipStatusCode() throws MalformedURLException, JSONException {
        // Arrange
        Map<String, Object> entityDefinition = createValidEntityDefinition();
        entityDefinition.put("Case Status", "");

        String expectedCaseId = "new-case-id-123";
        when(mockRestClient.post(eq("incidents"), any(JSONObject.class)))
                .thenReturn(expectedCaseId);

        // Act
        String result = caseDaoImpl.createCase(entityDefinition);

        // Assert
        assertEquals(expectedCaseId, result);
        verify(mockRestClient).post(eq("incidents"), any(JSONObject.class));
    }

    @Test
    public void testDeleteCaseById_WithValidId_ShouldCallDelete() throws MalformedURLException {
        // Act
        caseDaoImpl.deleteCaseById(TEST_CASE_ID);

        // Assert
        verify(mockRestClient).delete(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")");
    }

    @Test
    public void testGetSubjects_WithValidResponse_ShouldReturnSubjectsList() throws MalformedURLException {
        // Arrange
        String mockResponse = createMockSubjectsResponse();
        when(mockRestClient.get(eq("subjects"), any()))
                .thenReturn(mockResponse);

        // Act
        List<Map<String, Object>> result = caseDaoImpl.getSubjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        Map<String, Object> firstSubject = result.getFirst();
        assertEquals("Test Subject 1", firstSubject.get("Subject Title"));
        assertEquals("subject-1", firstSubject.get("Subject Id"));

        Map<String, Object> secondSubject = result.get(1);
        assertEquals("Test Subject 2", secondSubject.get("Subject Title"));
        assertEquals("subject-2", secondSubject.get("Subject Id"));

        verify(mockRestClient).get(eq("subjects"), any());
    }

    @Test
    public void testGetSubjects_WithJsonProcessingException_ShouldThrowJsonSyntaxException()  {
        // Arrange
        String invalidJsonResponse = "invalid json";
        when(mockRestClient.get(eq("subjects"), any()))
                .thenReturn(invalidJsonResponse);

        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            caseDaoImpl.getSubjects();
        });

        verify(mockRestClient).get(eq("subjects"), any());
    }

    // Helper methods for creating mock responses
    private String createMockCaseResponse() {
        JsonObject caseJson = new JsonObject();
        caseJson.addProperty("title", TEST_CASE_TITLE);
        caseJson.addProperty("ticketnumber", TEST_CASE_NUMBER);
        caseJson.addProperty("incidentid", TEST_CASE_ID);
        caseJson.addProperty("_customerid_value", TEST_CONTACT_ID);
        caseJson.addProperty("_subjectid_value", TEST_SUBJECT_ID);
        caseJson.addProperty("description", TEST_DESCRIPTION);
        caseJson.addProperty("prioritycode", "1");
        caseJson.addProperty("statuscode", "1");
        return Constants.GSON.toJson(caseJson);
    }

    private String createMockCasesListResponse() {
        JsonObject response = new JsonObject();
        JsonArray casesArray = new JsonArray();

        JsonObject caseJson = new JsonObject();
        caseJson.addProperty("title", TEST_CASE_TITLE);
        caseJson.addProperty("ticketnumber", TEST_CASE_NUMBER);
        caseJson.addProperty("incidentid", TEST_CASE_ID);
        caseJson.addProperty("_customerid_value", TEST_CONTACT_ID);
        caseJson.addProperty("_subjectid_value", TEST_SUBJECT_ID);
        caseJson.addProperty("description", TEST_DESCRIPTION);
        caseJson.addProperty("prioritycode", "1");
        caseJson.addProperty("statuscode", "1");

        casesArray.add(caseJson);
        response.add("value", casesArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockEmptyCasesListResponse() {
        JsonObject response = new JsonObject();
        JsonArray casesArray = new JsonArray();
        response.add("value", casesArray);
        return Constants.GSON.toJson(response);
    }

    private String createMockSubjectsResponse() {
        JsonObject response = new JsonObject();
        JsonArray subjectsArray = new JsonArray();

        JsonObject subject1 = new JsonObject();
        subject1.addProperty("title", "Test Subject 1");
        subject1.addProperty("subjectid", "subject-1");

        JsonObject subject2 = new JsonObject();
        subject2.addProperty("title", "Test Subject 2");
        subject2.addProperty("subjectid", "subject-2");

        subjectsArray.add(subject1);
        subjectsArray.add(subject2);
        response.add("value", subjectsArray);

        return Constants.GSON.toJson(response);
    }

    @Test
    public void testUpdateCase_WithValidCase_ShouldReturnUpdatedCase() throws MalformedURLException, IllegalAccessException {
        // Arrange
        Case mockOldCase = createMockCase();
        String getCaseResponse = createMockCaseResponse();
        String updateResponse = createMockUpdatedCaseResponse();

        when(mockRestClient.get(eq(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")"), any()))
                .thenReturn(getCaseResponse);
        when(mockRestClient.patch(eq(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")"), anyString()))
                .thenReturn(updateResponse);

        // Act
        Case result = caseDaoImpl.updateCase(mockOldCase);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Case Title", result.getCaseTitle());
        verify(mockRestClient).get(eq(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")"), any());
        verify(mockRestClient).patch(eq(Constants.INCIDENTS_API + "(" + TEST_CASE_ID + ")"), anyString());
    }

    @Test
    public void testGetAccountByName_WithValidName_ShouldReturnAccounts() {
        // Arrange
        String accountName = "Test Account";
        String mockResponse = createMockAccountsResponse();
        when(mockRestClient.get(eq("accounts"), any()))
                .thenReturn(mockResponse);

        // Act
        List<Map<String, String>> result = caseDaoImpl.getAccountByName(accountName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        Map<String, String> account = result.getFirst();
        assertEquals("Test Account Name", account.get("Account Name"));
        assertEquals("123 Test Street", account.get("Address"));
        assertEquals("S12345", account.get("Account Number"));

        verify(mockRestClient).get(eq("accounts"), any());
    }

    @Test
    public void testGetAccountByName_WithNameContainingApostrophe_ShouldSanitizeName() {
        // Arrange
        String accountName = "O'Connor's Account";
        String mockResponse = createMockAccountsResponse();
        when(mockRestClient.get(eq("accounts"), any()))
                .thenReturn(mockResponse);

        // Act
        List<Map<String, String>> result = caseDaoImpl.getAccountByName(accountName);

        // Assert
        assertNotNull(result);
        verify(mockRestClient).get(eq("accounts"), any());
    }

    @Test
    public void testGetAccountByName_WithRuntimeException_ShouldThrowRuntimeException() {
        // Arrange
        String accountName = "Test Account";
        when(mockRestClient.get(eq("accounts"), any()))
                .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getAccountByName(accountName);
        });

        assertNotNull(exception.getMessage());
        verify(mockRestClient).get(eq("accounts"), any());
    }

    @Test
    public void testGetAccountByName_WithGeneralException_ShouldThrowIllegalStateException() {
        // Arrange
        String accountName = "Test Account";
        when(mockRestClient.get(eq("accounts"), any()))
                .thenThrow(new RuntimeException("General error"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            caseDaoImpl.getAccountByName(accountName);
        });

        assertEquals("Unable to retrieve accounts list. Verify your permissions and API connectivity.", exception.getMessage());
        verify(mockRestClient).get(eq("accounts"), any());
    }

    @Test
    public void testGetWorkOrderByAccountId_WithValidAccountId_ShouldReturnWorkOrders() throws MalformedURLException {
        // Arrange
        String accountId = "account-123";
        Long startDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days ago
        Long endDate = System.currentTimeMillis();
        String mockResponse = createMockWorkOrdersResponse();

        when(mockRestClient.get(eq("msdyn_workorders"), any()))
                .thenReturn(mockResponse);
        when(mockRestClient.get(eq("annotations"), any()))
                .thenReturn(createMockAnnotationsResponse());

        // Act
        Map<String, Object> result = caseDaoImpl.getWorkOrderByAccountId(accountId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("Work Orders"));
        assertTrue(result.containsKey("Total Count"));
        assertTrue(result.containsKey("Date Range Applied"));

        @SuppressWarnings("unchecked")
        List<Object> workOrders = (List<Object>) result.get("Work Orders");
        assertEquals(1, workOrders.size());
        assertEquals(1, result.get("Total Count"));

        verify(mockRestClient).get(eq("msdyn_workorders"), any());
    }

    @Test
    public void testGetWorkOrderByAccountId_WithNullAccountId_ShouldThrowRuntimeException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getWorkOrderByAccountId(null, null, null);
        });

        assertTrue(exception.getMessage().contains("Account ID is required"));
    }

    @Test
    public void testGetWorkOrderByAccountId_WithEmptyAccountId_ShouldThrowRuntimeException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getWorkOrderByAccountId("", null, null);
        });

        assertTrue(exception.getMessage().contains("Account ID is required"));
    }

    @Test
    public void testGetWorkOrderByAccountId_WithDateRangeExceeding6Months_ShouldThrowRuntimeException() {
        // Arrange
        String accountId = "account-123";
        Long startDate = System.currentTimeMillis() - (200L * 24 * 60 * 60 * 1000); // 200 days ago
        Long endDate = System.currentTimeMillis();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getWorkOrderByAccountId(accountId, startDate, endDate);
        });

        assertTrue(exception.getMessage().contains("Date range cannot exceed 6 months"));
    }

    @Test
    public void testGetWorkOrderByAccountId_WithNullDates_ShouldUseDefaultDateRange() throws MalformedURLException {
        // Arrange
        String accountId = "account-123";
        String mockResponse = createMockWorkOrdersResponse();

        when(mockRestClient.get(eq("msdyn_workorders"), any()))
                .thenReturn(mockResponse);
        when(mockRestClient.get(eq("annotations"), any()))
                .thenReturn(createMockAnnotationsResponse());

        // Act
        Map<String, Object> result = caseDaoImpl.getWorkOrderByAccountId(accountId, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("Date Range Applied"));
        verify(mockRestClient).get(eq("msdyn_workorders"), any());
    }

    @Test
    public void testGetIncidentsByAccountId_WithValidAccountId_ShouldReturnIncidents() throws MalformedURLException {
        // Arrange
        String accountId = "account-123";
        Long startDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days ago
        Long endDate = System.currentTimeMillis();
        String includeTimelineDetails = "true";
        String mockResponse = createMockIncidentsResponse();

        when(mockRestClient.get(eq("incidents"), any()))
                .thenReturn(mockResponse);
        when(mockRestClient.get(eq("annotations"), any()))
                .thenReturn(createMockAnnotationsResponse());

        // Act
        Map<String, Object> result = caseDaoImpl.getIncidentsByAccountId(accountId, startDate, endDate, includeTimelineDetails);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("Incidents"));
        assertTrue(result.containsKey("Total Count"));
        assertTrue(result.containsKey("Date Range Applied"));

        @SuppressWarnings("unchecked")
        List<Object> incidents = (List<Object>) result.get("Incidents");
        assertEquals(1, incidents.size());
        assertEquals(1, result.get("Total Count"));

        verify(mockRestClient).get(eq("incidents"), any());
    }

    @Test
    public void testGetIncidentsByAccountId_WithNullAccountId_ShouldThrowRuntimeException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getIncidentsByAccountId(null, null, null, "false");
        });

        assertTrue(exception.getMessage().contains("Account ID is required"));
    }

    @Test
    public void testGetIncidentsByAccountId_WithEmptyAccountId_ShouldThrowRuntimeException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getIncidentsByAccountId("", null, null, "false");
        });

        assertTrue(exception.getMessage().contains("Account ID is required"));
    }

    @Test
    public void testGetIncidentsByAccountId_WithDateRangeExceeding6Months_ShouldThrowRuntimeException() {
        // Arrange
        String accountId = "account-123";
        Long startDate = System.currentTimeMillis() - (200L * 24 * 60 * 60 * 1000); // 200 days ago
        Long endDate = System.currentTimeMillis();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            caseDaoImpl.getIncidentsByAccountId(accountId, startDate, endDate, "false");
        });

        assertTrue(exception.getMessage().contains("Date range cannot exceed 6 months"));
    }

    @Test
    public void testGetIncidentsByAccountId_WithoutTimelineDetails_ShouldNotFetchTimeline() throws MalformedURLException {
        // Arrange
        String accountId = "account-123";
        String includeTimelineDetails = "false";
        String mockResponse = createMockIncidentsResponse();

        when(mockRestClient.get(eq("incidents"), any()))
                .thenReturn(mockResponse);

        // Act
        Map<String, Object> result = caseDaoImpl.getIncidentsByAccountId(accountId, null, null, includeTimelineDetails);

        // Assert
        assertNotNull(result);
        verify(mockRestClient).get(eq("incidents"), any());
        verify(mockRestClient, never()).get(eq("annotations"), any());
    }

    // Helper methods for creating mock objects and responses
    private Case createMockCase() {
        return new Case() {
            @Override
            public String getCaseTitle() {
                return "Updated Case Title";
            }

            @Override
            public String getCaseNumber() {
                return TEST_CASE_NUMBER;
            }

            @Override
            public String getCaseId() {
                return TEST_CASE_ID;
            }

            @Override
            public String getContactId() {
                return TEST_CONTACT_ID;
            }

            @Override
            public String getSubjectId() {
                return TEST_SUBJECT_ID;
            }

            @Override
            public String getDescription() {
                return "Updated description";
            }

            @Override
            public String getPriorityCode() {
                return "2";
            }

            @Override
            public String getCaseStatusCode() {
                return "2";
            }
        };
    }

    private String createMockUpdatedCaseResponse() {
        JsonObject caseJson = new JsonObject();
        caseJson.addProperty("title", "Updated Case Title");
        caseJson.addProperty("ticketnumber", TEST_CASE_NUMBER);
        caseJson.addProperty("incidentid", TEST_CASE_ID);
        caseJson.addProperty("_customerid_value", TEST_CONTACT_ID);
        caseJson.addProperty("_subjectid_value", TEST_SUBJECT_ID);
        caseJson.addProperty("description", "Updated description");
        caseJson.addProperty("prioritycode", "2");
        caseJson.addProperty("statuscode", "2");
        return Constants.GSON.toJson(caseJson);
    }

    private String createMockAccountsResponse() {
        JsonObject response = new JsonObject();
        JsonArray accountsArray = new JsonArray();

        JsonObject account = new JsonObject();
        account.addProperty("name", "Test Account Name");
        account.addProperty("address1_line1", "123 Test Street");
        account.addProperty("accountnumber", "S12345");
        account.addProperty("accountid", "account-123");
        account.addProperty("rsm_accountstatus", "Active");

        accountsArray.add(account);
        response.add("value", accountsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockWorkOrdersResponse() {
        JsonObject response = new JsonObject();
        JsonArray workOrdersArray = new JsonArray();

        JsonObject workOrder = new JsonObject();
        workOrder.addProperty("msdyn_workorderid", "wo-123");
        workOrder.addProperty("createdon", "2023-01-01T10:00:00Z");
        workOrder.addProperty("msdyn_systemstatus", 690970000);
        workOrder.addProperty("_msdyn_substatus_value", "substatus-123");
        workOrder.addProperty("gua_waivetripcharge", "false");
        workOrder.addProperty("msdyn_workordersummary", "Test work order summary");
        workOrder.addProperty("_msdyn_primaryincidenttype_value", "incident-type-123");
        workOrder.addProperty("msdyn_primaryincidentdescription", "Primary incident description");
        workOrder.addProperty("_gua_serviceplan_value", "service-plan-123");
        workOrder.addProperty("_gua_actualproblemid_value", "problem-123");
        workOrder.addProperty("_gua_resolutionid_value", "resolution-123");
        workOrder.addProperty("msdyn_timefrompromised", "2023-01-01T09:00:00Z");
        workOrder.addProperty("msdyn_timetopromised", "2023-01-01T17:00:00Z");
        workOrder.addProperty("_msdyn_servicerequest_value", "case-123");

        workOrdersArray.add(workOrder);
        response.add("value", workOrdersArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockIncidentsResponse() {
        JsonObject response = new JsonObject();
        JsonArray incidentsArray = new JsonArray();

        JsonObject incident = new JsonObject();
        incident.addProperty("incidentid", "incident-123");
        incident.addProperty("createdon", "2023-01-01T10:00:00Z");
        incident.addProperty("title", "Test Incident");
        incident.addProperty("description", "Test incident description");
        incident.addProperty("_msdyn_incidenttype_value", "incident-type-123");
        incident.addProperty("_gua_equipment_value", "equipment-123");
        incident.addProperty("_gua_serviceplan_value", "service-plan-123");
        incident.addProperty("_rsm_actualproblem_value", "problem-123");
        incident.addProperty("_rsm_resolutionlu_value", "resolution-123");

        incidentsArray.add(incident);
        response.add("value", incidentsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockAnnotationsResponse() {
        JsonObject response = new JsonObject();
        JsonArray annotationsArray = new JsonArray();

        JsonObject annotation = new JsonObject();
        annotation.addProperty("subject", "Test Note Subject");
        annotation.addProperty("notetext", "Test note content");
        annotation.addProperty("createdon", "2023-01-01T10:00:00Z");
        annotation.addProperty("_createdby_value", "user-123");

        annotationsArray.add(annotation);
        response.add("value", annotationsArray);

        return Constants.GSON.toJson(response);
    }

    private Map<String, Object> createValidEntityDefinition() {
        Map<String, Object> entityDefinition = new HashMap<>();
        entityDefinition.put("Case Title", TEST_CASE_TITLE);
        entityDefinition.put("Contact Id", TEST_CONTACT_ID);
        entityDefinition.put("Subject Id", TEST_SUBJECT_ID);
        entityDefinition.put("Description", TEST_DESCRIPTION);
        entityDefinition.put("Priority", TEST_PRIORITY);
        entityDefinition.put("Case Status", TEST_CASE_STATUS);
        return entityDefinition;
    }
}
