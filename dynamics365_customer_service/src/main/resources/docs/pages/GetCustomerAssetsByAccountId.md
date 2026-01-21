# Get Customer Assets by Account Id

## Overview

Retrieves all customer assets associated with a specific account in Microsoft Dynamics 365 Customer Service. This request enables users to view all equipment, products, and assets owned or managed by a customer account, making it essential for service management, warranty tracking, maintenance scheduling, and asset lifecycle management.

## Request Details

- **Area**: Customer Management
- **Type**: CHANGE_SYSTEM
- **Retry Support**: No - Query operations are idempotent and don't require retry logic

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| AccountId | Text | Yes | Unique identifier (GUID) of the account in Dynamics 365 | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |

### Parameter Details

#### AccountId
- **Format**: GUID (Globally Unique Identifier)
- **Validation**: Must be a valid GUID format
- **Purpose**: Identifies the customer account for which to retrieve assets
- **How to Obtain**: Use "Pick account using Name" or "Get Lookup Account by Account Id" catalog requests

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Customer Asset | List of Entity | Collection of customer asset records associated with the specified account |

### Customer Asset Entity Structure

Each customer asset in the list contains comprehensive asset information including:

| Field Name | Type | Description |
|------------|------|-------------|
| Asset ID | Text | Unique identifier of the customer asset |
| Asset Name | Text | Name or description of the asset |
| Product | Text | Product associated with the asset |
| Serial Number | Text | Serial number of the asset |
| Account | Text | Associated customer account |
| Parent Asset | Text | Parent asset (if part of a hierarchy) |
| Asset Status | Pick One | Current status of the asset |
| Install Date | Date | Date when the asset was installed |
| Warranty Expiration | Date | Warranty expiration date |
| Last Service Date | Date | Date of last service or maintenance |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| AccountId is null or empty | "AccountId is required" | Provide a valid AccountId |
| AccountId is not a valid GUID | "Invalid AccountId format" | Ensure AccountId is a properly formatted GUID |

## Error Handling

### Input Errors (INPUT_ERROR)

**Cause**: Invalid or missing required parameters

**Common Scenarios**:
- AccountId is null or empty
- AccountId is not a valid GUID format

**Error Messages**:
- "AccountId is required"
- "Invalid AccountId format"

**Resolution**:
1. Verify AccountId is provided
2. Ensure AccountId is a valid GUID format
3. Use "Pick account using Name" to get a valid AccountId

### Logic Errors (LOGIC_ERROR)

**Cause**: Business logic validation failures

**Common Scenarios**:
- Account does not exist in Dynamics 365
- AccountId is valid but account has been deleted

**Error Messages**:
- "Account not found"
- "Account does not exist"

**Resolution**:
1. Verify the AccountId is correct
2. Check if the account exists in Dynamics 365
3. Ensure the account is active (not deleted or deactivated)
4. Use "Get Lookup Account by Account Id" to verify account exists

### System Errors (SYSTEM_ERROR)

**Cause**: Dynamics 365 API errors or connection issues

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 service unavailable
- Authentication token expired
- Insufficient permissions to read customer asset data

**Error Messages**:
- "Failed to retrieve customer assets"
- "Connection to Dynamics 365 failed"
- "Authentication required"

**Resolution**:
1. Verify network connectivity to Dynamics 365
2. Check extension configuration and authentication
3. Re-validate the extension if authentication expired
4. Ensure the service account has read permissions for Customer Asset entity
5. Check Dynamics 365 service health status

## Usage Examples

### Example 1: Retrieve All Assets for an Account

**Scenario**: Get all equipment and products owned by a customer

**Input**:
```
AccountId: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
```

**Output**:
```json
{
  "Customer Asset": [
    {
      "Asset ID": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "Asset Name": "HVAC System - Building A",
      "Product": "Commercial HVAC Unit Model X200",
      "Serial Number": "SN-12345-HVAC",
      "Account": "Demo Company Inc.",
      "Parent Asset": null,
      "Asset Status": "Active",
      "Install Date": "2023-01-15T00:00:00Z",
      "Warranty Expiration": "2026-01-15T00:00:00Z",
      "Last Service Date": "2024-09-01T00:00:00Z"
    },
    {
      "Asset ID": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "Asset Name": "Server Rack - Data Center",
      "Product": "Enterprise Server Rack SR-500",
      "Serial Number": "SN-67890-SRV",
      "Account": "Demo Company Inc.",
      "Parent Asset": null,
      "Asset Status": "Active",
      "Install Date": "2022-06-10T00:00:00Z",
      "Warranty Expiration": "2025-06-10T00:00:00Z",
      "Last Service Date": "2024-08-15T00:00:00Z"
    }
  ]
}
```

**Result**: Retrieved 2 customer assets for the account

### Example 2: Account with No Assets

**Scenario**: Retrieve assets for an account that has no registered assets

**Input**:
```
AccountId: "3952deb7-6ge1-fc22-b8d8-111e4b2g7020"
```

**Output**:
```json
{
  "Customer Asset": []
}
```

**Result**: No assets found for this account (empty list)

### Example 3: Asset Hierarchy

**Scenario**: Retrieve assets including parent-child relationships

**Output**:
```json
{
  "Customer Asset": [
    {
      "Asset ID": "c3d4e5f6-a7b8-9012-cdef-123456789012",
      "Asset Name": "Manufacturing Line 1",
      "Product": "Assembly Line System",
      "Serial Number": "SN-11111-MFG",
      "Account": "Demo Company Inc.",
      "Parent Asset": null,
      "Asset Status": "Active"
    },
    {
      "Asset ID": "d4e5f6a7-b8c9-0123-def1-234567890123",
      "Asset Name": "Robotic Arm Unit 1",
      "Product": "Industrial Robot ARM-300",
      "Serial Number": "SN-22222-ARM",
      "Account": "Demo Company Inc.",
      "Parent Asset": "Manufacturing Line 1",
      "Asset Status": "Active"
    }
  ]
}
```

