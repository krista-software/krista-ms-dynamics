# Get Subjects for Case

## Overview

Retrieves a list of all available case subjects from your Dynamics 365 Customer Service instance. Subjects are used to categorize and organize customer service cases, making it easier to route, track, and report on different types of customer issues.

## Request Details

- **Area**: Case Management
- **Type**: QUERY_SYSTEM
- **Retry Support**: No - This is a simple query operation that either succeeds or fails immediately

## Input Parameters

This catalog request does not require any input parameters.

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| _(None)_ | - | - | No input parameters required | - |

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Subjects | List of Multi-Field | List of available subjects with their titles and identifiers |

### Subjects Structure

Each subject in the list contains:

| Field Name | Type | Description | Example |
|------------|------|-------------|---------|
| Subject Title | Text | Display name of the subject | "Product Support" |
| Subject Id | Text | Unique identifier (GUID) for the subject | "a1b2c3d4-e5f6-7890-abcd-ef1234567890" |

## Validation Rules

No validation is required as this request has no input parameters.

## Error Handling

### System Errors (SYSTEM_ERROR)

**Cause**: Connection issues or Dynamics 365 API errors

**Common Scenarios**:
- Network connectivity problems
- Dynamics 365 service unavailable
- Authentication token expired
- Insufficient permissions to read subject data

**Error Messages**:
- "Error occurred while getting subjects"
- "Failed to connect to Dynamics 365"
- "Authentication required"

**Resolution**:
1. Verify network connectivity to Dynamics 365
2. Check extension configuration and authentication
3. Ensure the service account has read permissions for subject entity
4. Re-validate the extension if authentication has expired
5. Contact your Dynamics 365 administrator if permissions are insufficient

### Authorization Errors

**Cause**: User or service account lacks permissions to read subjects

**Resolution**:
1. Verify the authenticated user has read access to the Subject entity in Dynamics 365
2. Check security roles assigned to the user/service account
3. Ensure the user has at least "Read" privilege on the Subject entity
4. Contact your Dynamics 365 administrator to grant appropriate permissions

## Usage Examples

### Example 1: Retrieve All Subjects

**Scenario**: Get a list of all available subjects to populate a dropdown in a case creation form

**Input**:
```
(No input parameters)
```

**Output**:
```json
{
  "Subjects": [
    {
      "Subject Title": "Product Support",
      "Subject Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    },
    {
      "Subject Title": "Billing Inquiry",
      "Subject Id": "b2c3d4e5-f6a7-8901-bcde-f12345678901"
    },
    {
      "Subject Title": "Technical Issue",
      "Subject Id": "c3d4e5f6-a7b8-9012-cdef-123456789012"
    },
    {
      "Subject Title": "General Question",
      "Subject Id": "d4e5f6a7-b8c9-0123-def1-234567890123"
    }
  ]
}
```

**Result**: Successfully retrieved 4 subjects that can be used for case categorization

### Example 2: Use in Case Creation Workflow

**Scenario**: Retrieve subjects to allow users to select the appropriate category when creating a new case

**Workflow**:
1. Call "Get Subjects for Case" to retrieve all available subjects
2. Display subject titles in a dropdown or selection list
3. User selects the appropriate subject
4. Use the Subject Id when creating the case with "Create Case" entity operation

**Result**: Users can properly categorize cases using predefined subjects

## Business Rules

1. **Read-Only Operation**: This request only retrieves data and does not modify any subjects
2. **All Active Subjects**: Returns all active subjects configured in your Dynamics 365 instance
3. **Hierarchical Subjects**: If your organization uses hierarchical subjects (parent-child relationships), all levels are returned
4. **No Filtering**: The request returns all subjects without any filtering options
5. **Cached Results**: Consider caching the results as subjects typically don't change frequently

## Limitations

1. **No Pagination**: All subjects are returned in a single response
2. **No Filtering**: Cannot filter subjects by specific criteria
3. **No Sorting**: Subjects are returned in the order provided by Dynamics 365 API
4. **Active Subjects Only**: Inactive or deleted subjects are not included
5. **No Hierarchy Information**: Parent-child relationships between subjects are not explicitly indicated in the response
6. **Performance**: For organizations with hundreds of subjects, the response may be large

## Best Practices

### 1. Cache Subject Data
Since subjects rarely change, cache the results to improve performance and reduce API calls.

