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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl;


import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsAccount;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Account;

public class AccountImpl implements Account {

    private final DynamicsAccount dynamicsAccount;

    public AccountImpl(DynamicsAccount dynamicsAccount) {
        this.dynamicsAccount = dynamicsAccount;
    }

    @Override
    public String getId() {
        return dynamicsAccount.accountId;
    }

    @Override
    public String getName() {
        return dynamicsAccount.name;
    }

    @Override
    public String getCity() {
        return dynamicsAccount.city;
    }

    @Override
    public String getFax() {
        return dynamicsAccount.fax;
    }

    @Override
    public String getPrimaryContactId() {
        return dynamicsAccount.primaryContactId;
    }

    @Override
    public String getParentCustomerId() {
        return dynamicsAccount.parentCustomerId;
    }

    @Override
    public String getStreet1() {
        return dynamicsAccount.street1;
    }

    @Override
    public String getStreet2() {
        return dynamicsAccount.street2;
    }

    @Override
    public String getStreet3() {
        return dynamicsAccount.street3;
    }

    @Override
    public String getState() {
        return dynamicsAccount.state;
    }

    @Override
    public String getPostalCode() {
        return dynamicsAccount.postalCode;
    }

    @Override
    public String getCountry() {
        return dynamicsAccount.country;
    }

    @Override
    public String getWebsite() {
        return dynamicsAccount.website;
    }

    @Override
    public String getPhone() {
        return dynamicsAccount.phone;
    }

}