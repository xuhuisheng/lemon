package com.mossle.user;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class UserModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_USER";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".user";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_USER";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_user";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${user.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${user.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
