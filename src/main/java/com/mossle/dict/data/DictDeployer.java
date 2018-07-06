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

public class DictDeployer {
    private static Logger logger = LoggerFactory.getLogger(DictDeployer.class);
    private DictTypeManager dictTypeManager;
    private DictInfoManager dictInfoManager;
    private String dataFilePath = "data/dict.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init user data");

            return;
        }

        InputStream is = DictDeployer.class.getClassLoader()
                .getResourceAsStream(dataFilePath);
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

    public void processLine(String line, int lineNo) throws Exception {
        String[] array = line.split(",");
        String code = this.processItem(array[0]);
        String name = this.processItem(array[1]);
        String type = this.processItem(array[2]);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, line);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateDictType(code, name, type, lineNo);
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

        DictInfoDeployer dictInfoDeployer = new DictInfoDeployer();
        dictInfoDeployer.setDictTypeManager(dictTypeManager);
        dictInfoDeployer.setDictInfoManager(dictInfoManager);
        dictInfoDeployer.setTypeCode(code);
        dictInfoDeployer.setTypeName(name);
        dictInfoDeployer.init();
    }

    @Resource
    public void setDictTypeManager(DictTypeManager dictTypeManager) {
        this.dictTypeManager = dictTypeManager;
    }

    @Resource
    public void setDictInfoManager(DictInfoManager dictInfoManager) {
        this.dictInfoManager = dictInfoManager;
    }
}
