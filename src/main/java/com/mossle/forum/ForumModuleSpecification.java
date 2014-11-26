package com.mossle.forum;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class ForumModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_FORUM";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".forum";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_FORUM";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_forum";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${forum.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${forum.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
