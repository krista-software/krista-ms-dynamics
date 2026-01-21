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
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.stores.MsDynamicsContactStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.AccountDaoImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.CaseDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@Domain(id = "catEntryDomain_a66f83b1-fa58-427f-b8b9-0c83a401bf53",
        name = "Customer Relations",
        ecosystemId = "catEntryEcosystem_8a0e8ce7-6289-492f-9202-52284024280c",
        ecosystemName = "CRM",
        ecosystemVersion = "9477f82d-4332-43f6-a8bb-1eb177bc57c1")
public class CaseManagementArea {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseManagementArea.class);
    private final CaseDaoImpl caseDao;
    private final AccountDaoImpl accountDao;
    private final MsDynamicsContactStore msDynamicsContactStore;

    @Inject
    public CaseManagementArea(CaseDaoImpl caseDao, AccountDaoImpl accountDao, MsDynamicsContactStore msDynamicsContactStore) {
        this.caseDao = caseDao;
        this.accountDao = accountDao;
        this.msDynamicsContactStore = msDynamicsContactStore;
    }

    @CatalogRequest(
            id = "localDomainRequest_6a4bc822-d991-4e0f-951a-e8e2c339c01f",
            name = "Get Subjects for Case",
            description = "This request will fetch the list of available subjects with their Identifier values.",
            area = "Case management",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Desc(name = "Subjects", type = "[ { Subject Title: Text, Subject Id: Text } ]", required = false)
    public List<Map<String, Object>> getSubjectsForCase() throws MalformedURLException {
        return caseDao.getSubjects();
    }

    @CatalogRequest(
            id = "localDomainRequest_29ab312a-4699-4434-90dd-c9a817cdc0a4",
            name = "Pick account using Name",
            description = "This request retrieves a list of accounts based on the account name",
            area = "Case management",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Desc(name = "List of Account", type = "[ { Account Name: Text, Address: Text, Account Number: Text } ]", required = false)
    public List<Map<String, String>> pickAccountUsingName(
            @Field.Text(name = "Account Name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Name of the account to search for in Dynamics 365. Supports partial matching to find accounts containing this text.'")}, options = {}) String accountName) {
        try {
            return caseDao.getAccountByName(accountName);
        } catch (Exception cause) {
            LOGGER.error("Error occurred while picking account by name ", cause);
            throw new RuntimeException("Error occurred while picking account by name ", cause);
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_cd3d20da-cc43-420a-8827-c1c95c4fdf49",
            name = "Get Contacts By Company Id",
            description = "This request retrieves contacts associated with a specific company ID.",
            area = "Case management",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Desc(name = "List of Contact", type = "[ Entity(Contact) ]", required = false)
    public List<MsDynamicsContact> getContactsByCompanyId(
            @Field.Text(name = "Company Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'The unique identifier of the company/account in Dynamics 365 to retrieve associated contacts.'")}, options = {}) String companyId) {
        try {
            return msDynamicsContactStore.getContactsByCompanyId(companyId);
        } catch (Exception cause) {
            throw new RuntimeException("Error occurred while getting contacts by company id ", cause);
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_57017142-2ed9-4ac2-8c7c-c43ea3e580ad",
            name = "Get Lookup Account by Account Id",
            description = "This request retrieves account information based on the account ID.",
            area = "Case management",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Desc(name = "Account", type = "Entity(Account)", required = false)
    public Account getLookupAccountByAccountId(
            @Field.Text(name = "AccountId", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'The unique identifier of the account in Dynamics 365 to retrieve account details.'")}, options = {}) String accountId) {
        try {
            return accountDao.getLookupAccountByAccountId(accountId);
        } catch (Exception cause) {
            LOGGER.error("Error occurred while getting accountId from customerAsset ", cause);
            throw new RuntimeException("Error occurred while getting accountId from customerAsset", cause);
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_14bbb875-4cb3-4817-9dfa-5b65fd00ca5e",
            name = "Get WorkOrder By AccountId",
            description = "Retrieves a collection of Work Orders from Microsoft Dynamics 365\nField Service based on Account ID and configurable date range. This\nrequest enables comprehensive work order analysis, tracking, and\nreporting for field service operations within specified time periods",
            area = "Case management",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Desc(name = "Work Orders", type = "[ Entity(Work Orders) ]", required = false)
    @Field(name = "Total Count", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Total number of work orders found'")}, options = {})
    @Field.Desc(name = "Date Range Applied", type = "{ Days Covered: Text, Start Date: Text, End Date: Text }", required = false)
    public Map<String, Object> getWorkOrderByAccountId(
            @Field.Text(name = "AccountId", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'The unique identifier of the account in Dynamics 365 to filter work orders.'")}, options = {}) String accountId,
            @Field.Date(name = "Start Date", required = false, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Start date for work order retrieval. Defaults to 90 days ago if not provided'")}, options = {}) Long startDate,
            @Field.Date(name = "End Date", required = false, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'End date for work order retrieval. Defaults to current date if not provided. Maximum range is 6 months from start date'")}, options = {}) Long endDate) {
        try {
            return caseDao.getWorkOrderByAccountId(accountId, startDate, endDate);
        } catch (Exception cause) {
            throw new RuntimeException(cause.getMessage());
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_f2c556a9-d838-475e-8d05-89d613da296b",
            name = "Get Incidents by Account ID",
            description = "Retrieves a collection of Incidents (Cases) from Microsoft Dynamics\n365 Customer Service based on Account ID and configurable date\nrange. This request enables comprehensive case analysis, tracking,\nand reporting for customer service operations within specified time\nperiods.",
            area = "Case management",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field.Desc(name = "Incidents", type = "[ Entity(Incidents) ]", required = false)
    @Field(name = "Total Count", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    @Field.Desc(name = "Date Range Applied", type = "{ Start Date: Text, End Date: Text, Days Covered: Text }", required = false)
    public Map<String, Object> getIncidentsByAccountID(
            @Field.Text(name = "Account ID", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'The unique identifier of the account in Dynamics 365 to filter incidents/cases'")}, options = {}) String accountID,
            @Field.Date(name = "Start Date", required = false, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Start date for incident retrieval. Defaults to 90 days ago if not provided'")}, options = {}) Long startDate,
            @Field.Date(name = "End Date", required = false, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'End date for incident retrieval. Defaults to current date if not provided. Maximum range is 6 months from start date'")}, options = {}) Long endDate,
            @Field.Text(name = "Include Timeline Details", required = true, attributes = {@Attribute(name = "visualWidth", value = "S"), @Attribute(name = "toolTip", value = "'Whether to include associated timeline details and notes. Defaults to true'")}, options = {}) String includeTimelineDetails) {
        try {
            return caseDao.getIncidentsByAccountId(accountID, startDate, endDate, includeTimelineDetails);
        } catch (Exception cause) {
            throw new RuntimeException(cause.getMessage());
        }
    }
}