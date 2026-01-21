# Case Entity

## Overview

The Case entity in Microsoft Dynamics 365 Customer Service represents customer service cases (also known as incidents). This entity enables comprehensive case management including creating new cases, searching for existing cases, updating case information, and managing case resolution.

## Entity Operations

The Case entity supports the following operations:

- **Search Case** - Find cases by title, number, or ID
- **Create Case** - Create new customer service cases
- **Update Case** - Modify existing case information
- **Delete Case** - Remove cases

---

## Search Case

### Description

Retrieves a list of cases from Dynamics 365 based on search criteria. You can search by Case Title, Case Number, or Case ID.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Case Title | Text | No | Title or subject of the case | `Title` |
| Case Number | Text | No | Unique case number | `CAS-47732-V4V6K6` |
| Case Id | Text | No | Unique identifier of the case | `6194b723-7e5f-eb11-a812-000d3a1a658` |

**Note**: At least one search parameter should be provided for meaningful results.

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Case | List<Case> | List of case entities matching the search criteria |

### Usage Example

**Scenario**: Search for cases by case number

**Input**:
```
Case Number: "CAS-47732-V4V6K6"
```

**Output**:
```json
{
  "Case": [
    {
      "Case Id": "6194b723-7e5f-eb11-a812-000d3a1a658",
      "Case Number": "CAS-47732-V4V6K6",
      "Case Title": "Product not working",
      "Case Status": "In Progress",
      "Priority": "High",
      "Created On": "2024-10-01T10:30:00Z"
    }
  ]
}
```

---

## Create Case

### Description

Creates a new case in Microsoft Dynamics 365 Customer Service and returns the created case entity.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Case Title | Text | Yes | Title or subject of the case | `Title` |
| Contact Id | Text | Yes | Identifier of the contact person | `0055w00000D9a8iAAC` |
| Subject Id | Text | No | Identifier of the case subject/category | `0015w00002inu5PAAP` |
| Priority | Pick One | No | Priority level of the case | `High` |
| Case Status | Pick One | No | Current status of the case | `In Progress` |
| Description | Rich Text | No | Detailed description of the case | `Description of the case` |

### Priority Options

- High
- Normal
- Low

### Case Status Options

- Active
- In Progress
- On Hold
- Waiting for Details
- Researching
- Resolved
- Canceled

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Case | Case | The newly created case entity |

### Important Notes

- The above fields are the only supported input fields. Any other field present in the entity is for internal purpose only.
- Providing invalid input for any parameter will result in a "Bad Request" error.
- Case Title and Contact Id are required fields.
- Subject Id should reference a valid subject from "Get Subjects for Case" catalog request.

### Usage Example

**Scenario**: Create a new customer service case

**Input**:
```
Case Title: "Software installation issue"
Contact Id: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Subject Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
Priority: "High"
Case Status: "In Progress"
Description: "Customer is unable to install the software on Windows 11"
```

**Output**:
```json
{
  "Case": {
    "Case Id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "Case Number": "CAS-00125-X7Y8Z9",
    "Case Title": "Software installation issue",
    "Contact Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
    "Subject Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Priority": "High",
    "Case Status": "In Progress",
    "Description": "Customer is unable to install the software on Windows 11",
    "Created On": "2024-10-08T14:30:00Z"
  }
}
```

---

## Update Case

### Description

Updates an existing case with the provided details and returns the updated case entity.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Case Id | Text | Yes | Unique identifier of the case to update | `6194b723-7e5f-eb11-a812-000d3a1a658` |
| Case Title | Text | Yes | Title or subject of the case | `Title` |
| Contact Id | Text | Yes | Identifier of the contact person | `0055w00000D9a8iAAC` |
| Subject Id | Text | No | Identifier of the case subject/category | `0015w00002inu5PAAP` |
| Priority | Pick One | No | Priority level of the case | `High` |
| Case Status | Pick One | No | Current status of the case | `In Progress` |
| Product ID | Text | No | Identifier of the related product | `0055w00000D9a8iAAC` |
| Description | Rich Text | No | Detailed description of the case | `Description of the case` |

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Case | Case | The updated case entity |

### Important Notes

- Case Id, Case Title, and Contact Id are required for updates.
- Only the fields you provide will be updated; other fields remain unchanged.
- Providing an invalid Case Id will result in a "Not Found" error.

### Usage Example

**Scenario**: Update case status and add product information

**Input**:
```
Case Id: "6194b723-7e5f-eb11-a812-000d3a1a658"
Case Title: "Software installation issue"
Contact Id: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Case Status: "Resolved"
Product ID: "c3d4e5f6-a7b8-9012-cdef-123456789012"
Description: "Issue resolved by providing Windows 11 compatible installer"
```

**Output**:
```json
{
  "Case": {
    "Case Id": "6194b723-7e5f-eb11-a812-000d3a1a658",
    "Case Number": "CAS-00125-X7Y8Z9",
    "Case Title": "Software installation issue",
    "Contact Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
    "Case Status": "Resolved",
    "Product ID": "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "Description": "Issue resolved by providing Windows 11 compatible installer",
    "Modified On": "2024-10-08T16:45:00Z"
  }
}
```

