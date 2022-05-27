package com.mossle.api.database;

public abstract class DatabaseProvider {
    public static DatabaseProvider provider() {
        DatabaseProvider databaseProvider = DatabaseProviderRegistry
                .getDefaultRegistry().provider();

        if (databaseProvider == null) {
            throw new RuntimeException("cannot find database provider");
        }

        return databaseProvider;
    }

    public int getPriority() {
        return 5;
    }

    public abstract DatabaseBuilder newBuilder();
}
