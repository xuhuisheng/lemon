package com.mossle.api.dict;

import java.util.List;

public class MockDictConnector implements DictConnector {
    public DictDTO findDictByName(String dictName, String typeName,
            String tenantId) {
        return null;
    }

    public DictDTO findDictByType(String typeName, String tenantId) {
        return null;
    }

    public List<DictDTO> findDictsByType(String typeCode, String tenantId) {
        return null;
    }

    public DictDTO findDictByCodeAndName(String typeCode, String name,
            String tenantId) {
        return null;
    }
}
