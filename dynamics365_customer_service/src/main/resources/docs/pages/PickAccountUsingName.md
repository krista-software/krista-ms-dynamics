# Pick Account Using Name

## Overview

Retrieves a list of accounts from Microsoft Dynamics 365 Customer Service based on the account name. This request enables users to search for customer accounts by name, making it easy to find and select the correct account when creating cases, contacts, or performing other operations that require account association.

## Request Details

- **Area**: Case Management
- **Type**: CHANGE_SYSTEM
- **Retry Support**: No - Query operations are idempotent and don't require retry logic

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Account Name | Text | Yes | Name or partial name of the account to search for | `Demo Company` |

### Parameter Details

#### Account Name
- **Format**: Text string
- **Validation**: Must not be empty
- **Search Behavior**: Performs a partial match search (case-insensitive)
- **Purpose**: Finds accounts whose name contains the provided text
- **Minimum Length**: Recommended at least 3 characters for meaningful results

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| List of Account | List of Multi-Field | Collection of matching accounts with basic information |

### List of Account Structure

Each account in the list contains:

| Field Name | Type | Description | Example |
|------------|------|-------------|---------|
| Account Name | Text | Full name of the account | "Demo Company Inc." |
| Address | Text | Primary address of the account | "123 Main St, New York, NY 10001" |
| Account Number | Text | Unique account number | "ACC-00123" |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Account Name is null or empty | "Account Name is required" | Provide a valid account name |
| Account Name is whitespace only | "Account Name cannot be empty" | Provide a non-empty account name |

## Error Handling

### Input Errors (INPUT_ERROR)

**Cause**: Invalid or missing required parameters

**Common Scenarios**:
- Account Name is null or empty
- Account Name contains only whitespace

**Error Messages**:
- "Account Name is required"
- "Invalid input parameter"

**Resolution**:
1. Verify Account Name is provided
2. Ensure Account Name contains at least one non-whitespace character
3. Check that the parameter is properly formatted

### Logic Errors (LOGIC_ERROR)

**Cause**: Business logic validation failures

**Common Scenarios**:
- No accounts found matching the search criteria
- Search term is too generic, returning too many results

**Error Messages**:
- "No accounts found matching the criteria"
- "Search returned too many results, please refine your search"

**Resolution**:
1. Verify the account name is spelled correctly
2. Try using a more specific search term
3. Check if the account exists in Dynamics 365
4. Use a longer or more unique portion of the account name

### System Errors (SYSTEM_ERROR)

**Cause**: Dynamics 365 API errors or connection issues

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 service unavailable
- Authentication token expired
- Insufficient permissions to read account data

**Error Messages**:
- "Failed to retrieve accounts"
- "Connection to Dynamics 365 failed"
- "Authentication required"

**Resolution**:
1. Verify network connectivity to Dynamics 365
2. Check extension configuration and authentication
3. Re-validate the extension if authentication expired
4. Ensure the service account has read permissions for Account entity
5. Check Dynamics 365 service health status

## Usage Examples

### Example 1: Search for Account by Full Name

**Scenario**: Find a specific account by its complete name

**Input**:
```
Account Name: "Demo Company"
```

**Output**:
```json
{
  "List of Account": [
    {
      "Account Name": "Demo Company",
      "Address": "123 Main Street, New York, NY 10001",
      "Account Number": "ACC-00123"
    }
  ]
}
```

**Result**: Found 1 exact match for the account

### Example 2: Search for Account by Partial Name

**Scenario**: Find all accounts containing "Tech" in their name

**Input**:
```
Account Name: "Tech"
```

**Output**:
```json
{
  "List of Account": [
    {
      "Account Name": "Tech Solutions Inc.",
      "Address": "456 Innovation Drive, San Francisco, CA 94105",
      "Account Number": "ACC-00456"
    },
    {
      "Account Name": "Advanced Technology Corp",
      "Address": "789 Silicon Valley Blvd, San Jose, CA 95110",
      "Account Number": "ACC-00789"
    },
    {
      "Account Name": "FinTech Partners LLC",
      "Address": "321 Financial Plaza, Boston, MA 02110",
      "Account Number": "ACC-00321"
    }
  ]
}
```

**Result**: Found 3 accounts with "Tech" in their name

### Example 3: No Results Found

**Scenario**: Search for an account that doesn't exist

**Input**:
```
Account Name: "NonExistent Company"
```

**Output**:
```json
{
  "List of Account": []
}
```

**Result**: No accounts found matching the search criteria

## Business Rules

