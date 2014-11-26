package com.mossle.cms;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class CmsModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_CMS";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".cms";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_CMS";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_cms";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${cms.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${cms.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
