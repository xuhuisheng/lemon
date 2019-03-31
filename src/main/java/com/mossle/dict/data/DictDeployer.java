package com.mossle.dict.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

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

        DictTypeCallback dictTypeCallback = new DictTypeCallback();
        dictTypeCallback.setDictTypeManager(dictTypeManager);
        dictTypeCallback.setDictInfoManager(dictInfoManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                dictTypeCallback);
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
