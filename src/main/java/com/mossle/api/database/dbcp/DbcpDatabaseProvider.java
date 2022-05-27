package com.mossle.api.database.dbcp;

import com.mossle.api.database.DatabaseBuilder;
import com.mossle.api.database.DatabaseProvider;

public class DbcpDatabaseProvider extends DatabaseProvider {
    @Override
    public DatabaseBuilder newBuilder() {
        return new DbcpDatabaseBuilder();
    }
}