**Recommendation**: Refresh the cache daily or when subjects are modified in Dynamics 365.

### 2. Validate Subject IDs
When using Subject IDs in case creation, validate that the ID exists in the retrieved list.

**Benefit**: Prevents errors when creating cases with invalid subject references.

### 3. Display User-Friendly Names
Always display the "Subject Title" to users, not the "Subject Id".

**Reason**: Subject IDs are GUIDs and not meaningful to end users.

### 4. Handle Empty Results
Implement logic to handle scenarios where no subjects are configured.

**Fallback**: Provide a default subject or notify administrators to configure subjects.

### 5. Error Handling
Implement robust error handling for network issues or permission errors.

**Recommendation**: Provide clear error messages to users and log detailed errors for troubleshooting.

## Common Use Cases

### 1. Case Creation Form
```
Scenario: Populate a subject dropdown in a case creation interface
Action: 
  1. Call "Get Subjects for Case"
  2. Extract Subject Titles for display
  3. Store Subject IDs for submission
  4. User selects subject from dropdown
  5. Submit case with selected Subject Id
Result: Case is created with proper subject categorization
```

### 2. Case Routing
```
Scenario: Route cases to appropriate teams based on subject
Action:
  1. Retrieve all subjects
  2. Map subjects to support teams
  3. When case is created, use subject to determine routing
Result: Cases are automatically routed to the correct team
```

### 3. Reporting and Analytics
```
Scenario: Generate reports showing case distribution by subject
Action:
  1. Get all subjects to create subject categories
  2. Query cases and group by subject
  3. Display analytics showing volume per subject
Result: Management insights into case types and volumes
```

### 4. Subject Validation
```
Scenario: Validate that a subject exists before creating a case
Action:
  1. Retrieve all subjects
  2. Check if the desired subject ID exists in the list
  3. If valid, proceed with case creation
  4. If invalid, prompt user to select a valid subject
Result: Prevents errors from invalid subject references
```

## Related Catalog Requests

- [Create Case](pages/CaseEntity.md#create-case) - Use retrieved Subject IDs when creating cases
- [Update Case](pages/CaseEntity.md#update-case) - Update case subject using Subject IDs
- [Search Case](pages/CaseEntity.md#search-case) - Search for cases by subject
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Retrieve cases which include subject information

## Technical Implementation

### Helper Class
- **Class**: CaseManagementArea
- **Package**: com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog
- **Method**: `getSubjectsForCase()`
- **Service**: CaseDaoImpl.getSubjects()

### Implementation Details

The catalog request is implemented in the `CaseManagementArea` class:

```java
@CatalogRequest(
    id = "localDomainRequest_6a4bc822-d991-4e0f-951a-e8e2c339c01f",
    name = "Get Subjects for Case",
    description = "This request will fetch the list of available subjects with their Identifier values.",
    area = "Case management",
    type = CatalogRequest.Type.QUERY_SYSTEM)
@Field.Desc(name = "Subjects", type = "[ { Subject Title: Text, Subject Id: Text } ]", required = false)
public List<Map<String, Object>> getSubjectsForCase() throws MalformedURLException {
    return caseDao.getSubjects();
}
```

### API Endpoint

The request calls the Dynamics 365 Web API:
- **Endpoint**: `/api/data/v9.2/subjects`
- **Method**: GET
- **Authentication**: OAuth 2.0 Bearer token

## Troubleshooting

### Issue: "No Subjects Returned"

**Cause**: No subjects are configured in Dynamics 365

**Solution**:
1. Log in to Dynamics 365 Customer Service
2. Navigate to Settings → Service Management → Subjects
3. Create at least one subject
4. Retry the request

### Issue: "Error occurred while getting subjects"

**Cause**: Connection or authentication issue

**Solution**:
1. Verify extension configuration is correct
2. Test the connection in extension setup
3. Re-validate authentication if needed
4. Check network connectivity to Dynamics 365
5. Review error logs for detailed error messages

### Issue: "Insufficient Permissions"

**Cause**: User or service account lacks read permissions for subjects

**Solution**:
1. Verify the user has a security role with read access to Subject entity
2. In Dynamics 365, go to Settings → Security → Security Roles
3. Edit the user's security role
4. Under Service tab, ensure "Subject" has at least "Read" privilege
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
- [Case Entity](pages/CaseEntity.md) - Create, update, and manage cases
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