1. **Partial Match Search**: The search performs a partial, case-insensitive match on account names
2. **Multiple Results**: If multiple accounts match, all are returned in the list
3. **Read-Only Operation**: This request only retrieves data and does not modify any accounts
4. **Active Accounts Only**: Only active accounts are returned; inactive or deleted accounts are excluded
5. **No Pagination**: All matching results are returned in a single response
6. **Permissions Required**: User must have read access to the Account entity in Dynamics 365

## Limitations

1. **No Advanced Filtering**: Cannot filter by other criteria (e.g., location, industry, account type)
2. **No Sorting Options**: Results are returned in the default order from Dynamics 365 API
3. **No Pagination**: Large result sets are returned all at once, which may impact performance
4. **Limited Fields**: Only Account Name, Address, and Account Number are returned
5. **No Fuzzy Matching**: Search requires exact substring match (no typo tolerance)
6. **Performance**: Searching with very short terms (1-2 characters) may be slow with large datasets

## Best Practices

### 1. Use Specific Search Terms
Use at least 3-4 characters for meaningful search results.

**Recommendation**: Avoid single-character searches that may return too many results.

### 2. Refine Searches Progressively
Start with a broader search and refine if too many results are returned.

**Example**: Search "Tech" first, then "Tech Solutions" if needed.

### 3. Cache Frequently Used Accounts
For accounts that are accessed repeatedly, cache the account information.

**Benefit**: Reduces API calls and improves performance.

### 4. Validate Selected Account
After user selects an account from the list, validate it still exists before using it.

**Reason**: Accounts may be deleted or deactivated between search and use.

### 5. Handle Empty Results Gracefully
Provide clear feedback when no accounts are found.

**User Experience**: Suggest checking spelling or trying a different search term.

## Common Use Cases

### 1. Case Creation - Account Selection
```
Scenario: User creating a case needs to select the customer account
Action:
  1. User enters partial account name
  2. Call "Pick Account Using Name"
  3. Display list of matching accounts
  4. User selects the correct account
  5. Use Account ID for case creation
Result: Case is associated with the correct customer account
```

### 2. Contact Association
```
Scenario: Associate a contact with an existing account
Action:
  1. Search for account by name
  2. Display matching accounts
  3. User selects the parent account
  4. Create or update contact with selected Account ID
Result: Contact is properly linked to the account
```

### 3. Account Lookup for Reporting
```
Scenario: Generate report for a specific customer
Action:
  1. Search for customer account by name
  2. Retrieve account details
  3. Use Account ID to query related data (cases, contacts, assets)
  4. Generate comprehensive customer report
Result: Complete customer activity report
```

### 4. Duplicate Account Detection
```
Scenario: Check if account already exists before creating new one
Action:
  1. Search for account by proposed name
  2. Review results for potential duplicates
  3. If duplicate found, use existing account
  4. If not found, proceed with account creation
Result: Prevents duplicate account creation
```

## Related Catalog Requests

- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Retrieve full account details by ID
- [Get Contacts By Company Id](pages/GetContactsByCompanyId.md) - Get contacts for a selected account
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Retrieve cases for an account
- [Get WorkOrder By AccountId](pages/GetWorkOrderByAccountId.md) - Get work orders for an account
- [Get Customer Assets by Account Id](pages/GetCustomerAssetsByAccountId.md) - Retrieve assets for an account

## Technical Implementation

### Helper Class
- **Class**: CaseManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `pickAccountUsingName()`
- **Service**: AccountDaoImpl.searchAccountsByName()

### Implementation Details

The catalog request is implemented in the `CaseManagementArea` class and performs a query against the Dynamics 365 Account entity using the Web API.

### API Endpoint

The request calls the Dynamics 365 Web API:
- **Endpoint**: `/api/data/v9.2/accounts`
- **Method**: GET
- **Query Parameters**: `$filter=contains(name, '{accountName}')`
- **Authentication**: OAuth 2.0 Bearer token

## Troubleshooting

### Issue: "No Accounts Found"

**Cause**: No accounts match the search criteria

**Solution**:
1. Verify the account name is spelled correctly
2. Try a shorter or more general search term
3. Check if the account exists in Dynamics 365
4. Verify the account is active (not deleted or deactivated)
5. Ensure you have permission to view the account

### Issue: "Too Many Results"

**Cause**: Search term is too generic

**Solution**:
1. Use a more specific search term
2. Include more characters from the account name
3. Add distinguishing words (e.g., "Inc", "Corp", location)
4. Consider implementing client-side filtering

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
- [Customer Entity](pages/CustomerEntity.md) - Create, update, and manage customer accounts
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

