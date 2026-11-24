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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.stores;

import app.krista.extension.executor.QueryClause;
import app.krista.extension.executor.SearchCondition;
import app.krista.extension.executor.SearchQuery;
import app.krista.extension.util.EntityStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.CatalogTypes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsCustomer;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.AccountDaoImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.AccountImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsAccount;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Account;
import org.codehaus.jettison.json.JSONException;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants.*;
import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.*;


@Service
public final class MsDynamicsCustomerStore implements EntityStore<MsDynamicsCustomer> {

    public static final String QUERY_FIELDS = "name, accountid, telephone1, fax, websiteurl,_primarycontactid_value, " +
            "_parentaccountid_value, address1_line1, address1_line2, address1_line3, address1_city," +
            " address1_stateorprovince, address1_country, address1_postalcode";
    private final AccountDaoImpl accountDao;

    @Inject
    private MsDynamicsCustomerStore(AccountDaoImpl accountDao) {
        this.accountDao = accountDao;
    }

    public static boolean isStringAvailable(String str) {
        return str != null && !str.isBlank();
    }


    /**
     * @param entityDefinition input map with {@link MsDynamicsCustomer} entity fields.
     * @return {@link MsDynamicsCustomer} for the given input
     */
    @Override
    public MsDynamicsCustomer create(Map<String, Object> entityDefinition) {
        if (entityDefinition == null || entityDefinition.isEmpty()) {
            throw new IllegalArgumentException(Constants.FAILED_TO_CREATE + Constants.EMPTY_ENTITY);
        }
        try {
            Account customerAccount = new AccountImpl(
                    Constants.GSON.fromJson(accountDao.createAccount(entityDefinition), DynamicsAccount.class)
            );
            return CatalogTypes.fromDynamicsToKristaEntity(customerAccount);
        } catch (JSONException | ExecutionException | IOException cause) {
            throw new RuntimeException(cause.getMessage());
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(cause.getMessage());
        }
    }

    /**
     * @param primaryKey Id of the {@link MsDynamicsCustomer}
     * @return {@link MsDynamicsCustomer} for the given Id.
     */
    @Override
    public MsDynamicsCustomer get(String primaryKey) throws InterruptedIOException {

        try {
            return CatalogTypes.fromDynamicsToKristaEntity(accountDao.getAccountById(primaryKey));
        } catch (IOException | ExecutionException cause) {
            throw new RuntimeException(cause.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException(Constants.FAILED_TO_GET_CUSTOMER + illegalArgumentException.getMessage());
        } catch (InterruptedException | JSONException interruptedException) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException(interruptedException.getMessage());
        }
    }

