package com.mossle.api.dict;

public class MockDictConnector implements DictConnector {
    public DictDTO findDictByName(String dictName, String typeName,
            String tenantId) {
        return null;
    }

    public DictDTO findDictByType(String typeName, String tenantId) {
        return null;
    }
}
