package com.mossle.internal.template;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class TemplateModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_TEMPLATE";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".template";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_TEMPLATE";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_template";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${template.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${template.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
