package com.mossle.auth;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class AuthModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_AUTH";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".auth";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_AUTH";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_auth";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${auth.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${auth.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
