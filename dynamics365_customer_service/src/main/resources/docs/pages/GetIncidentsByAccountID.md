# Get Incidents by Account ID

## Overview

Retrieves a comprehensive collection of incidents (cases) from Microsoft Dynamics 365 Customer Service for a specific account within a configurable date range. This request enables detailed case analysis, tracking, and reporting for customer service operations, with optional timeline details including notes, activities, and case history.

## Request Details

- **Area**: Case Management
- **Type**: CHANGE_SYSTEM
- **Retry Support**: No - Query operations are idempotent and don't require retry logic

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Account ID | Text | Yes | Unique identifier (GUID) of the account in Dynamics 365 | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |
| Start Date | Date | No | Beginning of the date range for incident retrieval (defaults to 90 days ago) | `2024-01-01T00:00:00Z` |
| End Date | Date | No | End of the date range for incident retrieval (defaults to current date) | `2024-12-31T23:59:59Z` |
| Include Timeline Details | Text | Yes | Whether to include associated timeline details and notes (`true` or `false`) | `true` |

### Parameter Details

#### Account ID
- **Format**: GUID (Globally Unique Identifier)
- **Validation**: Must be a valid GUID format
- **Purpose**: Identifies the customer account for which to retrieve incidents
- **How to Obtain**: Use "Pick account using Name" or "Get Lookup Account by Account Id" catalog requests

#### Start Date
- **Format**: ISO 8601 date-time format (UTC)
- **Default**: 90 days before current date if not provided
- **Validation**: Must be a valid date
- **Note**: Time component is included for precise filtering

#### End Date
- **Format**: ISO 8601 date-time format (UTC)
- **Default**: Current date and time if not provided
- **Validation**: Must be a valid date and should be after Start Date
- **Maximum Range**: Recommended to keep within 6 months for performance

#### Include Timeline Details
- **Format**: Text string (`"true"` or `"false"`)
- **Default**: `true`
- **Purpose**: Controls whether to fetch associated timeline activities, notes, and case history
- **Performance Impact**: Setting to `false` improves performance for large datasets

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Incidents | List of Entity(Incidents) | Collection of incident (case) records matching the criteria |
| Total Count | Number | Total number of incidents found for the account in the date range |
| Date Range Applied | Multi-Field | Details about the date range used for the query |

### Incidents Entity Structure

Each incident in the list contains comprehensive case information including:

| Field Name | Type | Description |
|------------|------|-------------|
| Case ID | Text | Unique identifier of the incident |
| Case Number | Text | Human-readable case number |
| Case Title | Text | Title/subject of the case |
| Description | Rich Text | Detailed description of the issue |
| Priority | Pick One | Priority level (High, Normal, Low) |
| Case Status | Pick One | Current status of the case |
| Created On | Date | When the case was created |
| Modified On | Date | When the case was last modified |
| Contact ID | Text | Associated contact identifier |
| Subject ID | Text | Subject/category identifier |
| Timeline Details | List | Activities, notes, and history (if Include Timeline Details = true) |

### Date Range Applied Structure

| Field Name | Type | Description |
|------------|------|-------------|
| Start Date | Text | Actual start date used in the query |
| End Date | Text | Actual end date used in the query |
| Days Covered | Text | Number of days in the date range |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Account ID is null or empty | "Account ID is required" | Provide a valid Account ID |
| Account ID is not a valid GUID | "Invalid Account ID format" | Ensure Account ID is a properly formatted GUID |
| Start Date is after End Date | "Start Date must be before End Date" | Adjust date range so Start Date is earlier than End Date |
| Date range exceeds 6 months | "Date range too large, maximum 6 months recommended" | Reduce the date range for better performance |
| Include Timeline Details is not "true" or "false" | "Include Timeline Details must be 'true' or 'false'" | Provide a valid boolean string value |

## Error Handling

### Input Errors (INPUT_ERROR)

**Cause**: Invalid or missing required parameters

**Common Scenarios**:
- Account ID is null, empty, or not a valid GUID
- Include Timeline Details is not "true" or "false"
- Date format is invalid

**Error Messages**:
- "Account ID is required"
- "Invalid Account ID format"
- "Include Timeline Details must be 'true' or 'false'"

**Resolution**:
1. Verify Account ID is a valid GUID
2. Ensure Include Timeline Details is either "true" or "false"
3. Use ISO 8601 format for dates
4. Check that all required parameters are provided

### Logic Errors (LOGIC_ERROR)

**Cause**: Business logic validation failures

**Common Scenarios**:
- Account does not exist in Dynamics 365
- Start Date is after End Date
- Date range is too large

**Error Messages**:
- "Account not found"
- "Invalid date range"
- "Date range exceeds maximum allowed period"