---

## Delete Case

### Description

Deletes a case from the Microsoft Dynamics 365 CRM instance.

### Input Parameters

| Parameter Name | Type | Required | Description |
|----------------|------|----------|-------------|
| Case | Case | Yes | The case entity to delete |

### Output Parameters

No output parameters. The operation returns success or error status.

### Important Notes

- Deleting a case is permanent and cannot be undone.
- Ensure you have proper permissions to delete case records.
- Related records (activities, notes, attachments) may be affected based on your Dynamics 365 configuration.
- Consider resolving or canceling cases instead of deleting to maintain historical data.

### Usage Example

**Scenario**: Delete a duplicate case

**Input**:
```
Case: {
  "Case Id": "b2c3d4e5-f6a7-8901-bcde-f12345678901"
}
```

**Result**: Case is permanently deleted from Dynamics 365.

---

## Business Rules

1. **Case Title Required**: Case Title is mandatory for create and update operations
2. **Contact Association**: Every case must be associated with a valid contact
3. **Unique Case Number**: System automatically generates unique case numbers
4. **Subject Categorization**: Subject Id helps categorize and route cases
5. **Priority Levels**: Priority determines case handling urgency
6. **Status Workflow**: Case Status follows a defined workflow (Active → In Progress → Resolved)
7. **Data Integrity**: Deleting a case may affect related activities and notes

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Case Title is empty (Create/Update) | "Case Title is required" | Provide a valid case title |
| Contact Id is empty (Create/Update) | "Contact Id is required" | Provide a valid contact identifier |
| Case Id is invalid (Update/Delete) | "Case not found" | Verify the Case Id exists |
| Invalid Subject Id | "Subject not found" | Use "Get Subjects for Case" to get valid subjects |
| Invalid Priority value | "Invalid priority value" | Use: High, Normal, or Low |
| Invalid Case Status value | "Invalid case status" | Use valid status from the list |

## Error Handling

### Common Errors

**Bad Request (400)**
- Invalid input parameters
- Missing required fields (Case Title, Contact Id)
- Invalid data format
- Invalid priority or status values

**Not Found (404)**
- Case Id doesn't exist
- Referenced entity (contact, subject, product) not found

**Forbidden (403)**
- Insufficient permissions to perform operation
- Security role doesn't allow case management

**Conflict (409)**
- Concurrent update conflict
- Case is locked by another user

## Best Practices

### 1. Use Subjects for Categorization
Always assign a Subject Id to cases for proper categorization and routing.

**Benefit**: Enables better case organization and reporting.

### 2. Set Appropriate Priority
Assign priority based on business impact and urgency.

**Guideline**: High for critical issues, Normal for standard requests, Low for minor inquiries.

### 3. Provide Detailed Descriptions
Include comprehensive information in the Description field.

**Benefit**: Helps support agents understand and resolve issues faster.

### 4. Update Status Regularly
Keep case status current to reflect actual progress.

**Workflow**: Active → In Progress → Resolved (or Canceled)

### 5. Associate with Products
Link cases to products when applicable for better tracking.

**Benefit**: Enables product-specific reporting and analysis.

### 6. Resolve Instead of Delete
Mark cases as Resolved or Canceled instead of deleting.

**Reason**: Maintains historical data for reporting and analysis.

## Common Use Cases

### 1. Customer Support Ticket Creation
```
Scenario: Customer calls with a product issue
Action:
  1. Search for customer contact
  2. Create case with issue details
  3. Assign appropriate subject and priority
  4. Route to support team
Result: Case is created and assigned for resolution
```

### 2. Case Status Tracking
```
Scenario: Update case as work progresses
Action:
  1. Search for case by case number
  2. Update status to "In Progress"
  3. Add notes about troubleshooting steps
  4. Update status to "Resolved" when complete
Result: Case lifecycle is properly tracked
```

### 3. Case Resolution
```
Scenario: Close a resolved case
Action:
  1. Update case status to "Resolved"
  2. Add resolution details in description
  3. Link to knowledge base article if applicable
  4. Notify customer of resolution
Result: Case is properly closed with documentation
```

## Related Operations

- [Get Subjects for Case](pages/GetSubjectsForCase.md) - Retrieve available case subjects
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Query cases for an account
- [Contact Entity](pages/ContactEntity.md) - Manage contacts associated with cases
- [Customer Entity](pages/CustomerEntity.md) - Manage customer accounts

## Technical Implementation

### API Endpoints

- **Search**: `GET /api/data/v9.2/incidents`
- **Create**: `POST /api/data/v9.2/incidents`
- **Update**: `PATCH /api/data/v9.2/incidents({id})`
- **Delete**: `DELETE /api/data/v9.2/incidents({id})`

### Authentication

All operations require OAuth 2.0 authentication with appropriate permissions for the Incident (Case) entity.

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Understanding authentication modes
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

