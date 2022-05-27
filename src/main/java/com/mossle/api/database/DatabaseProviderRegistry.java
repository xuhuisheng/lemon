package com.mossle.api.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class DatabaseProviderRegistry {
    private static DatabaseProviderRegistry instance;
    private List<DatabaseProvider> providers = new ArrayList<DatabaseProvider>();

    public static synchronized DatabaseProviderRegistry getDefaultRegistry() {
        if (instance == null) {
            instance = new DatabaseProviderRegistry();
            instance.refreshProviders();
        }

        return instance;
    }

    public void refreshProviders() {
        ServiceLoader<DatabaseProvider> serviceLoader = ServiceLoader
                .load(DatabaseProvider.class);
        Iterator<DatabaseProvider> iterator = serviceLoader.iterator();
        List<DatabaseProvider> databaseProviders = new ArrayList<DatabaseProvider>();

        while (iterator.hasNext()) {
            databaseProviders.add(iterator.next());
        }

        Collections.sort(databaseProviders, new DatabaseProviderComparator());
        this.providers = databaseProviders;
    }

    public DatabaseProvider provider() {
        if (providers.isEmpty()) {
            return null;
        }

        return providers.get(0);
    }
}