**Resolution**:
1. Verify the Account ID exists using "Get Lookup Account by Account Id"
2. Ensure Start Date is before End Date
3. Reduce date range if it's too large
4. Use default date range by omitting Start Date and End Date

### System Errors (SYSTEM_ERROR)

**Cause**: Dynamics 365 API errors or connection issues

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 service unavailable
- Authentication token expired
- Insufficient permissions

**Error Messages**:
- "Failed to retrieve incidents"
- "Connection to Dynamics 365 failed"
- "Authentication required"

**Resolution**:
1. Verify network connectivity
2. Check extension configuration and authentication
3. Re-validate the extension if authentication expired
4. Ensure user has read permissions for Incident entity
5. Check Dynamics 365 service health status

## Usage Examples

### Example 1: Get All Recent Incidents (Default Date Range)

**Scenario**: Retrieve all incidents for an account from the last 90 days with timeline details

**Input**:
```
Account ID: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Start Date: (not provided - defaults to 90 days ago)
End Date: (not provided - defaults to current date)
Include Timeline Details: "true"
```

**Output**:
```json
{
  "Incidents": [
    {
      "Case ID": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "Case Number": "CAS-00123-X5Y7Z9",
      "Case Title": "Product not working",
      "Description": "Customer reports product stopped functioning",
      "Priority": "High",
      "Case Status": "In Progress",
      "Created On": "2024-10-01T10:30:00Z",
      "Modified On": "2024-10-05T14:20:00Z",
      "Timeline Details": [...]
    },
    {
      "Case ID": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "Case Number": "CAS-00124-A6B8C0",
      "Case Title": "Billing question",
      "Description": "Customer has questions about recent invoice",
      "Priority": "Normal",
      "Case Status": "Resolved",
      "Created On": "2024-09-15T09:15:00Z",
      "Modified On": "2024-09-16T16:45:00Z",
      "Timeline Details": [...]
    }
  ],
  "Total Count": 2,
  "Date Range Applied": {
    "Start Date": "2024-07-09T00:00:00Z",
    "End Date": "2024-10-08T23:59:59Z",
    "Days Covered": "90"
  }
}
```

**Result**: Retrieved 2 incidents from the last 90 days with full timeline details

### Example 2: Get Incidents for Specific Date Range Without Timeline

**Scenario**: Retrieve incidents for Q1 2024 without timeline details for faster performance

**Input**:
```
Account ID: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Start Date: "2024-01-01T00:00:00Z"
End Date: "2024-03-31T23:59:59Z"
Include Timeline Details: "false"
```

**Output**:
```json
{
  "Incidents": [
    {
      "Case ID": "c3d4e5f6-a7b8-9012-cdef-123456789012",
      "Case Number": "CAS-00100-D7E9F1",
      "Case Title": "Installation issue",
      "Description": "Customer needs help with installation",
      "Priority": "Normal",
      "Case Status": "Resolved",
      "Created On": "2024-02-10T11:00:00Z",
      "Modified On": "2024-02-12T15:30:00Z"
    }
  ],
  "Total Count": 1,
  "Date Range Applied": {
    "Start Date": "2024-01-01T00:00:00Z",
    "End Date": "2024-03-31T23:59:59Z",
    "Days Covered": "90"
  }
}
```

**Result**: Retrieved 1 incident from Q1 2024 without timeline details for improved performance

### Example 3: Comprehensive Case Analysis with Timeline

**Scenario**: Analyze all case interactions for a VIP customer including full timeline

**Input**:
```
Account ID: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Start Date: "2024-01-01T00:00:00Z"
End Date: "2024-12-31T23:59:59Z"
Include Timeline Details: "true"
```

**Result**: Complete case history for the year with all activities, notes, emails, and phone calls

## Business Rules

1. **Date Range Defaults**: If Start Date or End Date is not provided, the system uses default values (90 days ago to current date)
2. **Maximum Date Range**: While no hard limit is enforced, date ranges exceeding 6 months may impact performance
3. **Timeline Details Impact**: Including timeline details significantly increases response size and processing time
4. **Account Association**: Only incidents directly associated with the specified Account ID are returned
5. **Active and Resolved Cases**: Both active and resolved incidents are included in the results
6. **Permissions**: User must have read access to the Incident entity in Dynamics 365
7. **Sorting**: Incidents are typically sorted by creation date (newest first)

## Limitations

1. **No Pagination**: All matching incidents are returned in a single response
2. **Performance**: Large date ranges with timeline details may result in slow response times
3. **Data Volume**: Accounts with hundreds of incidents may produce very large responses
4. **No Filtering**: Cannot filter by case status, priority, or other criteria
5. **Timeline Depth**: Timeline details include all associated activities, which can be extensive
6. **API Rate Limits**: Subject to Dynamics 365 API rate limits for high-volume requests

