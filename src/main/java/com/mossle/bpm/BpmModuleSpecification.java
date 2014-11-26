package com.mossle.bpm;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class BpmModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_BPM";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".bpm";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_BPM";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_bpm";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${bpm.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${bpm.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
