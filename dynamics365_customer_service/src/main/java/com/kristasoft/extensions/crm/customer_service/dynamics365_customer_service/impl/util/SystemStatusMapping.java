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

import java.util.HashMap;
import java.util.Map;

public class SystemStatusMapping {

    private static final Map<Integer, String> SYSTEM_STATUS_MAP = new HashMap<>();

    static {
        SYSTEM_STATUS_MAP.put(690970000, "Open - Unscheduled");
        SYSTEM_STATUS_MAP.put(690970001, "Open - Scheduled");
        SYSTEM_STATUS_MAP.put(690970002, "Open - In Progress");
        SYSTEM_STATUS_MAP.put(690970003, "Open - Completed");
        SYSTEM_STATUS_MAP.put(690970004, "Closed - Posted");
        SYSTEM_STATUS_MAP.put(690970005, "Closed - Canceled");
    }

    private SystemStatusMapping() {
        // Utility class
    }

    public static String getSystemStatusDescription(int statusCode) {
        return SYSTEM_STATUS_MAP.getOrDefault(statusCode, String.valueOf(statusCode));
    }
}