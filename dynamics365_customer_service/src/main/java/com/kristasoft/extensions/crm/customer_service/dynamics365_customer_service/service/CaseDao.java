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

import org.codehaus.jettison.json.JSONException;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public interface CaseDao {

    Case getCaseById(String primaryKey) throws MalformedURLException;

    List<Case> getCases(long pageSize, long pageIndex, String orderByField, String orderByOperand) throws MalformedURLException;

    List<Case> getCasesByQuery(Map<String, Object> queryParams) throws MalformedURLException;

    String createCase(Map<String, Object> entityDefinition) throws MalformedURLException, JSONException;

    Case updateCase(Case oldCase) throws MalformedURLException, IllegalAccessException;

    void deleteCaseById(String primaryKey) throws MalformedURLException;

    List<Map<String, Object>> getSubjects() throws MalformedURLException;
}
