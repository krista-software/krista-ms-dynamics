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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsCase;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Case;
import net.minidev.json.annotate.JsonIgnore;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.*;

public class DynamicsCase {

    @SerializedName("title")
    public String caseTitle;

    @SerializedName("ticketnumber")
    public String caseNumber;

    @SerializedName("incidentid")
    public String caseId;

    @SerializedName("_customerid_value")
    public String contactId;

    @SerializedName("_subjectid_value")
    public String subjectId;


    @SerializedName("description")
    public String description;

    public String priority;

    public String caseStatus;

    @SerializedName("prioritycode")
    public String priorityCode;

    @SerializedName("statuscode")
    public String caseStatusCode;

    @JsonIgnore
    @JsonProperty("@odata.etag")
    private String etag;

    public static DynamicsCase from(MsDynamicsCase dynamicsCase) {
        if (dynamicsCase == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        DynamicsCase dCase = new DynamicsCase();
        dCase.caseTitle = dynamicsCase.caseTitle;
        dCase.caseNumber = dynamicsCase.caseNumber;
        dCase.caseId = dynamicsCase.caseId;
        dCase.contactId = dynamicsCase.contactId;
        dCase.subjectId = dynamicsCase.subjectId;
        dCase.description = dynamicsCase.description;
        dCase.priority = dynamicsCase.priority;
        dCase.caseStatus = dynamicsCase.caseStatus;
        dCase.priorityCode = getPriorityFromKey(dynamicsCase.priority);
        dCase.caseStatusCode = getCaseStatusFromKey(dynamicsCase.caseStatus);

        return dCase;
    }

    public static DynamicsCase from(Case dynCase) {
        if (dynCase == null) {
            throw new IllegalArgumentException(Constants.EMPTY_ENTITY);
        }
        DynamicsCase dCase = new DynamicsCase();
        dCase.caseTitle = dynCase.getCaseTitle();
        dCase.caseNumber = dynCase.getCaseNumber();
        dCase.caseId = dynCase.getCaseId();
        dCase.contactId = dynCase.getContactId();
        dCase.subjectId = dynCase.getSubjectId();
        dCase.description = dynCase.getDescription();
        dCase.priority = getKeyFromValue("Priority", dynCase.getPriorityCode());
        dCase.caseStatus = getKeyFromValue("Case Status", dynCase.getCaseStatusCode());

        return dCase;
    }
}