**Result**: Retrieved assets showing parent-child relationship

## Business Rules

1. **Account Association**: Only assets directly associated with the specified account are returned
2. **Active and Inactive Assets**: Both active and inactive assets are included in the results
3. **Read-Only Operation**: This request only retrieves data and does not modify any assets
4. **No Pagination**: All assets for the account are returned in a single response
5. **Full Entity**: Complete Customer Asset entity information is returned
6. **Permissions Required**: User must have read access to the Customer Asset entity in Dynamics 365
7. **Asset Hierarchy**: Parent Asset field indicates if the asset is part of a hierarchy

## Limitations

1. **No Filtering**: Cannot filter assets by specific criteria (e.g., product type, warranty status)
2. **No Sorting**: Assets are returned in the default order from Dynamics 365 API
3. **No Pagination**: All assets are returned at once, which may impact performance for accounts with many assets
4. **Single Account Only**: Can only query one account at a time
5. **No Service History**: Does not automatically include service history or work orders
6. **Performance**: Accounts with hundreds of assets may experience slower response times

## Best Practices

### 1. Validate AccountId Before Use
Verify the AccountId exists before retrieving assets.

**Recommendation**: Use "Get Lookup Account by Account Id" to validate the account first.

### 2. Handle Empty Results
Implement logic to handle accounts with no assets.

**User Experience**: Display a message like "No assets found for this account" rather than showing an empty list.

### 3. Cache Asset Lists
For frequently accessed accounts, cache the asset list.

**Benefit**: Reduces API calls and improves performance.

### 4. Display Asset Hierarchy
If assets have parent-child relationships, display them hierarchically.

**Enhancement**: Use tree view or indentation to show asset structure.

### 5. Highlight Warranty Status
Identify assets with expiring or expired warranties.

**Value**: Proactive warranty management and renewal opportunities.

## Common Use Cases

### 1. Service Call Preparation
```
Scenario: Technician preparing for a service call needs to know what equipment is on-site
Action:
  1. Get AccountId for the customer
  2. Call "Get Customer Assets by Account Id"
  3. Display list of all assets
  4. Technician reviews equipment before visit
Result: Technician arrives prepared with correct parts and tools
```

### 2. Warranty Management
```
Scenario: Identify assets with expiring warranties
Action:
  1. Retrieve all assets for an account
  2. Check Warranty Expiration dates
  3. Filter assets with warranties expiring in next 90 days
  4. Generate renewal notifications
Result: Proactive warranty renewal management
```

### 3. Maintenance Scheduling
```
Scenario: Schedule preventive maintenance for customer assets
Action:
  1. Get all assets for the account
  2. Check Last Service Date for each asset
  3. Identify assets due for maintenance
  4. Create work orders for scheduled maintenance
Result: Proactive maintenance scheduling
```

### 4. Asset Inventory Report
```
Scenario: Generate complete asset inventory for a customer
Action:
  1. Retrieve all assets
  2. Format asset information
  3. Include warranty and service dates
  4. Export to PDF or Excel
Result: Comprehensive asset inventory report
```

## Related Catalog Requests

- [Pick Account Using Name](pages/PickAccountUsingName.md) - Find AccountId by account name
- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Verify account exists and get details
- [Get WorkOrder By AccountId](pages/GetWorkOrderByAccountId.md) - Get service history for assets
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Retrieve cases that may be related to assets

## Technical Implementation

### Helper Class
- **Class**: CustomerManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `getCustomerAssetsByAccountId()`
- **Service**: CustomerAssetDaoImpl.getAssetsByAccountId()

### Implementation Details

The catalog request queries the Dynamics 365 Customer Asset entity filtered by the associated account.

### API Endpoint

The request calls the Dynamics 365 Web API:
- **Endpoint**: `/api/data/v9.2/msdyn_customerassets`
- **Method**: GET
- **Query Parameters**: `$filter=_msdyn_account_value eq '{accountId}'`
- **Authentication**: OAuth 2.0 Bearer token

## Troubleshooting

### Issue: "No Assets Found"

**Cause**: Account has no registered customer assets

**Solution**:
1. Verify the AccountId is correct
2. Check if assets exist for this account in Dynamics 365
3. Ensure assets are properly associated with the account
4. Verify you have permission to view the assets
5. Consider creating asset records if none exist

### Issue: "Account Not Found"

**Cause**: AccountId does not exist or account has been deleted

**Solution**:
1. Verify the AccountId is correct
2. Use "Pick account using Name" to find the correct account
3. Check if the account has been deleted in Dynamics 365
4. Ensure you're using the correct Dynamics 365 instance

### Issue: "Insufficient Permissions"

**Cause**: User or service account lacks read access to Customer Asset entity

**Solution**:
1. Verify the user has a security role with read access to Customer Asset entity
2. In Dynamics 365, go to Settings → Security → Security Roles
3. Edit the user's security role
4. Ensure "Customer Asset" has at least "Read" privilege
5. Save changes and retry the request

### Issue: "Authentication Required"

**Cause**: Access token has expired

**Solution**:
1. The extension should automatically refresh the token
2. If automatic refresh fails, re-validate the extension
3. Check that the refresh token hasn't expired (90 days of inactivity)
4. Verify Client Secret hasn't expired in Microsoft Entra ID

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Understanding authentication modes
- [Customer Entity](pages/CustomerEntity.md) - Manage customer accounts
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

