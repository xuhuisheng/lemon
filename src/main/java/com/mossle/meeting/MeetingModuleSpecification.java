package com.mossle.meeting;

import com.mossle.ext.dbmigrate.ModuleSpecification;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class MeetingModuleSpecification implements ModuleSpecification {
    private String type;
    private boolean enabled;
    private boolean initData;

    public boolean isEnabled() {
        return enabled;
    }

    public String getSchemaTable() {
        return "SCHEMA_VERSION_MEETING";
    }

    public String getSchemaLocation() {
        return "dbmigrate." + type + ".meeting";
    }

    public boolean isInitData() {
        return initData;
    }

    public String getDataTable() {
        return "SCHEMA_VERSION_DATA_MEETING";
    }

    public String getDataLocation() {
        return "dbmigrate." + type + ".data_meeting";
    }

    @Value("${application.database.type}")
    public void setType(String type) {
        this.type = type;
    }

    @Value("${meeting.dbmigrate.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Value("${meeting.dbmigrate.initData}")
    public void setInitData(boolean initData) {
        this.initData = initData;
    }
}
