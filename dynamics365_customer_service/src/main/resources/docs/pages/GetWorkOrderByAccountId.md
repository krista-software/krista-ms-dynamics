# Get WorkOrder By AccountId

## Overview

Retrieves a collection of work orders from Microsoft Dynamics 365 Field Service based on Account ID and configurable date range. This request enables comprehensive work order analysis, tracking, and reporting for field service operations within specified time periods, providing visibility into service activities, technician assignments, and work completion status.

## Request Details

- **Area**: Case Management
- **Type**: CHANGE_SYSTEM
- **Retry Support**: No - Query operations are idempotent and don't require retry logic

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| AccountId | Text | Yes | Unique identifier (GUID) of the account in Dynamics 365 | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |
| Start Date | Date | No | Beginning of the date range for work order retrieval (defaults to 90 days ago) | `2024-01-01T00:00:00Z` |
| End Date | Date | No | End of the date range for work order retrieval (defaults to current date) | `2024-12-31T23:59:59Z` |

### Parameter Details

#### AccountId
- **Format**: GUID (Globally Unique Identifier)
- **Validation**: Must be a valid GUID format
- **Purpose**: Identifies the customer account for which to retrieve work orders
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

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Work Orders | List of Entity(Work Orders) | Collection of work order records matching the criteria |
| Total Count | Number | Total number of work orders found for the account in the date range |
| Date Range Applied | Multi-Field | Details about the date range used for the query |

### Work Orders Entity Structure

Each work order in the list contains comprehensive field service information including:

| Field Name | Type | Description |
|------------|------|-------------|
| Work Order ID | Text | Unique identifier of the work order |
| Work Order Number | Text | Human-readable work order number |
| Service Account | Text | Associated customer account |
| Work Order Type | Pick One | Type of work order (Installation, Repair, Maintenance, etc.) |
| System Status | Pick One | Current status of the work order |
| Primary Incident Type | Text | Type of service issue |
| Booking Status | Pick One | Scheduling status |
| Created On | Date | When the work order was created |
| Date Window Start | Date | Scheduled start date/time |
| Date Window End | Date | Scheduled end date/time |
| Technician | Text | Assigned technician/resource |

### Date Range Applied Structure

| Field Name | Type | Description |
|------------|------|-------------|
| Start Date | Text | Actual start date used in the query |
| End Date | Text | Actual end date used in the query |
| Days Covered | Text | Number of days in the date range |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| AccountId is null or empty | "AccountId is required" | Provide a valid AccountId |
| AccountId is not a valid GUID | "Invalid AccountId format" | Ensure AccountId is a properly formatted GUID |
| Start Date is after End Date | "Start Date must be before End Date" | Adjust date range so Start Date is earlier than End Date |
| Date range exceeds 6 months | "Date range too large, maximum 6 months recommended" | Reduce the date range for better performance |

## Error Handling

### Input Errors (INPUT_ERROR)

**Cause**: Invalid or missing required parameters

**Common Scenarios**:
- AccountId is null, empty, or not a valid GUID
- Date format is invalid

**Error Messages**:
- "AccountId is required"
- "Invalid AccountId format"
- "Invalid date format"

**Resolution**:
1. Verify AccountId is a valid GUID
2. Use ISO 8601 format for dates
3. Check that all required parameters are provided

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
1. Verify the AccountId exists using "Get Lookup Account by Account Id"
2. Ensure Start Date is before End Date
3. Reduce date range if it's too large
4. Use default date range by omitting Start Date and End Date

### System Errors (SYSTEM_ERROR)

**Cause**: Dynamics 365 API errors or connection issues

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 Field Service not available
- Authentication token expired
- Insufficient permissions

**Error Messages**:
- "Failed to retrieve work orders"
- "Connection to Dynamics 365 failed"
- "Authentication required"

