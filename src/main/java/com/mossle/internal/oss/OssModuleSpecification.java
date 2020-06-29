package com.mossle.internal.oss;

import com.mossle.core.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class OssModuleSpecification implements ModuleSpecification {
    private static final String MODULE_NAME = "oss";
    private static final String MODULE_NAME_UPPER = MODULE_NAME.toUpperCase();
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return PREFIX + MODULE_NAME_UPPER;
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + "." + MODULE_NAME;
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return PREFIX + "DATA_" + MODULE_NAME_UPPER;
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_" + MODULE_NAME;
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${" + MODULE_NAME + ".dbmigrate.enabled:false}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${" + MODULE_NAME + ".dbmigrate.initData:false}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
