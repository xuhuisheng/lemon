package com.mossle.api.internal;

public interface DictConnector {
    DictDTO findDictByName(String dictName, String typeName);

    DictDTO findDictByType(String typeName);
}
