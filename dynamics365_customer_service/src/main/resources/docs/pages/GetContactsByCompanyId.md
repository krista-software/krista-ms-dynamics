# Get Contacts By Company Id

## Overview

Retrieves all contacts associated with a specific company (account) in Microsoft Dynamics 365 Customer Service. This request enables users to view all contact persons related to a customer account, making it easy to identify key stakeholders, decision-makers, and other individuals associated with the organization.

## Request Details

- **Area**: Case Management
- **Type**: CHANGE_SYSTEM
- **Retry Support**: No - Query operations are idempotent and don't require retry logic

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Company Id | Text | Yes | Unique identifier (GUID) of the account in Dynamics 365 | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |

### Parameter Details

#### Company Id
- **Format**: GUID (Globally Unique Identifier)
- **Validation**: Must be a valid GUID format
- **Purpose**: Identifies the customer account for which to retrieve contacts
- **How to Obtain**: Use "Pick account using Name" or "Get Lookup Account by Account Id" catalog requests

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| List of Contact | List of Entity(Contact) | Collection of contact records associated with the specified account |

### Contact Entity Structure

Each contact in the list is a full Contact entity containing all contact information including:

| Field Name | Type | Description |
|------------|------|-------------|
| Id | Text | Unique identifier of the contact |
| First Name | Text | Contact's first name |
| Last Name | Text | Contact's last name |
| Account Id | Text | Associated account identifier |
| Email | Email | Contact's email address |
| Mobile Phone | Text | Contact's mobile phone number |
| Fax | Text | Contact's fax number |
| Contact Method | Pick One | Preferred contact method |
| Address | Multi-Field | Complete address information |

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Company Id is null or empty | "Company Id is required" | Provide a valid Company Id |
| Company Id is not a valid GUID | "Invalid Company Id format" | Ensure Company Id is a properly formatted GUID |

## Error Handling

### Input Errors (INPUT_ERROR)

**Cause**: Invalid or missing required parameters

**Common Scenarios**:
- Company Id is null or empty
- Company Id is not a valid GUID format

**Error Messages**:
- "Company Id is required"
- "Invalid Company Id format"

**Resolution**:
1. Verify Company Id is provided
2. Ensure Company Id is a valid GUID format
3. Use "Pick account using Name" to get a valid Company Id

### Logic Errors (LOGIC_ERROR)

**Cause**: Business logic validation failures

**Common Scenarios**:
- Company (account) does not exist in Dynamics 365
- Company Id is valid but account has been deleted

**Error Messages**:
- "Company not found"
- "Account does not exist"

**Resolution**:
1. Verify the Company Id is correct
2. Check if the account exists in Dynamics 365
3. Ensure the account is active (not deleted or deactivated)
4. Use "Get Lookup Account by Account Id" to verify account exists

### System Errors (SYSTEM_ERROR)

**Cause**: Dynamics 365 API errors or connection issues

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 service unavailable
- Authentication token expired
- Insufficient permissions to read contact data

**Error Messages**:
- "Failed to retrieve contacts"
- "Connection to Dynamics 365 failed"
- "Authentication required"

**Resolution**:
1. Verify network connectivity to Dynamics 365
2. Check extension configuration and authentication
3. Re-validate the extension if authentication expired
4. Ensure the service account has read permissions for Contact entity
5. Check Dynamics 365 service health status

## Usage Examples

### Example 1: Retrieve All Contacts for an Account

**Scenario**: Get all contact persons for a customer account

**Input**:
```
Company Id: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
```

**Output**:
```json
{
  "List of Contact": [
    {
      "Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "First Name": "John",
      "Last Name": "Smith",
      "Account Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
      "Email": "john.smith@democompany.com",
      "Mobile Phone": "555-0123",
      "Contact Method": "Email"
    },
    {
      "Id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "First Name": "Jane",
      "Last Name": "Doe",
      "Account Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
      "Email": "jane.doe@democompany.com",
      "Mobile Phone": "555-0124",
      "Contact Method": "Phone"
    }
  ]
}
```

**Result**: Retrieved 2 contacts associated with the account

### Example 2: Account with No Contacts

**Scenario**: Retrieve contacts for an account that has no associated contacts

**Input**:
```
Company Id: "3952deb7-6ge1-fc22-b8d8-111e4b2g7020"
```

**Output**:
```json
{
  "List of Contact": []
}
```

**Result**: No contacts found for this account (empty list)

### Example 3: Use in Case Creation Workflow

**Scenario**: When creating a case, retrieve contacts to select the primary contact

**Workflow**:
1. User selects customer account
2. Call "Get Contacts By Company Id" with the account's Company Id
3. Display list of contacts to user
4. User selects the appropriate contact
5. Create case with selected Contact Id

**Result**: Case is created with the correct customer and contact association

## Business Rules

