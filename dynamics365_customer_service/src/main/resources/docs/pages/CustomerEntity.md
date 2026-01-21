# Customer Entity

## Overview

The Customer entity in Microsoft Dynamics 365 Customer Service represents customer accounts (companies or organizations). This entity enables comprehensive customer account management including creating new customers, searching for existing customers, updating customer information, and managing customer relationships.

## Entity Operations

The Customer entity supports the following operations:

- **Search Customer** - Find customers by ID or name
- **Create Customer** - Create new customer accounts
- **Update Customer** - Modify existing customer information
- **Delete Customer** - Remove customer accounts

---

## Search Customer

### Description

Retrieves a list of customers from Dynamics 365 based on search criteria. You can search by Customer ID, Customer Name, or both.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Customer Id | Text | No | Unique identifier of the customer | `0065w00002AlDtDAAM` |
| Customer Name | Text | No | Name of the customer account | `Demo Customer` |

**Note**: At least one search parameter should be provided for meaningful results.

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Customer | List<Customer> | List of customer entities matching the search criteria |

### Usage Example

**Scenario**: Search for customers by name

**Input**:
```
Customer Name: "Demo"
```

**Output**:
```json
{
  "Customer": [
    {
      "Customer Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
      "Customer Name": "Demo Company Inc.",
      "Phone": "555-0100",
      "Website": "www.democompany.com"
    }
  ]
}
```

---

## Create Customer

### Description

Creates a new customer account in Microsoft Dynamics 365 Customer Service and returns the created customer entity.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Customer Name | Text | Yes | Name of the customer account | `Demo Customer` |
| Phone | Text | No | Primary phone number | `9898789878` |
| Fax | Text | No | Fax number | `9876568` |
| Website | Text | No | Company website URL | `www.example.com` |
| Primary Contact Id | Text | No | Identifier of the primary contact person | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |
| Parent Customer Id | Text | No | Identifier of the parent account (for hierarchies) | `2842cda6-5fd0-ed11-a7c7-iejudhei889d` |
| Street1 | Street | No | Address line 1 | `Street Name One` |
| Street2 | Street | No | Address line 2 | `Street Name Two` |
| Street3 | Street | No | Address line 3 | `Street Name Three` |
| City | City | No | City | `Pune` |
| State | State | No | State or province | `Maharashtra` |
| Postal Code | Postal Code | No | Postal or ZIP code | `411045` |
| Country | Country | No | Country | `India` |

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Customer | Customer | The newly created customer entity |

### Important Notes

- The above fields are the only supported input fields. Any other field present in the entity is for internal purpose only.
- Providing invalid input for any parameter will result in a "Bad Request" error.
- Customer Name is the only required field.

### Usage Example

**Scenario**: Create a new customer account

**Input**:
```
Customer Name: "New Tech Solutions"
Phone: "555-0200"
Website: "www.newtechsolutions.com"
Street1: "456 Innovation Drive"
City: "San Francisco"
State: "CA"
Postal Code: "94105"
Country: "United States"
```

**Output**:
```json
{
  "Customer": {
    "Customer Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Customer Name": "New Tech Solutions",
    "Phone": "555-0200",
    "Website": "www.newtechsolutions.com",
    "Street1": "456 Innovation Drive",
    "City": "San Francisco",
    "State": "CA",
    "Postal Code": "94105",
    "Country": "United States"
  }
}
```

---

## Update Customer

### Description

Updates an existing customer account with the provided details and returns the updated customer entity.

### Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Customer Id | Text | Yes | Unique identifier of the customer to update | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |
| Customer Name | Text | Yes | Name of the customer account | `Demo Customer` |
| Phone | Text | No | Primary phone number | `9898789878` |
| Fax | Text | No | Fax number | `9876568` |
| Website | Text | No | Company website URL | `www.example.com` |
| Primary Contact Id | Text | No | Identifier of the primary contact person | `2842cda6-5fd0-ed11-a7c7-000d3a1f6919` |
| Parent Customer Id | Text | No | Identifier of the parent account | `2842cda6-5fd0-ed11-a7c7-iejudhei889d` |
| Street1 | Street | No | Address line 1 | `Street Name One` |
| Street2 | Street | No | Address line 2 | `Street Name Two` |
| Street3 | Street | No | Address line 3 | `Street Name Three` |
| City | City | No | City | `Pune` |
| State | State | No | State or province | `Maharashtra` |
| Postal Code | Postal Code | No | Postal or ZIP code | `411045` |
| Country | Country | No | Country | `India` |

### Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Customer | Customer | The updated customer entity |

### Important Notes

- Both Customer Id and Customer Name are required for updates.
- Only the fields you provide will be updated; other fields remain unchanged.
- Providing an invalid Customer Id will result in a "Not Found" error.

### Usage Example

**Scenario**: Update customer phone number and address

**Input**:
```
Customer Id: "2842cda6-5fd0-ed11-a7c7-000d3a1f6919"
Customer Name: "Demo Company Inc."
Phone: "555-0999"
Street1: "789 New Address Street"
City: "New York"
State: "NY"
Postal Code: "10001"
```

**Output**:
```json
{
  "Customer": {
    "Customer Id": "2842cda6-5fd0-ed11-a7c7-000d3a1f6919",
    "Customer Name": "Demo Company Inc.",
    "Phone": "555-0999",
    "Street1": "789 New Address Street",
    "City": "New York",
    "State": "NY",
    "Postal Code": "10001"
  }
}
```

---

## Delete Customer

### Description

Deletes a customer account from the Microsoft Dynamics 365 CRM instance.

### Input Parameters

| Parameter Name | Type | Required | Description |
|----------------|------|----------|-------------|
| Customer | Customer | Yes | The customer entity to delete |

### Output Parameters

No output parameters. The operation returns success or error status.

### Important Notes

- Deleting a customer is permanent and cannot be undone.
- Ensure you have proper permissions to delete customer records.
- Related records (contacts, cases, etc.) may be affected based on your Dynamics 365 configuration.
- Consider deactivating instead of deleting if you need to maintain historical data.

### Usage Example

**Scenario**: Delete a customer account

**Input**:
```
Customer: {
  "Customer Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**Result**: Customer account is permanently deleted from Dynamics 365.

---

## Business Rules

1. **Customer Name Required**: Customer Name is mandatory for create and update operations
2. **Unique Identification**: Customer Id uniquely identifies each customer account
3. **Address Validation**: Address fields should follow proper formatting for the specified country
4. **Parent-Child Relationships**: Parent Customer Id enables account hierarchies
5. **Primary Contact**: Primary Contact Id must reference a valid contact entity
6. **Data Integrity**: Deleting a customer may affect related records (contacts, cases, assets)

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Customer Name is empty (Create/Update) | "Customer Name is required" | Provide a valid customer name |
| Customer Id is invalid (Update/Delete) | "Customer not found" | Verify the Customer Id exists |
| Invalid phone format | "Invalid phone number format" | Use valid phone number format |
| Invalid email format | "Invalid email format" | Provide a valid email address |
| Parent Customer Id doesn't exist | "Parent customer not found" | Verify the parent customer exists |

## Error Handling

### Common Errors

**Bad Request (400)**
- Invalid input parameters
- Missing required fields
- Invalid data format

**Not Found (404)**
- Customer Id doesn't exist
- Referenced entity (contact, parent customer) not found

**Forbidden (403)**
- Insufficient permissions to perform operation
- Security role doesn't allow customer management

**Conflict (409)**
- Duplicate customer name (if uniqueness is enforced)
- Concurrent update conflict

## Best Practices

### 1. Search Before Create
Always search for existing customers before creating new ones to avoid duplicates.

### 2. Validate Input Data
Validate phone numbers, email addresses, and postal codes before submission.

### 3. Use Parent-Child Relationships
Leverage Parent Customer Id for corporate hierarchies and subsidiaries.

### 4. Maintain Data Quality
Keep customer information up-to-date with regular reviews and updates.

### 5. Soft Delete When Possible
Consider deactivating customers instead of deleting to maintain historical data.

## Related Operations

- [Pick Account Using Name](pages/PickAccountUsingName.md) - Search for customers by name
- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Retrieve customer details
- [Get Contacts By Company Id](pages/GetContactsByCompanyId.md) - Get contacts for a customer
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Retrieve cases for a customer
- [Get Customer Assets by Account Id](pages/GetCustomerAssetsByAccountId.md) - Get assets for a customer

## Technical Implementation

### API Endpoints

- **Search**: `GET /api/data/v9.2/accounts`
- **Create**: `POST /api/data/v9.2/accounts`
- **Update**: `PATCH /api/data/v9.2/accounts({id})`
- **Delete**: `DELETE /api/data/v9.2/accounts({id})`

### Authentication

All operations require OAuth 2.0 authentication with appropriate permissions for the Account entity.

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure the extension
- [Authentication](pages/Authentication.md) - Understanding authentication modes
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Microsoft Entra ID setup guide