    /**
     * @param customer {@link MsDynamicsCustomer} object is given as input to update.
     * @return returns the updated entity {@link MsDynamicsCustomer}
     * @throws InterruptedIOException if there is interruption during execution of salesforce API
     */
    @Override
    public MsDynamicsCustomer update(MsDynamicsCustomer customer) throws InterruptedIOException {
        if (customer == null) {
            throw new IllegalArgumentException(Constants.FAILED_TO_UPDATE + "Customer entity is required. Please provide a valid customer object.");
        }
        try {
            return CatalogTypes.fromDynamicsToKristaEntity(
                    accountDao.updateAccount(
                            new AccountImpl(DynamicsAccount.from(customer))
                    )
            );
        } catch (JSONException | IOException | ExecutionException | IllegalAccessException cause) {
            throw new RuntimeException(cause.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException(Constants.FAILED_TO_GET_CUSTOMER + illegalArgumentException.getMessage());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException(interruptedException.getMessage());
        }
    }

    @Override
    public void delete(String primaryKey) {
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalArgumentException(Constants.FAILED_TO_DELETE + Constants.PRIMARY_KEY_IS_EMPTY);
        }
        try {
            accountDao.deleteAccountById(primaryKey);
        } catch (JSONException | ExecutionException | IOException cause) {
            throw new RuntimeException(cause.getMessage());
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(cause.getMessage());
        }
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<MsDynamicsCustomer> search(List<SearchCondition> conditions, long pageIndex, int pageSize) {
        return Collections.emptyList();
    }

    /**
     * @param searchQuery list of search conditions and query clauses given as input
     * @param pageIndex   page number of the query results
     * @param pageSize    number of results per page
     * @return returns {@link List<MsDynamicsCustomer>} as per the given query
     */
    @Override
    public List<MsDynamicsCustomer> search(SearchQuery searchQuery, long pageIndex, int pageSize) {

        final List<SearchCondition> conditions = getSearchConditions(searchQuery);
        final List<QueryClause> queryClauses = getQueryClauses(searchQuery);
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Failed to get Customers. " + Constants.CONDITIONS_NOT_FOUND);
        }
        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put(SELECT, QUERY_FIELDS);
            queryParams.put(ORDER_BY, getOrderByQueryParams(queryClauses));

            if (conditions.size() != 1) {
                String filterQuery = getFilterByQueryParams(conditions);
                if (isStringAvailable(filterQuery)) {
                    queryParams.put(FILTER, filterQuery);
                }
            }
            return getFilteredCustomers(pageIndex, pageSize, queryParams);
        } catch (IOException cause) {
            throw new RuntimeException(cause.getMessage(), cause.getCause());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalArgumentException(Constants.FAILED_TO_SEARCH + illegalArgumentException.getMessage());
        }
    }

    /**
     * Retrieves filtered customers from Dynamics 365 and applies pagination.
     *
     * @param pageIndex Page number (1-based) for pagination
     * @param pageSize Number of results per page
     * @param queryParams OData query parameters ($select, $filter, $orderby)
     * @return List of MsDynamicsCustomer entities for the requested page
     * @throws MalformedURLException if API endpoint URL is invalid
     */
    private List<MsDynamicsCustomer> getFilteredCustomers(long pageIndex, int pageSize, Map<String, Object> queryParams) throws MalformedURLException {
        List<Account> allAccounts = accountDao.getAccountByQuery(queryParams);
        return allAccounts.stream().skip(Math.max((pageIndex - 1) * pageSize, 0)).limit(pageSize)
                .map(CatalogTypes::fromDynamicsToKristaEntity).collect(Collectors.toList());
    }

    /**
     * Builds OData $filter query parameter from search conditions.
     * Supports filtering by customer ID and customer name.
     *
     * @param conditions List of SearchCondition objects
     * @return OData filter string (e.g., "contains(name, 'Acme') and accountid eq guid")
     */
    private String getFilterByQueryParams(List<SearchCondition> conditions) {
        String customerId = getSearchConditionValue(conditions, Constants.CUSTOMER_ID);
        String customerName = getSearchConditionValue(conditions, CUSTOMER_NAME);
        String customerNameOperator = getSearchConditionStringOperator(conditions, CUSTOMER_NAME);
        String customerFilter;
        if (!customerName.isBlank()) {
            customerFilter = customerNameOperator + "(name, '" + customerName + "')";
        } else if (!customerId.isEmpty()) {
            customerFilter = "accountid eq " + customerId;
        } else {
            customerFilter = "";
        }

        if (!customerName.isEmpty() && !customerId.isEmpty()) {
            customerFilter = customerNameOperator + "(name, '" + customerName + "') " +
                    "and accountid eq " + customerId;
        }
        return customerFilter;
    }

    /**
     * Builds OData $orderby query parameter from query clauses.
     * Defaults to "name DESC" if no clauses provided.
     *
     * @param queryClauses List of QueryClause objects specifying sort order
     * @return OData orderby string (e.g., "name desc")
     */
    private String getOrderByQueryParams(List<QueryClause> queryClauses) {
        String orderByField = queryClauses.isEmpty() ? CUSTOMER_NAME : queryClauses.get(0).getFieldName();
        String orderByOperand = queryClauses.isEmpty() ? "DESC" : queryClauses.get(0).getOperand().toString();
        return getSupportedParameterName(orderByField) + " " + orderByOperand.toLowerCase();
    }

    /**
     * Maps catalog field names to Dynamics 365 API field names for ordering.
     *
     * @param orderByField Catalog field name (e.g., "customerName", "customerId")
     * @return Dynamics 365 API field name (e.g., "name", "accountid")
     */
    private String getSupportedParameterName(String orderByField) {
        if (orderByField.equalsIgnoreCase(CUSTOMER_NAME)) {
            return "name";
        } else if (orderByField.equalsIgnoreCase("customerId")) {
            return "accountid";
        }
        return "_ownerid_value";
    }

    @Override
    public long count(List<SearchCondition> list) {
        return 0;
    }

    @Override
    public List<String> lookup(Map<String, Object> map, List<String> list) {
        return Collections.emptyList();
    }
}
