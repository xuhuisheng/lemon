package com.mossle.api;

public interface GroupProcessor {
    void insertGroup(String id, String name);

    void updateGroup(String id, String name);

    void removeGroup(String id);
}
