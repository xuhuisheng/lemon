package com.mossle.api.database.support;

public interface DataSourceInfo {
    String getName();

    void setName(String name);

    void validate();
}
