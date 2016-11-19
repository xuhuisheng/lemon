package com.mossle.inventory;

import com.mossle.core.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class InventoryModuleSpecification implements ModuleSpecification {
    private static final String MODULE_NAME = "inventory";
    private static final String MODULE_NAME_UPPER = MODULE_NAME.toUpperCase();
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_" + MODULE_NAME_UPPER;
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + "." + MODULE_NAME;
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_" + MODULE_NAME_UPPER;
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_" + MODULE_NAME;
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${" + MODULE_NAME + ".dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${" + MODULE_NAME + ".dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
