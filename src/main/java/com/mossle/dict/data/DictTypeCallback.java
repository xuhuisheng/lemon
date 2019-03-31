package com.mossle.dict.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;
import com.mossle.core.csv.CsvProcessor;

import com.mossle.dict.persistence.domain.DictType;
import com.mossle.dict.persistence.manager.DictInfoManager;
import com.mossle.dict.persistence.manager.DictTypeManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictTypeCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(DictTypeCallback.class);
    private DictTypeManager dictTypeManager;
    private DictInfoManager dictInfoManager;
    private String defaultTenantId = "1";
    private String dataFilePath = "data/dict/.csv";
    private String dataFileEncoding = "GB2312";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String type = list.get(2);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateDictType(code, name, type, lineNo);
    }

    public void createOrUpdateDictType(String code, String name, String type,
            int lineNo) throws Exception {
        DictType dictType = dictTypeManager.findUniqueBy("code", code);

        if (dictType == null) {
            // insert
            dictType = new DictType();
            dictType.setCode(code);
            dictType.setName(name);
            dictType.setType(type);
            dictType.setTenantId(defaultTenantId);
            dictTypeManager.save(dictType);
        }

        DictInfoCallback dictInfoCallback = new DictInfoCallback();
        dictInfoCallback.setDictTypeManager(dictTypeManager);
        dictInfoCallback.setDictInfoManager(dictInfoManager);
        dictInfoCallback.setTypeCode(code);
        new CsvProcessor().process("data/dict/" + code + ".csv",
                dataFileEncoding, dictInfoCallback);
    }

    public void setDictTypeManager(DictTypeManager dictTypeManager) {
        this.dictTypeManager = dictTypeManager;
    }

    public void setDictInfoManager(DictInfoManager dictInfoManager) {
        this.dictInfoManager = dictInfoManager;
    }
}
