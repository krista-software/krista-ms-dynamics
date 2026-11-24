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

import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.CustomerAsset;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.CustomerAssetsDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@Domain(id = "catEntryDomain_a66f83b1-fa58-427f-b8b9-0c83a401bf53",
        name = "Customer Relations",
        ecosystemId = "catEntryEcosystem_8a0e8ce7-6289-492f-9202-52284024280c",
        ecosystemName = "CRM",
        ecosystemVersion = "8468041b-0057-4d8f-91cd-06e28b720465")
public class CustomerManagementArea {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerManagementArea.class);
    private final CustomerAssetsDaoImpl customerAssetsDao;

    @Inject
    public CustomerManagementArea(CustomerAssetsDaoImpl customerAssetsDao) {
        this.customerAssetsDao = customerAssetsDao;
    }

    @CatalogRequest(
            id = "localDomainRequest_66b7c249-e800-4e63-80db-3d4f4926c62b",
            name = "Get Customer Assets by Account Id",
            description = "This request retrieves customer assets associated with a specific account ID.",
            area = "Customer Management",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Desc(name = "Customer Asset", type = "[ Entity ]", required = false)
    public List<CustomerAsset> getCustomerAssetByAccountId(
            @Field.Text(name = "AccountId", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'The unique identifier of the account in Dynamics 365 to retrieve associated customer assets.'")}, options = {}) String accountId) {
        try {
            return customerAssetsDao.getCustomerAssetsByAccountId(accountId);
        } catch (Exception cause) {
            LOGGER.error("Error occurred while getting customer assets by account id ", cause);
            throw new RuntimeException("Error occurred while getting accountId from customerAsset", cause);
        }
    }
}