## Best Practices

### 1. Use Appropriate Date Ranges
Limit date ranges to what's actually needed for your use case.

**Recommendation**: Use 30-90 day ranges for regular reporting, longer ranges only for historical analysis.

### 2. Optimize Timeline Details Usage
Only request timeline details when necessary.

**When to Include**:
- Detailed case analysis
- Customer service quality reviews
- Comprehensive case history

**When to Exclude**:
- Simple case counts
- Performance-sensitive dashboards
- High-volume batch processing

### 3. Implement Pagination Logic
For accounts with many incidents, consider implementing client-side pagination.

**Approach**: Retrieve incidents in smaller date ranges and combine results.

### 4. Cache Results
Cache incident data for dashboards and reports that don't require real-time data.

**Recommendation**: Refresh cache hourly or daily depending on requirements.

### 5. Error Handling
Implement robust error handling for various failure scenarios.

**Key Areas**:
- Invalid Account IDs
- Network timeouts for large datasets
- Permission errors
- Date range validation

## Common Use Cases

### 1. Customer Service Dashboard
```
Scenario: Display recent cases for a customer account
Action:
  1. Get Account ID from customer lookup
  2. Call "Get Incidents by Account ID" with default date range
  3. Display case list with status, priority, and creation date
  4. Allow drill-down to see timeline details
Result: Customer service rep sees all recent cases at a glance
```

### 2. Case Volume Analysis
```
Scenario: Analyze case trends over time for an account
Action:
  1. Retrieve incidents for the past year
  2. Set Include Timeline Details to "false" for performance
  3. Group cases by month and status
  4. Generate trend charts
Result: Management insights into case volume and resolution patterns
```

### 3. VIP Customer Review
```
Scenario: Prepare for quarterly business review with VIP customer
Action:
  1. Get all incidents for the quarter
  2. Include timeline details to see all interactions
  3. Analyze response times and resolution quality
  4. Identify improvement opportunities
Result: Comprehensive case history for customer review meeting
```

## Related Catalog Requests

- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Get account details before retrieving incidents
- [Pick Account Using Name](pages/PickAccountUsingName.md) - Find Account ID by account name
- [Get Subjects for Case](pages/GetSubjectsForCase.md) - Understand case categorization
- [Case Entity](pages/CaseEntity.md) - Create, update, and manage individual cases
- [Get WorkOrder By AccountId](pages/GetWorkOrderByAccountId.md) - Similar request for field service work orders

## Technical Implementation

### Helper Class
- **Class**: CaseManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `getIncidentsByAccountID()`
- **Service**: CaseDaoImpl.getIncidentsByAccountId()

### Implementation Details

```java
@CatalogRequest(
    id = "localDomainRequest_f2c556a9-d838-475e-8d05-89d613da296b",
    name = "Get Incidents by Account ID",
    description = "Retrieves a collection of Incidents (Cases) from Microsoft Dynamics 365 Customer Service...",
    area = "Case management",
    type = CatalogRequest.Type.CHANGE_SYSTEM)
@Field.Desc(name = "Incidents", type = "[ Entity(Incidents) ]", required = false)
@Field(name = "Total Count", type = "Number", required = false)
@Field.Desc(name = "Date Range Applied", type = "{ Start Date: Text, End Date: Text, Days Covered: Text }", required = false)
public Map<String, Object> getIncidentsByAccountID(
    @Field.Text(name = "Account ID", required = true) String accountID,
    @Field.Date(name = "Start Date", required = false) Long startDate,
    @Field.Date(name = "End Date", required = false) Long endDate,
    @Field.Text(name = "Include Timeline Details", required = true) String includeTimelineDetails)
```

## Troubleshooting

### Issue: "No Incidents Found"

**Cause**: No incidents exist for the account in the specified date range

**Solution**:
1. Verify the Account ID is correct
2. Expand the date range
3. Check if incidents exist in Dynamics 365 for this account
4. Verify the account has associated cases

### Issue: "Request Timeout"

**Cause**: Too many incidents or timeline details causing slow response

**Solution**:
1. Reduce the date range
2. Set Include Timeline Details to "false"
3. Implement pagination by querying smaller date ranges
4. Contact administrator if performance issues persist

### Issue: "Insufficient Permissions"

**Cause**: User lacks read access to Incident entity

**Solution**:
1. Verify user has appropriate security role in Dynamics 365
2. Ensure security role includes read privilege for Incident entity
3. Contact Dynamics 365 administrator to grant permissions

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Understanding authentication modes
- [Case Entity](pages/CaseEntity.md) - Manage individual cases
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup

