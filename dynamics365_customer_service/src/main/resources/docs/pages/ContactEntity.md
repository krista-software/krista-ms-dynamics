# Contact Entity

## Overview

The Contact entity in Microsoft Dynamics 365 Customer Service represents individual people associated with customer accounts. This entity enables comprehensive contact management including creating new contacts, searching for existing contacts, updating contact information, and managing contact relationships with accounts.

## Entity Operations

The Contact entity supports the following operations:

- **Search Contact** - Find contacts by ID, last name, or account
- **Create Contact** - Create new contact records
- **Update Contact** - Modify existing contact information
- **Delete Contact** - Remove contacts

---

## Search Contact

### Description

Retrieves a list of contacts from Dynamics 365 based on search criteria. You can search by Contact ID, Last Name, or Account ID.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Id | Text | No | Unique identifier of the contact | `6194b723-7e5f-eb11-a812-000d3a1a658` |
| Last Name | Text | No | Last name of the contact | `Demo` |
| Account Id | Text | No | Identifier of the associated account | `6194b723-7e5f-eb11-a812-000d3a1a658` |

**Note**: At least one search parameter should be provided for meaningful results.

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Contact | List<Contact> | List of contact entities matching the search criteria |

### Usage Example

**Scenario**: Search for contacts by last name

**Input**:
```
Last Name: "Smith"
```

**Output**:
```json
{
  "Contact": [
    {
      "Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "First Name": "John",
      "Last Name": "Smith",
      "Email": "john.smith@example.com",
      "Mobile Phone": "555-0123",
      "Account Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
    },
    {
      "Id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "First Name": "Jane",
      "Last Name": "Smith",
      "Email": "jane.smith@example.com",
      "Mobile Phone": "555-0124",
      "Account Id": "3952deb7-6ge1-fc22-b8d8-111e4b2g7020"
    }
  ]
}
```

---

## Create Contact

### Description

Creates a new contact in Microsoft Dynamics 365 Customer Service and returns the created contact entity.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| First Name | Text | No | First name of the contact | `First` |
| Last Name | Text | Yes | Last name of the contact | `Last` |
| Account Id | Text | No | Identifier of the associated account | `6194b723-7e5f-eb11-a812-000d3a1a658` |
| Mobile Phone | Text | No | Mobile phone number | `989787878` |
| Fax | Text | No | Fax number | `78736837` |
| Contact Method | Pick One | No | Preferred contact method | `Email` |
| Street1 | Street | No | Address line 1 | `Street Name One` |
| Street2 | Street | No | Address line 2 | `Street Name Two` |
| Street3 | Street | No | Address line 3 | `Street Name Three` |
| City | City | No | City | `Pune` |
| State | State | No | State or province | `Maharashtra` |
| Postal Code | Postal Code | No | Postal or ZIP code | `411045` |
| Country | Country | No | Country | `India` |
| Email | Email | No | Email address | `mail@example.com` |

### Contact Method Options

- Email
- Phone
- Fax
- Mail

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Contact | Contact | The newly created contact entity |

### Important Notes

- Providing invalid input for any parameter will result in a "Bad Request" error.
- Last Name is the only required field.
- Email must be in valid email format.
- Account Id should reference a valid customer account.

### Usage Example

**Scenario**: Create a new contact for a customer account

**Input**:
```
First Name: "Michael"
Last Name: "Johnson"
Account Id: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Email: "michael.johnson@democompany.com"
Mobile Phone: "555-0300"
Contact Method: "Email"
Street1: "123 Business Plaza"
City: "New York"
State: "NY"
Postal Code: "10001"
Country: "United States"
```

**Output**:
```json
{
  "Contact": {
    "Id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "First Name": "Michael",
    "Last Name": "Johnson",
    "Account Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
    "Email": "michael.johnson@democompany.com",
    "Mobile Phone": "555-0300",
    "Contact Method": "Email",
    "Street1": "123 Business Plaza",
    "City": "New York",
    "State": "NY",
    "Postal Code": "10001",
    "Country": "United States"
  }
}
```

---

## Update Contact

### Description

Updates an existing contact with the provided details and returns the updated contact entity.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Id | Text | Yes | Unique identifier of the contact to update | `6194b723-7e5f-eb11-a812-000d3a1a658` |
| First Name | Text | No | First name of the contact | `First` |
| Last Name | Text | Yes | Last name of the contact | `Last` |
| Account Id | Text | No | Identifier of the associated account | `6194b723-7e5f-eb11-a812-000d3a1a658` |
| Mobile Phone | Text | No | Mobile phone number | `989787878` |
| Fax | Text | No | Fax number | `78736837` |
| Contact Method | Pick One | No | Preferred contact method | `Email` |
| Street1 | Street | No | Address line 1 | `Street Name One` |
| Street2 | Street | No | Address line 2 | `Street Name Two` |
| Street3 | Street | No | Address line 3 | `Street Name Three` |
| City | City | No | City | `Pune` |
| State | State | No | State or province | `Maharashtra` |
| Postal Code | Postal Code | No | Postal or ZIP code | `411045` |
| Country | Country | No | Country | `India` |
| Email | Email | No | Email address | `mail@example.com` |

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Contact | Contact | The updated contact entity |

### Important Notes

- Both Id and Last Name are required for updates.
- Only the fields you provide will be updated; other fields remain unchanged.
- Providing an invalid Id will result in a "Not Found" error.

### Usage Example

**Scenario**: Update contact email and phone number

**Input**:
```
Id: "c3d4e5f6-a7b8-9012-cdef-123456789012"
Last Name: "Johnson"
Email: "m.johnson@newcompany.com"
Mobile Phone: "555-0999"
Contact Method: "Phone"
```

**Output**:
```json
{
  "Contact": {
    "Id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "First Name": "Michael",
    "Last Name": "Johnson",
    "Email": "m.johnson@newcompany.com",
    "Mobile Phone": "555-0999",
    "Contact Method": "Phone"
  }
}
```

---

## Delete Contact

### Description

Deletes a contact from the Microsoft Dynamics 365 CRM instance.

### Input Parameters

| Parameter Name | Type | Required | Description |
|----------------|------|----------|-------------|
| Contact | Contact | Yes | The contact entity to delete |

### Output Parameters

No output parameters. The operation returns success or error status.

### Important Notes

- Deleting a contact is permanent and cannot be undone.
- Ensure you have proper permissions to delete contact records.
- Related records (cases, activities) may be affected based on your Dynamics 365 configuration.
- Consider deactivating instead of deleting if you need to maintain historical data.

### Usage Example

**Scenario**: Delete a duplicate contact

**Input**:
```
Contact: {
  "Id": "d4e5f6a7-b8c9-0123-def1-234567890123"
}
```

**Result**: Contact is permanently deleted from Dynamics 365.

---

## Business Rules

1. **Last Name Required**: Last Name is mandatory for create and update operations
2. **Unique Identification**: Contact Id uniquely identifies each contact
3. **Account Association**: Contacts can be associated with customer accounts via Account Id
4. **Email Validation**: Email must be in valid format (user@domain.com)
5. **Contact Method**: Preferred contact method helps in communication planning
6. **Address Validation**: Address fields should follow proper formatting
7. **Data Integrity**: Deleting a contact may affect related cases and activities

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Last Name is empty (Create/Update) | "Last Name is required" | Provide a valid last name |
| Id is invalid (Update/Delete) | "Contact not found" | Verify the Contact Id exists |
| Invalid email format | "Invalid email format" | Provide a valid email address (user@domain.com) |
| Invalid phone format | "Invalid phone number format" | Use valid phone number format |
| Account Id doesn't exist | "Account not found" | Verify the account exists |
| Invalid Contact Method | "Invalid contact method" | Use: Email, Phone, Fax, or Mail |

## Error Handling

### Common Errors

**Bad Request (400)**
- Invalid input parameters
- Missing required field (Last Name)
- Invalid email format
- Invalid data format

**Not Found (404)**
- Contact Id doesn't exist
- Referenced account not found

**Forbidden (403)**
- Insufficient permissions to perform operation
- Security role doesn't allow contact management

**Conflict (409)**
- Duplicate email address (if uniqueness is enforced)
- Concurrent update conflict

## Best Practices

### 1. Associate with Accounts
Always link contacts to their respective customer accounts.

**Benefit**: Enables better relationship management and reporting.

### 2. Validate Email Addresses
Ensure email addresses are valid before creating or updating contacts.

**Recommendation**: Use email validation regex or service.

### 3. Set Preferred Contact Method
Specify the contact's preferred communication method.

**Benefit**: Improves customer satisfaction by using preferred channels.

### 4. Maintain Complete Information
Collect and maintain comprehensive contact information.

**Fields**: Name, email, phone, address, and account association.

### 5. Search Before Create
Search for existing contacts before creating new ones to avoid duplicates.

**Method**: Search by email or last name + account.

### 6. Soft Delete When Possible
Consider deactivating contacts instead of deleting.

**Reason**: Maintains historical data for cases and activities.

## Common Use Cases

### 1. Contact Creation for New Customer
```
Scenario: New customer account needs primary contact
Action:
  1. Create customer account
  2. Create contact with account association
  3. Set as primary contact for account
  4. Use contact for case creation
Result: Customer account has proper contact information
```

### 2. Contact Information Update
```
Scenario: Contact changes email or phone number
Action:
  1. Search for contact by name or ID
  2. Update email and/or phone number
  3. Update preferred contact method if needed
  4. Verify changes saved correctly
Result: Contact information is current and accurate
```

### 3. Find All Contacts for Account
```
Scenario: View all contacts associated with a customer
Action:
  1. Get Account Id
  2. Search contacts by Account Id
  3. Display list of all contacts
  4. Identify primary contact
Result: Complete view of account relationships
```

### 4. Case Assignment to Contact
```
Scenario: Create case for specific contact person
Action:
  1. Search for contact by name
  2. Verify contact details
  3. Create case with Contact Id
  4. Case is linked to correct person
Result: Case is properly associated with contact
```

## Related Operations

- [Get Contacts By Company Id](pages/GetContactsByCompanyId.md) - Retrieve all contacts for an account
- [Customer Entity](pages/CustomerEntity.md) - Manage customer accounts
- [Case Entity](pages/CaseEntity.md) - Create cases associated with contacts
- [Pick Account Using Name](PickAccountUsingName.md) - Find accounts to associate with contacts

## Technical Implementation

### API Endpoints

- **Search**: `GET /api/data/v9.2/contacts`
- **Create**: `POST /api/data/v9.2/contacts`
- **Update**: `PATCH /api/data/v9.2/contacts({id})`
- **Delete**: `DELETE /api/data/v9.2/contacts({id})`

### Authentication

All operations require OAuth 2.0 authentication with appropriate permissions for the Contact entity.

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Understanding authentication modes
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

