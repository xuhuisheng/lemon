package com.mossle.msg;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class MsgModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_MSG";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".msg";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_MSG";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_msg";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${msg.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${msg.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
