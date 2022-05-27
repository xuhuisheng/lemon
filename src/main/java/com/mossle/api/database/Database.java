package com.mossle.api.database;

import java.io.Closeable;

import javax.sql.DataSource;

public interface Database extends Closeable {
    public static final String NAME_DEFAULT = "default";

    DataSource getDataSource();

    DataSource getDataSource(String name);
}
