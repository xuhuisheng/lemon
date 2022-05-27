package com.mossle.api.database.dbcp;

import javax.sql.DataSource;

import com.mossle.api.database.Database;

public abstract class AbstractDatabase implements Database {
    @Override
    public DataSource getDataSource() {
        return getDataSource(NAME_DEFAULT);
    }
}
