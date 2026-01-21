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
        ecosystemVersion = "272afd56-d4e0-45ee-ab68-13df5340dfa5")
@Entity(name = "Work Orders", id = "localDomainEntity_1c0e5b9c-7de1-403c-81cc-3b7474c6baf6", primaryKey = "Work Order ID", options = {}, supportStore = false)
public class WorkOrders {

    @Field.Text(name = "Create Date", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String createDate;

    @Field.Text(name = "System Status", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String systemStatus;

    @Searchable
    @ToString
    @Field.Text(name = "Sub Status", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String subStatus;

    @Searchable
    @ToString
    @Field.Text(name = "Trip Charge Waived", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String tripChargeWaived;

    @Searchable
    @ToString
    @Field.Text(name = "WO Summary", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String wOSummary;

    @Searchable
    @ToString
    @Field.Text(name = "Primary Incident Type", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String primaryIncidentType;

    @Searchable
    @ToString
    @Field.Text(name = "Primary Incident Customer Asset Description", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String primaryIncidentCustomerAssetDescription;

    @Searchable
    @ToString
    @Field.Text(name = "Service Plan", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String servicePlan;

    @Searchable
    @ToString
    @Field.Text(name = "Timeline Details", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String timelineDetails;

    @Searchable
    @ToString
    @Field.Text(name = "Actual Problem", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String actualProblem;

    @Searchable
    @ToString
    @Field.Text(name = "Actual Resolution", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String actualResolution;

    @Searchable
    @ToString
    @Field.Text(name = "Promise From", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String promiseFrom;

    @Searchable
    @ToString
    @Field.Text(name = "Promise To", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String promiseTo;

    @Searchable
    @ToString
    @Field.Text(name = "Associated Case", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String associatedCase;

    @Field.Text(name = "Work Order ID", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String workOrderID;

}
