package com.mossle.api.database;

import java.util.Comparator;

public class DatabaseProviderComparator implements Comparator<DatabaseProvider> {
    public int compare(DatabaseProvider o1, DatabaseProvider o2) {
        return o1.getPriority() - o2.getPriority();
    }
}
