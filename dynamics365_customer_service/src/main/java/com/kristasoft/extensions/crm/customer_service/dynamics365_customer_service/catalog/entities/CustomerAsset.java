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
        ecosystemVersion = "705bb482-45fb-464b-8c6c-a2f9ef13c835")
@Entity(name = "CustomerAsset", id = "localDomainEntity_6b55b097-b3bd-4fa7-9de1-5914f90e1e86", primaryKey = "Account Id", options = {}, supportStore = false)
public class CustomerAsset {

    @Searchable
    @ToString
    @Field.Text(name = "Customer Asset Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String customerAssetId;

    @Searchable
    @ToString
    @Field.Text(name = "Name", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String name;

    @Searchable
    @ToString
    @Field.Text(name = "Status Code", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String statusCode;

    @Searchable
    @ToString
    @Field.Text(name = "State Code", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String stateCode;

    @Searchable
    @ToString
    @Field.Text(name = "Created On", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String createdOn;

    @Searchable
    @ToString
    @Field.Text(name = "Modified On", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String modifiedOn;

    @Searchable
    @ToString
    @Field.Text(name = "Owner Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String ownerId;

    @Searchable
    @ToString
    @Field.Text(name = "Account Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String accountId;

    @Searchable
    @ToString
    @Field.Text(name = "CS Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String cSNumber;

    @Searchable
    @ToString
    @Field.Text(name = "Asset Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String assetNumber;

    @Searchable
    @ToString
    @Field.Boolean(name = "Is Monitored Asset", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public Boolean isMonitoredAsset;

    @Searchable
    @ToString
    @Field.Text(name = "In Service Date", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String inServiceDate;

    @Searchable
    @ToString
    @Field.Text(name = "Out of Service Date", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String outOfServiceDate;

    @Searchable
    @ToString
    @Field.Text(name = "Service Plan", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String servicePlan;

    @Searchable
    @ToString
    @Field.Boolean(name = "Is Active Asset", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public Boolean isActiveAsset;

    @Searchable
    @ToString
    @Field.Text(name = "Upgrade Type", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String upgradeType;

    @Searchable
    @ToString
    @Field.Text(name = "OOS Category", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String oOSCategory;

    @Searchable
    @ToString
    @Field.Text(name = "System Type Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String systemTypeId;

    @Searchable
    @ToString
    @Field.Text(name = "System Type Description", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String systemTypeDescription;

    @Searchable
    @ToString
    @Field.Text(name = "System Type Value", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String systemTypeValue;

    @Searchable
    @ToString
    @Field.Text(name = "Display Group", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String displayGroup;

    public static class Builder {
        private String customerAssetId;
        private String name;
        private String statusCode;
        private String stateCode;
        private String createdOn;
        private String modifiedOn;
        private String ownerId;
        private String accountId;
        private String cSNumber;
        private String assetNumber;
        private Boolean isMonitoredAsset;
        private String inServiceDate;
        private String outOfServiceDate;
        private String servicePlan;
        private Boolean isActiveAsset;
        private String upgradeType;
        private String oOSCategory;
        private String systemTypeId;
        private String systemTypeDescription;
        private String systemTypeValue;
        private String displayGroup;

        public Builder customerAssetId(String customerAssetId) {
            this.customerAssetId = customerAssetId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder statusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder stateCode(String stateCode) {
            this.stateCode = stateCode;
            return this;
        }

        public Builder createdOn(String createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public Builder modifiedOn(String modifiedOn) {
            this.modifiedOn = modifiedOn;
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder cSNumber(String cSNumber) {
            this.cSNumber = cSNumber;
            return this;
        }

        public Builder assetNumber(String assetNumber) {
            this.assetNumber = assetNumber;
            return this;
        }

        public Builder isMonitoredAsset(Boolean isMonitoredAsset) {
            this.isMonitoredAsset = isMonitoredAsset;
            return this;
        }

        public Builder inServiceDate(String inServiceDate) {
            this.inServiceDate = inServiceDate;
            return this;
        }

        public Builder outOfServiceDate(String outOfServiceDate) {
            this.outOfServiceDate = outOfServiceDate;
            return this;
        }

        public Builder servicePlan(String servicePlan) {
            this.servicePlan = servicePlan;
            return this;
        }

        public Builder isActiveAsset(Boolean isActiveAsset) {
            this.isActiveAsset = isActiveAsset;
            return this;
        }

        public Builder upgradeType(String upgradeType) {
            this.upgradeType = upgradeType;
            return this;
        }

        public Builder oOSCategory(String oOSCategory) {
            this.oOSCategory = oOSCategory;
            return this;
        }

        public Builder systemTypeId(String systemTypeId) {
            this.systemTypeId = systemTypeId;
            return this;
        }

        public Builder systemTypeDescription(String systemTypeDescription) {
            this.systemTypeDescription = systemTypeDescription;
            return this;
        }

        public Builder systemTypeValue(String systemTypeValue) {
            this.systemTypeValue = systemTypeValue;
            return this;
        }

        public Builder displayGroup(String displayGroup) {
            this.displayGroup = displayGroup;
            return this;
        }

        public CustomerAsset build() {
            return new CustomerAsset(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private CustomerAsset(Builder builder) {
        this.customerAssetId = builder.customerAssetId;
        this.name = builder.name;
        this.statusCode = builder.statusCode;
        this.stateCode = builder.stateCode;
        this.createdOn = builder.createdOn;
        this.modifiedOn = builder.modifiedOn;
        this.ownerId = builder.ownerId;
        this.accountId = builder.accountId;
        this.cSNumber = builder.cSNumber;
        this.assetNumber = builder.assetNumber;
        this.isMonitoredAsset = builder.isMonitoredAsset;
        this.inServiceDate = builder.inServiceDate;
        this.outOfServiceDate = builder.outOfServiceDate;
        this.servicePlan = builder.servicePlan;
        this.isActiveAsset = builder.isActiveAsset;
        this.upgradeType = builder.upgradeType;
        this.oOSCategory = builder.oOSCategory;
        this.systemTypeId = builder.systemTypeId;
        this.systemTypeDescription = builder.systemTypeDescription;
        this.systemTypeValue = builder.systemTypeValue;
        this.displayGroup = builder.displayGroup;
    }
}