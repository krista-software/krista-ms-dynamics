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
        ecosystemVersion = "ea7e5630-fc2e-4f8e-a19a-cd44225f9ca5")
@Entity(name = "MsDynamics Contact", id = "localDomainEntity_a390faef-b7e5-4ba8-9d5f-e30b0d66f6e9", primaryKey = "Id", supportStore = false)
@Searchable
public class MsDynamicsContact {

    @ToString
    @Field.Text(name = "First Name", required = false)
    public String firstName;

    @Searchable
    @ToString
    @Field.Text(name = "Last Name")
    public String lastName;

    @Searchable
    @ToString
    @Field.Text(name = "Id", required = false)
    public String id;

    @Searchable
    @Field.Text(name = "Account Id", required = false)
    public String accountId;

    @Field.Text(name = "Mobile Phone", required = false)
    public String mobilePhone;

    @Field.Text(name = "Fax", required = false)
    public String fax;

    @Field.PickOne(name = "Contact Method", values = {"Any", "Email", "Phone", "Fax", "Mail"}, required = false)
    public String contactMethod;

    @Field(name = "Street 1", type = "Street", required = false)
    public String street1;

    @Field(name = "Street 2", type = "Street", required = false)
    public String street2;

    @Field(name = "Street 3", type = "Street", required = false)
    public String street3;

    @Field(name = "City", type = "City", required = false)
    public String city;

    @Field(name = "State", type = "State", required = false)
    public String state;

    @Field(name = "Postal Code", type = "PostalCode", required = false)
    public String postalCode;

    @Field(name = "Country", type = "Country", required = false)
    public String country;

    @Field(name = "Email", type = "Email", required = false)
    public String email;

    public MsDynamicsContact() {
    }

    public MsDynamicsContact(
            String id,
            String firstName,
            String lastName,
            String accountId,
            String mobilePhone,
            String fax,
            String email,
            String street1,
            String street2,
            String street3,
            String city,
            String state,
            String postalCode,
            String country,
            String contactMethod) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountId = accountId;
        this.mobilePhone = mobilePhone;
        this.fax = fax;
        this.email = email;
        this.street1 = street1;
        this.street2 = street2;
        this.street3 = street3;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.contactMethod = contactMethod;
    }


}