1. **Account Association**: Only contacts directly associated with the specified account are returned
2. **Active Contacts Only**: Only active contacts are returned; inactive or deleted contacts are excluded
3. **Read-Only Operation**: This request only retrieves data and does not modify any contacts
4. **No Pagination**: All contacts for the account are returned in a single response
5. **Full Entity**: Complete Contact entity information is returned, not just basic fields
6. **Permissions Required**: User must have read access to the Contact entity in Dynamics 365

## Limitations

1. **No Filtering**: Cannot filter contacts by specific criteria (e.g., role, department)
2. **No Sorting**: Contacts are returned in the default order from Dynamics 365 API
3. **No Pagination**: All contacts are returned at once, which may impact performance for accounts with many contacts
4. **Single Account Only**: Can only query one account at a time
5. **No Hierarchy**: Does not include contacts from parent or child accounts
6. **Performance**: Accounts with hundreds of contacts may experience slower response times

## Best Practices

### 1. Validate Company Id Before Use
Verify the Company Id exists before retrieving contacts.

**Recommendation**: Use "Get Lookup Account by Account Id" to validate the account first.

### 2. Handle Empty Results
Implement logic to handle accounts with no contacts.

**User Experience**: Display a message like "No contacts found for this account" rather than showing an empty list.

### 3. Cache Contact Lists
For frequently accessed accounts, cache the contact list.

**Benefit**: Reduces API calls and improves performance.

### 4. Display User-Friendly Information
Show contact names and email addresses prominently in the UI.

**Reason**: Makes it easy for users to identify and select the correct contact.

### 5. Implement Search/Filter on Client Side
For accounts with many contacts, provide client-side search functionality.

**Enhancement**: Allow users to filter contacts by name, email, or role.

## Common Use Cases

### 1. Case Creation - Contact Selection
```
Scenario: User creating a case needs to select the primary contact
Action:
  1. User selects customer account
  2. Call "Get Contacts By Company Id"
  3. Display list of contacts
  4. User selects the appropriate contact
  5. Create case with selected Contact Id
Result: Case is associated with the correct contact person
```

### 2. Account Overview Dashboard
```
Scenario: Display all key contacts for a customer account
Action:
  1. Retrieve account information
  2. Call "Get Contacts By Company Id"
  3. Display contacts in account overview
  4. Show contact details (name, email, phone)
Result: Complete view of account relationships
```

### 3. Contact Directory
```
Scenario: Generate a contact directory for a specific customer
Action:
  1. Get Company Id for the customer
  2. Retrieve all contacts
  3. Format contact information
  4. Export to PDF or Excel
Result: Printable contact directory for the customer
```

### 4. Email Campaign Targeting
```
Scenario: Send targeted communication to all contacts at a company
Action:
  1. Identify target account
  2. Retrieve all contacts
  3. Extract email addresses
  4. Send personalized emails to each contact
Result: Targeted communication to all stakeholders
```

## Related Catalog Requests

- [Pick Account Using Name](pages/PickAccountUsingName.md) - Find the Company Id by account name
- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Verify account exists and get details
- [Contact Entity](pages/ContactEntity.md) - Create, update, and manage individual contacts
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Retrieve cases for the same account
- [Get Customer Assets by Account Id](pages/GetCustomerAssetsByAccountId.md) - Get assets for the account

## Technical Implementation

### Helper Class
- **Class**: CaseManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `getContactsByCompanyId()`
- **Service**: ContactDaoImpl.getContactsByAccountId()

### Implementation Details

The catalog request queries the Dynamics 365 Contact entity filtered by the parent account relationship.

### API Endpoint

The request calls the Dynamics 365 Web API:
- **Endpoint**: `/api/data/v9.2/contacts`
- **Method**: GET
- **Query Parameters**: `$filter=_parentcustomerid_value eq '{companyId}'`
- **Authentication**: OAuth 2.0 Bearer token

## Troubleshooting

### Issue: "No Contacts Found"

**Cause**: Account has no associated contacts

**Solution**:
1. Verify the Company Id is correct
2. Check if contacts exist for this account in Dynamics 365
3. Ensure contacts are active (not deleted or deactivated)
4. Verify the account-contact relationship is properly established
5. Consider creating contacts if none exist

### Issue: "Company Not Found"

**Cause**: Company Id does not exist or account has been deleted

**Solution**:
1. Verify the Company Id is correct
2. Use "Pick account using Name" to find the correct account
3. Check if the account has been deleted in Dynamics 365
4. Ensure you're using the correct Dynamics 365 instance

### Issue: "Insufficient Permissions"

**Cause**: User or service account lacks read access to Contact entity

**Solution**:
1. Verify the user has a security role with read access to Contact entity
2. In Dynamics 365, go to Settings → Security → Security Roles
3. Edit the user's security role
4. Under Core Records tab, ensure "Contact" has at least "Read" privilege
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
- [Contact Entity](pages/ContactEntity.md) - Create, update, and manage contacts
- [Customer Entity](pages/CustomerEntity.md) - Manage customer accounts
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

