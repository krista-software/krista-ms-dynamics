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

import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;

@Domain(id = "catEntryDomain_a66f83b1-fa58-427f-b8b9-0c83a401bf53",
        name = "Customer Relations",
        ecosystemId = "catEntryEcosystem_8a0e8ce7-6289-492f-9202-52284024280c",
        ecosystemName = "CRM",
        ecosystemVersion = "272afd56-d4e0-45ee-ab68-13df5340dfa5")
@Entity(name = "Incidents", id = "localDomainEntity_cd86115c-d63e-4a93-a1a4-2d2aeb0ea81d", primaryKey = "Incident ID", options = {}, supportStore = false)
public class Incidents {

    @Field.Text(name = "Incident ID", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String incidentID;

    @Field.Text(name = "Create Date", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String createDate;

    @Field.Text(name = "Case Title", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String caseTitle;

    @Field.Text(name = "Description", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String description;

    @Field.Text(name = "Primary Incident", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String primaryIncident;

    @Field.Text(name = "Equipment System Description", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String equipmentSystemDescription;

    @Field.Text(name = "Service Plan", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String servicePlan;

    @Field.Text(name = "Timeline Details", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String timelineDetails;

    @Field.Text(name = "Actual Problem", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String actualProblem;

    @Field.Text(name = "Resolution", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String resolution;

}