**Resolution**:
1. Verify network connectivity
2. Check extension configuration and authentication
3. Re-validate the extension if authentication expired
4. Ensure user has read permissions for Work Order entity
5. Verify Dynamics 365 Field Service is enabled

## Usage Examples

### Example 1: Get All Recent Work Orders (Default Date Range)

**Scenario**: Retrieve all work orders for an account from the last 90 days

**Input**:
```
AccountId: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Start Date: (not provided - defaults to 90 days ago)
End Date: (not provided - defaults to current date)
```

**Output**:
```json
{
  "Work Orders": [
    {
      "Work Order ID": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "Work Order Number": "WO-00123",
      "Service Account": "Demo Company Inc.",
      "Work Order Type": "Repair",
      "System Status": "Completed",
      "Primary Incident Type": "Equipment Malfunction",
      "Booking Status": "Completed",
      "Created On": "2024-10-01T10:30:00Z",
      "Date Window Start": "2024-10-02T08:00:00Z",
      "Date Window End": "2024-10-02T12:00:00Z",
      "Technician": "John Smith"
    },
    {
      "Work Order ID": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "Work Order Number": "WO-00124",
      "Service Account": "Demo Company Inc.",
      "Work Order Type": "Maintenance",
      "System Status": "Scheduled",
      "Primary Incident Type": "Preventive Maintenance",
      "Booking Status": "Scheduled",
      "Created On": "2024-09-15T09:15:00Z",
      "Date Window Start": "2024-10-15T09:00:00Z",
      "Date Window End": "2024-10-15T11:00:00Z",
      "Technician": "Jane Doe"
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

**Result**: Retrieved 2 work orders from the last 90 days

### Example 2: Get Work Orders for Specific Date Range

**Scenario**: Retrieve work orders for Q1 2024

**Input**:
```
AccountId: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Start Date: "2024-01-01T00:00:00Z"
End Date: "2024-03-31T23:59:59Z"
```

**Output**:
```json
{
  "Work Orders": [
    {
      "Work Order ID": "c3d4e5f6-a7b8-9012-cdef-123456789012",
      "Work Order Number": "WO-00100",
      "Service Account": "Demo Company Inc.",
      "Work Order Type": "Installation",
      "System Status": "Completed",
      "Primary Incident Type": "New Installation",
      "Booking Status": "Completed",
      "Created On": "2024-02-10T11:00:00Z",
      "Date Window Start": "2024-02-15T10:00:00Z",
      "Date Window End": "2024-02-15T14:00:00Z",
      "Technician": "Mike Johnson"
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

**Result**: Retrieved 1 work order from Q1 2024

### Example 3: No Work Orders Found

**Scenario**: Account has no work orders in the specified date range

**Input**:
```
AccountId: "3952deb7-6ge1-fc22-b8d8-111e4b2g7020"
Start Date: "2024-01-01T00:00:00Z"
End Date: "2024-12-31T23:59:59Z"
```

**Output**:
```json
{
  "Work Orders": [],
  "Total Count": 0,
  "Date Range Applied": {
    "Start Date": "2024-01-01T00:00:00Z",
    "End Date": "2024-12-31T23:59:59Z",
    "Days Covered": "365"
  }
}
```

**Result**: No work orders found for this account in the date range

## Business Rules

1. **Date Range Defaults**: If Start Date or End Date is not provided, the system uses default values (90 days ago to current date)
2. **Maximum Date Range**: While no hard limit is enforced, date ranges exceeding 6 months may impact performance
3. **Account Association**: Only work orders directly associated with the specified AccountId are returned
4. **All Statuses**: Work orders in all statuses (Scheduled, In Progress, Completed, Canceled) are included
5. **Permissions Required**: User must have read access to the Work Order entity in Dynamics 365
6. **Field Service Required**: Dynamics 365 Field Service must be enabled in your instance
7. **Sorting**: Work orders are typically sorted by creation date (newest first)

## Limitations

1. **No Pagination**: All matching work orders are returned in a single response
2. **Performance**: Large date ranges may result in slow response times
3. **Data Volume**: Accounts with hundreds of work orders may produce very large responses
4. **No Filtering**: Cannot filter by work order type, status, or other criteria
5. **No Related Data**: Does not automatically include related bookings, products, or services
6. **API Rate Limits**: Subject to Dynamics 365 API rate limits for high-volume requests

## Best Practices

### 1. Use Appropriate Date Ranges
Limit date ranges to what's actually needed for your use case.

**Recommendation**: Use 30-90 day ranges for regular reporting, longer ranges only for historical analysis.

### 2. Implement Pagination Logic
For accounts with many work orders, consider implementing client-side pagination.

**Approach**: Retrieve work orders in smaller date ranges and combine results.

### 3. Cache Results
Cache work order data for dashboards and reports that don't require real-time data.

**Recommendation**: Refresh cache hourly or daily depending on requirements.

### 4. Filter on Client Side
Implement client-side filtering for work order type, status, or technician.

**Benefit**: Provides flexibility without additional API calls.

### 5. Error Handling
Implement robust error handling for various failure scenarios.

**Key Areas**:
- Invalid AccountIds
- Network timeouts for large datasets
- Permission errors
- Date range validation

## Common Use Cases

### 1. Field Service Dashboard
```
Scenario: Display recent work orders for a customer account
Action:
  1. Get AccountId from customer lookup
  2. Call "Get WorkOrder By AccountId" with default date range
  3. Display work order list with status, type, and scheduled dates
  4. Allow drill-down to see work order details
Result: Field service rep sees all recent work orders at a glance
```

### 2. Service History Analysis
```
Scenario: Analyze service patterns over time for an account
Action:
  1. Retrieve work orders for the past year
  2. Group work orders by type and month
  3. Identify trends and patterns
  4. Generate service history report
Result: Insights into service frequency and types
```

### 3. Technician Performance Review
```
Scenario: Review technician performance for a specific customer
Action:
  1. Get all work orders for the account
  2. Filter by assigned technician
  3. Analyze completion times and customer feedback
  4. Identify areas for improvement
Result: Data-driven technician performance evaluation
```

## Related Catalog Requests

- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Get account details before retrieving work orders
- [Pick Account Using Name](pages/PickAccountUsingName.md) - Find AccountId by account name
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Similar request for customer service cases
- [Get Customer Assets by Account Id](pages/GetCustomerAssetsByAccountId.md) - Get assets that may be related to work orders

## Technical Implementation

### Helper Class
- **Class**: CaseManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `getWorkOrderByAccountId()`
- **Service**: WorkOrderDaoImpl.getWorkOrdersByAccountId()

### API Endpoint

The request calls the Dynamics 365 Web API:
- **Endpoint**: `/api/data/v9.2/msdyn_workorders`
- **Method**: GET
- **Query Parameters**: `$filter=_msdyn_serviceaccount_value eq '{accountId}' and createdon ge {startDate} and createdon le {endDate}`
- **Authentication**: OAuth 2.0 Bearer token

## Troubleshooting

### Issue: "No Work Orders Found"

**Cause**: No work orders exist for the account in the specified date range

**Solution**:
1. Verify the AccountId is correct
2. Expand the date range
3. Check if work orders exist in Dynamics 365 for this account
4. Verify Dynamics 365 Field Service is enabled

### Issue: "Request Timeout"

**Cause**: Too many work orders causing slow response

**Solution**:
1. Reduce the date range
2. Implement pagination by querying smaller date ranges
3. Contact administrator if performance issues persist

### Issue: "Insufficient Permissions"

**Cause**: User lacks read access to Work Order entity

**Solution**:
1. Verify user has appropriate security role in Dynamics 365
2. Ensure security role includes read privilege for Work Order entity
3. Contact Dynamics 365 administrator to grant permissions

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Understanding authentication modes
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup

