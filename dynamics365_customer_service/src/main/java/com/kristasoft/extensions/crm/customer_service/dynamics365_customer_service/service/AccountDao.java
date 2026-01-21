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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public interface AccountDao {

    /**
     * Returns {@link Account} object of salesforce from the account id provided.
     *
     * @param id id of account to be searched
     * @return {@link Account} object of salesforce
     */
    Account getAccountById(String id) throws IOException, ExecutionException, InterruptedException, JSONException;

    /**
     * Returns {@link List <Account>} list of object of salesforce.
     *
     * @param size           number of accounts to be fetched
     * @param page           page number to be fetched
     * @param orderByField
     * @param orderByOperand
     * @return {@link List<Account>} list of object of salesforce
     */
    List<Account> getAllAccounts(long size, long page, String orderByField, String orderByOperand) throws IOException, ExecutionException, InterruptedException;

    /**
     * Returns {@link List<Account>} list of object of salesforce.
     *
     * @param queryParams query parameters to be searched
     * @return {@link List<Account>} list of object of salesforce
     */
    List<Account> getAccountByQuery(Map<String, Object> queryParams) throws IOException, ExecutionException, InterruptedException;

    /**
     * Add {@link Account} to salesforce from provided input fields
     *
     * @param entityDefinition fields which are added in the {@link Account} object of salesforce to create
     */
    String createAccount(Map<String, Object> entityDefinition) throws IOException, ExecutionException, InterruptedException, JSONException;

    /**
     * Update {@link Account} to salesforce from provided input fields
     *
     * @param account {@link Account} object of salesforce to be updated
     */
    Account updateAccount(Account account) throws IllegalAccessException, IOException, ExecutionException, InterruptedException, JSONException;

    void deleteAccountById(String primaryKey) throws InterruptedException, JSONException, IOException, ExecutionException;

    com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account getLookupAccountByAccountId(String accountId) throws MalformedURLException;
}