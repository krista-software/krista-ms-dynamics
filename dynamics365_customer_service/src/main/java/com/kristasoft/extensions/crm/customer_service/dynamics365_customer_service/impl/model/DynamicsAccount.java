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
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsCustomer;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Account;

public class DynamicsAccount {

    @SerializedName("accountid")
    public String accountId;
    @SerializedName("name")
    public String name;
    @SerializedName("fax")
    public String fax;
    @SerializedName("websiteurl")
    public String website;
    @SerializedName("_primarycontactid_value")
    public String primaryContactId;
    @SerializedName("_parentaccountid_value")
    public String parentCustomerId;
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
    @SerializedName("telephone1")
    public String phone;

    @JsonIgnore
    @JsonProperty("@odata.etag")
    private String etag;

    public static DynamicsAccount from(MsDynamicsCustomer dynamicsCustomer) {
        if (dynamicsCustomer == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        DynamicsAccount account = new DynamicsAccount();
        account.accountId = dynamicsCustomer.customerId;
        account.name = dynamicsCustomer.customerName;
        account.fax = dynamicsCustomer.fax;
        account.website = dynamicsCustomer.website;
        account.primaryContactId = dynamicsCustomer.primaryContactId;
        account.parentCustomerId = dynamicsCustomer.parentCustomerId;
        account.street1 = dynamicsCustomer.street1;
        account.street2 = dynamicsCustomer.street2;
        account.street3 = dynamicsCustomer.street3;
        account.city = dynamicsCustomer.city;
        account.state = dynamicsCustomer.state;
        account.postalCode = dynamicsCustomer.postalCode;
        account.country = dynamicsCustomer.country;
        account.phone = dynamicsCustomer.phone;

        return account;
    }

    public static DynamicsAccount from(Account dynAccount) {
        if (dynAccount == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        DynamicsAccount account = new DynamicsAccount();
        account.accountId = dynAccount.getId();
        account.name = dynAccount.getName();
        account.fax = dynAccount.getFax();
        account.website = dynAccount.getWebsite();
        account.primaryContactId = dynAccount.getPrimaryContactId();
        account.parentCustomerId = dynAccount.getParentCustomerId();
        account.street1 = dynAccount.getStreet1();
        account.street2 = dynAccount.getStreet2();
        account.street3 = dynAccount.getStreet3();
        account.city = dynAccount.getCity();
        account.state = dynAccount.getState();
        account.postalCode = dynAccount.getPostalCode();
        account.country = dynAccount.getCountry();
        account.phone = dynAccount.getPhone();

        return account;
    }
}
