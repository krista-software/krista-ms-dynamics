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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service;

public interface Contact {

    String getId();

    String getFirstName();

    String getLastName();

    String getAccountId();

    String getMobilePhone();

    String getFax();

    String getStreet1();

    String getStreet2();

    String getStreet3();

    String getCity();

    String getState();

    String getPostalCode();

    String getCountry();

    String getEmail();

    String getContactMethodCode();

}
