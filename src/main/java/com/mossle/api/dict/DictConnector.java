package com.mossle.api.dict;

import java.util.List;

public interface DictConnector {
    DictDTO findDictByName(String dictName, String typeName, String tenantId);

    DictDTO findDictByType(String typeName, String tenantId);

    List<DictDTO> findDictsByType(String typeCode, String tenantId);

    DictDTO findDictByCodeAndName(String typeCode, String name, String tenantId);
}
