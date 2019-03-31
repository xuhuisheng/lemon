package com.mossle.dict.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.dict.persistence.domain.DictInfo;
import com.mossle.dict.persistence.domain.DictType;
import com.mossle.dict.persistence.manager.DictInfoManager;
import com.mossle.dict.persistence.manager.DictTypeManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictInfoCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(DictInfoCallback.class);
    private DictTypeManager dictTypeManager;
    private DictInfoManager dictInfoManager;
    private String defaultTenantId = "1";
    private String typeCode;

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String value = list.get(1);
        String ref = "";

        if (list.size() > 2) {
            ref = list.get(2);
        }

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, list);

            return;
        }

        DictType dictType = dictTypeManager.findUniqueBy("code", typeCode);

        if (dictType == null) {
            logger.info("cannot find dict type : {}", typeCode);

            return;
        }

        this.createOrUpdateDictInfo(name, value, dictType, lineNo);
    }

    public void createOrUpdateDictInfo(String name, String value,
            DictType dictType, int lineNo) {
        DictInfo dictInfo = dictInfoManager.findUniqueBy("name", name);

        if (dictInfo != null) {
            logger.info("skip exists dict info : {}", name);

            return;
        }

        dictInfo = new DictInfo();
        dictInfo.setName(name);
        dictInfo.setValue(value);
        dictInfo.setPriority(lineNo);
        dictInfo.setDictType(dictType);
        dictInfoManager.save(dictInfo);
    }

    public void setDictTypeManager(DictTypeManager dictTypeManager) {
        this.dictTypeManager = dictTypeManager;
    }

    public void setDictInfoManager(DictInfoManager dictInfoManager) {
        this.dictInfoManager = dictInfoManager;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
