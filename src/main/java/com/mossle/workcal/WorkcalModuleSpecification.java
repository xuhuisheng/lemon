package com.mossle.workcal;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class WorkcalModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_WORKCAL";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".workcal";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_WORKCAL";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_workcal";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${workcal.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${workcal.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
