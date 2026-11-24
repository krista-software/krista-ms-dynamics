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
        ecosystemVersion = "8acd9337-0857-44fa-826f-1111f5c8948e")
@Entity(name = "MsDynamics Case", id = "localDomainEntity_e119978f-7c66-4238-9765-451397461cf2", primaryKey = "Case Id", supportStore = false)
@Searchable
public class MsDynamicsCase {

    @Searchable
    @ToString
    @Field.Text(name = "Case Title")
    public String caseTitle;

    @Searchable
    @ToString
    @Field.Text(name = "Case Number", required = false)
    public String caseNumber;

    @Searchable
    @Field.Text(name = "Case Id", required = false)
    public String caseId;

    @Searchable
    @Field.Text(name = "Contact Id")
    public String contactId;

    @Field.Text(name = "Subject Id", required = false)
    public String subjectId;

    @Field.PickOne(name = "Priority", values = {"High", "Normal", "Low"}, required = false)
    public String priority;

    @Field.PickOne(name = "Case Status", values = {"In Progress", "On Hold", "Waiting for Details", "Researching",
            "Problem Solved", "Cancelled", "Information Provided", "Merged"}, required = false)
    public String caseStatus;


    @Field(name = "Description", type = "RichText", required = false)
    public String description;

}
