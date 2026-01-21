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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities;


import app.krista.extension.impl.anno.*;

@Domain(id = "catEntryDomain_a66f83b1-fa58-427f-b8b9-0c83a401bf53",
        name = "Customer Relations",
        ecosystemId = "catEntryEcosystem_8a0e8ce7-6289-492f-9202-52284024280c",
        ecosystemName = "CRM",
        ecosystemVersion = "474d34d5-451b-4068-9754-c749ca7205b8")
@Entity(name = "MsDynamics Customer", id = "localDomainEntity_82652c91-eb45-4c9b-815c-dde3aa035985", primaryKey = "Customer Id", supportStore = false)
@Searchable
public class MsDynamicsCustomer {

    @Searchable
    @ToString
    @Field(name = "Customer Id", type = "Text", required = false)
    public String customerId;

    @Searchable
    @ToString
    @Field(name = "Customer Name", type = "Text")
    public String customerName;

    @Field(name = "Phone", type = "Text", required = false)
    public String phone;

    @Field(name = "Fax", type = "Text", required = false)
    public String fax;

    @Field(name = "Website", type = "Text", required = false)
    public String website;

    @Field(name = "Primary Contact Id", type = "Text", required = false)
    public String primaryContactId;

    @Field(name = "Parent Customer Id", type = "Text", required = false)
    public String parentCustomerId;

    @Field(name = "Street1", type = "Street", required = false)
    public String street1;

    @Field(name = "Street2", type = "Street2", required = false)
    public String street2;

    @Field(name = "Street3", type = "Street2", required = false)
    public String street3;

    @Field(name = "City", type = "City", required = false)
    public String city;

    @Field(name = "State", type = "State", required = false)
    public String state;

    @Field(name = "Postal Code", type = "PostalCode", required = false)
    public String postalCode;

    @Field(name = "Country", type = "Country", required = false)
    public String country;

}
