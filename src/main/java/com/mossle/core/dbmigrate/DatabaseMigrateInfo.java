package com.mossle.core.dbmigrate;

public class DatabaseMigrateInfo implements Comparable<DatabaseMigrateInfo> {
    private String name;
    private String table;
    private String location;
    private boolean enabled = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int compareTo(DatabaseMigrateInfo o) {
        if ((o == null) || (o.getName() == null)) {
            return 1;
        }

        if (this.name == null) {
            return -1;
        }

        return this.name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DatabaseMigrateInfo)) {
            return false;
        }

        return this.compareTo((DatabaseMigrateInfo) o) == 0;
    }

    @Override
    public int hashCode() {
        if (this.name == null) {
            return super.hashCode();
        }

        return this.name.hashCode();
    }
}
