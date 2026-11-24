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

import java.util.List;
import java.util.Map;

@Domain(id = "catEntryDomain_a66f83b1-fa58-427f-b8b9-0c83a401bf53",
        name = "Customer Relations",
        ecosystemId = "catEntryEcosystem_8a0e8ce7-6289-492f-9202-52284024280c",
        ecosystemName = "CRM",
        ecosystemVersion = "cae45aa0-daab-4c66-b40c-b551f962bcfc")
@Entity(name = "Account", id = "localDomainEntity_f1cbc06e-dcdf-4ca5-88f9-cd4e1687bdba", primaryKey = "Account Id", options = {}, supportStore = false)
public class Account {

    @Searchable
    @ToString
    @Field.Text(name = "Address", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String address; //"address1_line1"

    @Searchable
    @ToString
    @Field.Text(name = "Account Name", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String accountName;

    @Searchable
    @ToString
    @Field.Text(name = "Account Id", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String accountId;

    @Searchable
    @ToString
    @Field.Text(name = "City", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String city;

    @ToString
    @Field.Desc(name = "List of Customer Assets", type = "[ { Customer Asset Id: Text, Customer Asset Name: Text, System Type Value: Text, Is Active: Boolean, Gua Name: Text, Display Group: Text } ]", required = false)
    public List<Map<String, Object>> listOfCustomerAssets;

    @Searchable
    @ToString
    @Field.Text(name = "Account Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String accountNumber;

    @Searchable
    @ToString
    @Field.Text(name = "Phone", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String phone;

    @Searchable
    @ToString
    @Field.Text(name = "CS Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String cSNumber;

    public Account(String address, String accountName, String accountId, String city,
                   List<Map<String, Object>> listOfCustomerAssets, String accountNumber, String phone, String cSNumber) {
        this.address = address;
        this.accountName = accountName;
        this.accountId = accountId;
        this.city = city;
        this.listOfCustomerAssets = listOfCustomerAssets;
        this.accountNumber = accountNumber;
        this.phone = phone;
        this.cSNumber = cSNumber;
    }

}

