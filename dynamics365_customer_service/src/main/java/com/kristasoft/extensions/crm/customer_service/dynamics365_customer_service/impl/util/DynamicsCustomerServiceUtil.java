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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util;

import app.krista.extension.executor.QueryClause;
import app.krista.extension.executor.SearchCondition;
import app.krista.extension.executor.SearchQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicsCustomerServiceUtil {

    private static final Map<String, String> priorityKeyValues;
    private static final Map<String, String> caseStatus;
    private static final Map<String, String> contactMethod;

    static {
        priorityKeyValues = new HashMap<>();
        priorityKeyValues.put("High", "1");
        priorityKeyValues.put("Normal", "2");
        priorityKeyValues.put("Low", "3");
    }

    static {
        caseStatus = new HashMap<>();
        caseStatus.put("In Progress", "1");
        caseStatus.put("On Hold", "2");
        caseStatus.put("Waiting for Details", "3");
        caseStatus.put("Researching", "4");
        caseStatus.put("Problem Solved", "5");
        caseStatus.put("Canceled", "6");
        caseStatus.put("Information Provided", "1000");
        caseStatus.put("Merged", "2000");
    }

    static {
        contactMethod = new HashMap<>();
        contactMethod.put("Any", "1");
        contactMethod.put("Email", "2");
        contactMethod.put("Phone", "3");
        contactMethod.put("Fax", "4");
        contactMethod.put("Mail", "5");
    }


    private DynamicsCustomerServiceUtil() {
    }

    public static List<QueryClause> getQueryClauses(SearchQuery searchQuery) {
        return searchQuery.getQueryClauses();
    }

    public static List<SearchCondition> getSearchConditions(SearchQuery searchQuery) {
        return searchQuery.getSearchConditions();
    }

    public static String getSearchConditionValue(List<SearchCondition> conditions, String fieldName) {
        for (SearchCondition condition : conditions) {
            if (fieldName.equalsIgnoreCase(condition.getFieldName())) {
                return condition.getOperand() != null ?
                        String.valueOf(condition.getOperand()).trim() :
                        Constants.EMPTY_STRING;
            }
        }
        return Constants.EMPTY_STRING;
    }

    public static String getSearchConditionStringOperator(List<SearchCondition> conditions, String fieldName) {
        for (SearchCondition condition : conditions) {
            if (fieldName.equalsIgnoreCase(condition.getFieldName())) {
                return condition.getOperator() != null ?
                        String.valueOf(condition.getOperator()).trim() :
                        Constants.CONTAINS;
            }
        }
        return Constants.CONTAINS;
    }

    public static String getKeyFromValue(String typeOfKey, String value) {
        Map<String, String> map = new HashMap<>();
        if (typeOfKey.equalsIgnoreCase("Priority")) {
            map = priorityKeyValues;
        } else if (typeOfKey.equalsIgnoreCase("Case Status")) {
            map = caseStatus;
        } else if (typeOfKey.equalsIgnoreCase("Contact Method")) {
            map = contactMethod;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getPriorityFromKey(String key) {
        return priorityKeyValues.get(key);
    }

    public static String getCaseStatusFromKey(String key) {
        return caseStatus.get(key);
    }

    public static String getContactMethodFromKey(String key) {
        return contactMethod.get(key);
    }
}
