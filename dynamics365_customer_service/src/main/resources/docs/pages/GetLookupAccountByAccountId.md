# Get Lookup Account by Account Id

## Overview

Retrieves complete account information from Microsoft Dynamics 365 Customer Service based on the account identifier. This request enables users to fetch detailed customer account data including contact information, address, and other account attributes, making it essential for account verification, data enrichment, and account-related operations.

## Request Details

- **Area**: Case Management
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
- **Purpose**: Identifies the specific customer account to retrieve
- **How to Obtain**: Use "Pick account using Name" catalog request or from other account-related operations

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Account | Entity(Account) | Complete account entity with all account information |

### Account Entity Structure

The Account entity contains comprehensive customer account information including:

| Field Name | Type | Description |
|------------|------|-------------|
| Customer Id | Text | Unique identifier of the account |
| Customer Name | Text | Full name of the account/company |
| Phone | Text | Primary phone number |
| Fax | Text | Fax number |
| Website | Text | Company website URL |
| Primary Contact Id | Text | Identifier of the primary contact person |
| Parent Customer Id | Text | Identifier of the parent account (if applicable) |
| Address | Multi-Field | Complete address information (Street, City, State, Postal Code, Country) |
| Account Number | Text | Unique account number |

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
4. Use "Pick account using Name" to search for the account

### System Errors (SYSTEM_ERROR)

**Cause**: Dynamics 365 API errors or connection issues

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 service unavailable
- Authentication token expired
- Insufficient permissions to read account data

**Error Messages**:
- "Failed to retrieve account"
- "Connection to Dynamics 365 failed"
- "Authentication required"

**Resolution**:
1. Verify network connectivity to Dynamics 365
2. Check extension configuration and authentication
3. Re-validate the extension if authentication expired
4. Ensure the service account has read permissions for Account entity
5. Check Dynamics 365 service health status

## Usage Examples

### Example 1: Retrieve Complete Account Information

**Scenario**: Get full details for a specific customer account

**Input**:
```
AccountId: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
```

**Output**:
```json
{
  "Account": {
    "Customer Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
    "Customer Name": "Demo Company Inc.",
    "Phone": "555-0100",
    "Fax": "555-0101",
    "Website": "www.democompany.com",
    "Primary Contact Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Parent Customer Id": null,
    "Street1": "123 Main Street",
    "Street2": "Suite 100",
    "Street3": "",
    "City": "New York",
    "State": "NY",
    "Postal Code": "10001",
    "Country": "United States",
    "Account Number": "ACC-00123"
  }
}
```

**Result**: Retrieved complete account information

### Example 2: Verify Account Exists

**Scenario**: Validate that an account exists before performing operations

**Input**:
```
AccountId: "3952deb7-6ge1-fc22-b8d8-111e4b2g7020"
```

**Output**:
```
Error: "Account not found"
```

**Result**: Account does not exist or has been deleted

### Example 3: Get Account for Case Creation

**Scenario**: Retrieve account details to populate case creation form

**Workflow**:
1. User selects account from search results
2. Call "Get Lookup Account by Account Id" with the AccountId
3. Display account information in the form
4. Pre-fill account-related fields
5. User completes case creation

**Result**: Case creation form is populated with accurate account information

## Business Rules

1. **Single Account**: Returns exactly one account entity (or error if not found)
2. **Complete Entity**: Returns the full Account entity with all available fields
3. **Read-Only Operation**: This request only retrieves data and does not modify the account
4. **Active Accounts**: Only active accounts can be retrieved; deleted accounts return an error
5. **Permissions Required**: User must have read access to the Account entity in Dynamics 365
6. **Account Hierarchy**: Parent Customer Id indicates if the account is part of a hierarchy

## Limitations

1. **Single Account Only**: Can only retrieve one account at a time
2. **No Related Data**: Does not automatically include related contacts, cases, or assets
3. **No Historical Data**: Returns current account state only, not historical changes
4. **Field Availability**: Some custom fields may not be included in the standard entity
5. **No Aggregations**: Does not include summary data (e.g., total cases, total revenue)

## Best Practices

