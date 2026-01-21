# Microsoft Dynamics 365 Customer Service Extension

## Overview

The Krista Microsoft Dynamics 365 Customer Service Extension enables seamless integration with Microsoft's comprehensive customer service platform. This extension connects to your Dynamics 365 Customer Service instance to perform operations related to case management, customer management, contact management, and field service operations.

Microsoft Dynamics 365 Customer Service is a powerful customer service solution that helps organizations deliver exceptional customer experiences across multiple channels including phone, email, chat, and social media platforms. The Krista extension leverages the Dynamics 365 Web API to provide automated workflows and intelligent automation capabilities.

## Key Features

- **Case Management** - Create, search, update, and manage customer service cases with full lifecycle support
- **Customer Management** - Comprehensive customer (account) operations including CRUD operations and asset tracking
- **Contact Management** - Manage customer contacts with detailed information and relationships
- **Field Service Integration** - Retrieve and analyze work orders for field service operations
- **Incident Tracking** - Query and analyze incidents with configurable date ranges and timeline details
- **OAuth 2.0 Authentication** - Secure authentication supporting both service account and user account modes
- **Flexible Date Ranges** - Query historical data with configurable time periods for comprehensive analysis
- **Entity Relationships** - Maintain relationships between accounts, contacts, cases, and assets

## Quick Start Guide

### Prerequisites

- Microsoft Dynamics 365 Customer Service instance
- Microsoft Entra ID (formerly Azure AD) tenant access
- Administrator privileges to create app registrations
- Valid Dynamics 365 user account with appropriate permissions

### Getting Started

1. **Create Microsoft Entra ID App Registration** - Follow the [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) guide
2. **Configure Extension** - Set up Client ID, Client Secret, Tenant ID, and Base URL using [Extension Configuration](pages/ExtensionConfiguration.md)
3. **Authenticate** - Choose between service account or user account authentication modes in [Authentication](pages/Authentication.md)
4. **Start Using Catalog Requests** - Explore available operations in the documentation below

## Documentation Structure

### Getting Started

- [Extension Configuration](pages/ExtensionConfiguration.md) - Complete setup and configuration guide
- [Authentication](pages/Authentication.md) - OAuth 2.0 authentication modes and token management
- [Creating Dynamics 365 App](pages/CreatingDynamics365App.md) - Step-by-step Microsoft Entra ID app registration

### Case Management

- [Get Subjects for Case](pages/GetSubjectsForCase.md) - Retrieve available case subjects
- [Pick Account Using Name](pages/PickAccountUsingName.md) - Search accounts by name
- [Get Contacts By Company Id](pages/GetContactsByCompanyId.md) - Retrieve contacts for an account
- [Get Lookup Account by Account Id](pages/GetLookupAccountByAccountId.md) - Fetch account details
- [Get WorkOrder By AccountId](pages/GetWorkOrderByAccountId.md) - Retrieve field service work orders
- [Get Incidents by Account ID](pages/GetIncidentsByAccountID.md) - Query customer service incidents

### Customer Management

- [Get Customer Assets by Account Id](pages/GetCustomerAssetsByAccountId.md) - Retrieve customer assets

### Entity Operations

- [Customer Entity](pages/CustomerEntity.md) - Search, create, update, and delete customers
- [Case Entity](pages/CaseEntity.md) - Search, create, update, and delete cases
- [Contact Entity](pages/ContactEntity.md) - Search, create, update, and delete contacts

### Additional Resources

- [Release Notes](pages/release-notes.md) - Version history and updates

## Support & Resources

- **Extension Version**: 2.0.7
- **Krista Service APIs Java**: 1.0.115
- **Global Catalog Version**: GC-2025.10.2

## Important Notes

**Token Expiration**: If the extension remains unused for more than 90 days, re-authentication will be required.

**Entity Migration**: The 'Get Customer Assets by Account Id' catalog request has been moved to the Customer Management area. Users with private implementations will need to reselect this request when switching to the global version.

## Need Help?

For detailed information about specific catalog requests, entity operations, or troubleshooting, please refer to the individual documentation pages linked above.
