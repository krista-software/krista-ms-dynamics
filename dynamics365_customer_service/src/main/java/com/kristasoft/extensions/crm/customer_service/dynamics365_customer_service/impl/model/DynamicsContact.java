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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Contact;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getContactMethodFromKey;
import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getKeyFromValue;

public class DynamicsContact {

    @SerializedName("contactid")
    public String id;

    @SerializedName("firstname")
    public String firstName;

    @SerializedName("lastname")
    public String lastName;

    @SerializedName("_parentcustomerid_value")
    public String accountId;

    @SerializedName("mobilephone")
    public String mobilePhone;

    @SerializedName("fax")
    public String fax;

    @SerializedName("preferredcontactmethodcode")
    public String contactMethodCode;

    public String contactMethod;

    @SerializedName("address1_line1")
    public String street1;
    @SerializedName("address1_line2")
    public String street2;
    @SerializedName("address1_line3")
    public String street3;
    @SerializedName("address1_city")
    public String city;
    @SerializedName("address1_stateorprovince")
    public String state;
    @SerializedName("address1_postalcode")
    public String postalCode;
    @SerializedName("address1_country")
    public String country;

    @SerializedName("emailaddress1")
    public String email;

    @JsonIgnore
    @JsonProperty("@odata.etag")
    private String etag;

    public static DynamicsContact from(MsDynamicsContact dynamicsContact) {
        if (dynamicsContact == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        DynamicsContact contact = new DynamicsContact();
        contact.id = dynamicsContact.id;
        contact.firstName = dynamicsContact.firstName;
        contact.lastName = dynamicsContact.lastName;
        contact.accountId = dynamicsContact.accountId;
        contact.mobilePhone = dynamicsContact.mobilePhone;
        contact.fax = dynamicsContact.fax;
        contact.contactMethod = dynamicsContact.contactMethod;
        contact.contactMethodCode = getContactMethodFromKey(dynamicsContact.contactMethod);
        contact.street1 = dynamicsContact.street1;
        contact.street2 = dynamicsContact.street2;
        contact.street3 = dynamicsContact.street3;
        contact.city = dynamicsContact.city;
        contact.state = dynamicsContact.state;
        contact.postalCode = dynamicsContact.postalCode;
        contact.country = dynamicsContact.country;
        contact.email = dynamicsContact.email;

        return contact;
    }

    public static DynamicsContact from(Contact modelContact) {
        if (modelContact == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        DynamicsContact contact = new DynamicsContact();
        contact.id = modelContact.getId();
        contact.firstName = modelContact.getFirstName();
        contact.lastName = modelContact.getLastName();
        contact.accountId = modelContact.getAccountId();
        contact.mobilePhone = modelContact.getMobilePhone();
        contact.fax = modelContact.getFax();
        contact.contactMethod = getKeyFromValue("Contact Method", modelContact.getContactMethodCode());
        contact.street1 = modelContact.getStreet1();
        contact.street2 = modelContact.getStreet2();
        contact.street3 = modelContact.getStreet3();
        contact.city = modelContact.getCity();
        contact.state = modelContact.getState();
        contact.postalCode = modelContact.getPostalCode();
        contact.country = modelContact.getCountry();
        contact.email = modelContact.getEmail();

        return contact;

    }

}