### 1. Validate AccountId Format
Check that the AccountId is a valid GUID before making the request.

**Recommendation**: Use regex or GUID validation to prevent unnecessary API calls.

### 2. Handle Not Found Errors Gracefully
Implement proper error handling for accounts that don't exist.

**User Experience**: Display a clear message like "Account not found. Please verify the account ID."

### 3. Cache Account Data
For frequently accessed accounts, cache the account information.

**Benefit**: Reduces API calls and improves performance.

### 4. Use for Verification
Use this request to verify an account exists before performing operations.

**Example**: Before creating a case, verify the account exists and is active.

### 5. Combine with Related Requests
After retrieving account details, use related requests to get complete information.

**Workflow**: Get account → Get contacts → Get cases → Get assets

## Common Use Cases

### 1. Account Verification
```
Scenario: Verify an account exists before creating a case
Action:
  1. Receive AccountId from user input or previous operation
  2. Call "Get Lookup Account by Account Id"
  3. If successful, account exists and is active
  4. If error, account doesn't exist or is inactive
  5. Proceed or show error accordingly
Result: Validated account existence before proceeding
```

### 2. Account Details Display
```
Scenario: Display complete account information in a dashboard
Action:
  1. Get AccountId from context
  2. Call "Get Lookup Account by Account Id"
  3. Display account name, contact info, address
  4. Show related data (contacts, cases, assets)
Result: Comprehensive account overview
```

### 3. Form Pre-Population
```
Scenario: Pre-fill form fields with account information
Action:
  1. User selects account
  2. Retrieve account details
  3. Auto-populate form fields (name, address, phone)
  4. User completes remaining fields
Result: Faster data entry with accurate information
```

### 4. Account Hierarchy Navigation
```
Scenario: Navigate to parent or child accounts
Action:
  1. Retrieve current account
  2. Check Parent Customer Id field
  3. If parent exists, retrieve parent account details
  4. Display account hierarchy
Result: Understanding of account relationships
```

## Related Catalog Requests

- [Pick Account Using Name](pages/PickAccountUsingName.md) - Find AccountId by account name
- [Get Contacts By Company Id](pages/GetContactsByCompanyId.md) - Get contacts for this account
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Retrieve cases for this account
- [Get WorkOrder By AccountId](pages/GetWorkOrderByAccountId.md) - Get work orders for this account
- [Get Customer Assets by Account Id](pages/GetCustomerAssetsByAccountId.md) - Get assets for this account
- [Customer Entity](pages/CustomerEntity.md) - Create, update, and manage accounts

## Technical Implementation

### Helper Class
- **Class**: CaseManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `getLookupAccountByAccountId()`
- **Service**: AccountDaoImpl.getAccountById()

### Implementation Details

The catalog request retrieves a single account entity by its unique identifier using the Dynamics 365 Web API.

### API Endpoint

The request calls the Dynamics 365 Web API:
- **Endpoint**: `/api/data/v9.2/accounts({accountId})`
- **Method**: GET
- **Authentication**: OAuth 2.0 Bearer token

## Troubleshooting

### Issue: "Account Not Found"

**Cause**: Account does not exist or has been deleted

**Solution**:
1. Verify the AccountId is correct
2. Use "Pick account using Name" to search for the account
3. Check if the account has been deleted in Dynamics 365
4. Ensure you're using the correct Dynamics 365 instance
5. Verify the account is active

### Issue: "Invalid AccountId Format"

**Cause**: AccountId is not a valid GUID

**Solution**:
1. Verify the AccountId is a properly formatted GUID
2. Check for extra spaces or special characters
3. Ensure the AccountId is copied correctly
4. Use "Pick account using Name" to get a valid AccountId

### Issue: "Insufficient Permissions"

**Cause**: User or service account lacks read access to Account entity

**Solution**:
1. Verify the user has a security role with read access to Account entity
2. In Dynamics 365, go to Settings → Security → Security Roles
3. Edit the user's security role
4. Under Core Records tab, ensure "Account" has at least "Read" privilege
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
- [Customer Entity](pages/CustomerEntity.md) - Create, update, and manage accounts
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

