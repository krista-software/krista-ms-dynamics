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

import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Contact;

public class ContactImpl implements Contact {

    private final DynamicsContact dynamicsContact;

    public ContactImpl(DynamicsContact dynamicsContact) {
        this.dynamicsContact = dynamicsContact;
    }

    @Override
    public String getId() {
        return dynamicsContact.id;
    }

    @Override
    public String getFirstName() {
        return dynamicsContact.firstName;
    }

    @Override
    public String getLastName() {
        return dynamicsContact.lastName;
    }

    @Override
    public String getAccountId() {
        return dynamicsContact.accountId;
    }

    @Override
    public String getMobilePhone() {
        return dynamicsContact.mobilePhone;
    }

    @Override
    public String getFax() {
        return dynamicsContact.fax;
    }

    @Override
    public String getStreet1() {
        return dynamicsContact.street1;
    }

    @Override
    public String getStreet2() {
        return dynamicsContact.street2;
    }

    @Override
    public String getStreet3() {
        return dynamicsContact.street3;
    }

    @Override
    public String getCity() {
        return dynamicsContact.city;
    }

    @Override
    public String getState() {
        return dynamicsContact.state;
    }

    @Override
    public String getPostalCode() {
        return dynamicsContact.postalCode;
    }

    @Override
    public String getCountry() {
        return dynamicsContact.country;
    }

    @Override
    public String getEmail() {
        return dynamicsContact.email;
    }

    @Override
    public String getContactMethodCode() {
        return dynamicsContact.contactMethodCode;
    }
}
