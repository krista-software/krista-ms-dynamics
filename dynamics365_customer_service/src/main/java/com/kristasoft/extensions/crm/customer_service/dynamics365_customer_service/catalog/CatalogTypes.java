/*
 * Ms Dynamics Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog;

import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsCase;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsCustomer;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Account;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Case;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Contact;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getKeyFromValue;

public class CatalogTypes {
    private CatalogTypes() {
    }

    public static MsDynamicsCase fromDynamicsToKristaEntity(Case dCase) {
        if (dCase == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        MsDynamicsCase newCase = new MsDynamicsCase();
        newCase.caseTitle = dCase.getCaseTitle();
        newCase.caseNumber = dCase.getCaseNumber();
        newCase.caseId = dCase.getCaseId();
        newCase.caseId = dCase.getCaseId();
        newCase.contactId = dCase.getContactId();
        newCase.subjectId = dCase.getSubjectId();
        newCase.description = dCase.getDescription();
        newCase.priority = getKeyFromValue("Priority", dCase.getPriorityCode());
        newCase.caseStatus = getKeyFromValue("Case Status", dCase.getCaseStatusCode());

        return newCase;
    }

    public static MsDynamicsContact fromDynamicsToKristaEntity(Contact dynamicsContact) {
        if (dynamicsContact == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        MsDynamicsContact newContact = new MsDynamicsContact();
        newContact.firstName = dynamicsContact.getFirstName();
        newContact.lastName = dynamicsContact.getLastName();
        newContact.id = dynamicsContact.getId();
        newContact.accountId = dynamicsContact.getAccountId();
        newContact.mobilePhone = dynamicsContact.getMobilePhone();
        newContact.fax = dynamicsContact.getFax();
        newContact.street1 = dynamicsContact.getStreet1();
        newContact.street2 = dynamicsContact.getStreet2();
        newContact.street3 = dynamicsContact.getStreet3();
        newContact.city = dynamicsContact.getCity();
        newContact.state = dynamicsContact.getState();
        newContact.postalCode = dynamicsContact.getPostalCode();
        newContact.country = dynamicsContact.getCountry();
        newContact.email = dynamicsContact.getEmail();
        newContact.contactMethod = getKeyFromValue("Contact Method", dynamicsContact.getContactMethodCode());
        return newContact;
    }

    public static MsDynamicsCustomer fromDynamicsToKristaEntity(Account dynamicsAccount) {
        if (dynamicsAccount == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        MsDynamicsCustomer customer = new MsDynamicsCustomer();
        customer.customerId = dynamicsAccount.getId();
        customer.customerName = dynamicsAccount.getName();
        customer.phone = dynamicsAccount.getPhone();
        customer.fax = dynamicsAccount.getFax();
        customer.website = dynamicsAccount.getWebsite();
        customer.primaryContactId = dynamicsAccount.getPrimaryContactId();
        customer.parentCustomerId = dynamicsAccount.getParentCustomerId();
        customer.street1 = dynamicsAccount.getStreet1();
        customer.street2 = dynamicsAccount.getStreet2();
        customer.street3 = dynamicsAccount.getStreet3();
        customer.city = dynamicsAccount.getCity();
        customer.state = dynamicsAccount.getState();
        customer.postalCode = dynamicsAccount.getPostalCode();
        customer.country = dynamicsAccount.getCountry();

        return customer;
    }
}
