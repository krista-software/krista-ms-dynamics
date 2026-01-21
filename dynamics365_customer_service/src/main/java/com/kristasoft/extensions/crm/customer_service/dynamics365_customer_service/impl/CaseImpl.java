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

import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsCase;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Case;

public class CaseImpl implements Case {

    private final DynamicsCase dynamicsCase;

    public CaseImpl(DynamicsCase dynamicsCase) {
        this.dynamicsCase = dynamicsCase;
    }

    @Override
    public String getCaseTitle() {
        return dynamicsCase.caseTitle;
    }

    @Override
    public String getCaseNumber() {
        return dynamicsCase.caseNumber;
    }

    @Override
    public String getCaseId() {
        return dynamicsCase.caseId;
    }

    @Override
    public String getContactId() {
        return dynamicsCase.contactId;
    }

    @Override
    public String getSubjectId() {
        return dynamicsCase.subjectId;
    }

    @Override
    public String getDescription() {
        return dynamicsCase.description;
    }

    @Override
    public String getPriorityCode() {
        return dynamicsCase.priorityCode;
    }

    @Override
    public String getCaseStatusCode() {
        return dynamicsCase.caseStatusCode;
    }
}
