package com.mossle.api.internal;

public class MockDictConnector implements DictConnector {
    public DictDTO findDictByName(String dictName, String typeName) {
        return null;
    }

    public DictDTO findDictByType(String typeName) {
        return null;
    }
}
