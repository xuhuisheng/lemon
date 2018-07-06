package com.mossle.dict.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.dict.persistence.domain.DictInfo;
import com.mossle.dict.persistence.domain.DictType;
import com.mossle.dict.persistence.manager.DictInfoManager;
import com.mossle.dict.persistence.manager.DictTypeManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictInfoDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(DictInfoDeployer.class);
    private DictTypeManager dictTypeManager;
    private DictInfoManager dictInfoManager;
    private String dataFilePath = "data/dict/.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private String typeCode;
    private String typeName;

    public void init() throws Exception {
        if (!enable) {
            logger.info("skip init user data");

            return;
        }

        InputStream is = DictInfoDeployer.class.getClassLoader()
                .getResourceAsStream("data/dict/" + typeCode + ".csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                dataFileEncoding));

        String line = null;
        int lineNo = 0;

        while ((line = reader.readLine()) != null) {
            lineNo++;

            if (lineNo == 1) {
                continue;
            }

            this.processLine(line, lineNo);
        }
    }

    public void processLine(String line, int lineNo) {
        String[] array = line.split(",");
        String name = this.processItem(array[0]);
        String value = this.processItem(array[1]);

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, line);

            return;
        }

        DictType dictType = dictTypeManager.findUniqueBy("code", typeCode);

        if (dictType == null) {
            logger.info("cannot find dict type : {}", typeCode);

            return;
        }

        this.createOrUpdateDictInfo(name, value, dictType, lineNo);
    }

    public String processItem(String text) {
        if (text == null) {
            logger.info("text is null");

            return "";
        }

        text = text.trim();

        if (text.charAt(0) == '\"') {
            text = text.substring(1);
        }

        if (text.charAt(text.length() - 1) == '\"') {
            text = text.substring(0, text.length() - 1);
        }

        return text;
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

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
