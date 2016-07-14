package com.mossle.dict.support;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.dict.DictConnector;
import com.mossle.api.dict.DictDTO;
import com.mossle.api.dict.DictDataDTO;

import com.mossle.dict.persistence.domain.DictData;
import com.mossle.dict.persistence.domain.DictInfo;
import com.mossle.dict.persistence.domain.DictSchema;
import com.mossle.dict.persistence.domain.DictType;
import com.mossle.dict.persistence.manager.DictDataManager;
import com.mossle.dict.persistence.manager.DictInfoManager;
import com.mossle.dict.persistence.manager.DictSchemaManager;
import com.mossle.dict.persistence.manager.DictTypeManager;

public class DictConnectorImpl implements DictConnector {
    private DictInfoManager dictInfoManager;
    private DictDataManager dictDataManager;
    private DictSchemaManager dictSchemaManager;
    private DictTypeManager dictTypeManager;

    public DictDTO findDictByName(String name, String typeName, String tenantId) {
        String dictInfoHql = "from DictInfo where name=? and dictType.name=? and tenantId=?";
        DictInfo dictInfo = dictInfoManager.findUnique(dictInfoHql, name,
                typeName, tenantId);
        DictType dictType = dictInfo.getDictType();
        DictDTO dictDto = new DictDTO();
        dictDto.setName(dictInfo.getName());
        dictDto.setType(dictType.getName());
        dictDto.setValue(dictInfo.getValue());

        String hql = "from DictData where dictSchema.dictType.id=? order by dictSchema.priority";
        List<DictData> dictDatas = dictDataManager.find(hql, dictType.getId());

        for (DictData dictData : dictDatas) {
            DictDataDTO dictDataDto = new DictDataDTO();
            dictDataDto.setName(dictData.getName());
            dictDataDto.setType(dictData.getDictSchema().getType());
            dictDataDto.setValue(dictData.getValue());
            dictDto.addData(dictData.getName(), dictDataDto);
        }

        return dictDto;
    }

    public DictDTO findDictByType(String typeName, String tenantId) {
        DictType dictType = dictTypeManager
                .findUnique("from DictType where name=? and tenantId=?",
                        typeName, tenantId);
        DictDTO dictDto = new DictDTO();
        dictDto.setType(dictType.getName());

        String hql = "from DictSchema where dictType.id=? order by priority";
        List<DictSchema> dictSchemas = dictSchemaManager.find(hql,
                dictType.getId());

        for (DictSchema dictSchema : dictSchemas) {
            DictDataDTO dictDataDto = new DictDataDTO();
            dictDataDto.setName(dictSchema.getName());
            dictDataDto.setType(dictSchema.getType());
            dictDto.addData(dictSchema.getName(), dictDataDto);
        }

        return dictDto;
    }

    @Resource
    public void setDictInfoManager(DictInfoManager dictInfoManager) {
        this.dictInfoManager = dictInfoManager;
    }

    @Resource
    public void setDictDataManager(DictDataManager dictDataManager) {
        this.dictDataManager = dictDataManager;
    }

    @Resource
    public void setDictSchemaManager(DictSchemaManager dictSchemaManager) {
        this.dictSchemaManager = dictSchemaManager;
    }

    @Resource
    public void setDictTypeManager(DictTypeManager dictTypeManager) {
        this.dictTypeManager = dictTypeManager;
    }
}
