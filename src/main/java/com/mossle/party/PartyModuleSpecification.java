package com.mossle.party;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class PartyModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_PARTY";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".party";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_PARTY";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_party";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${party.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${party.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
