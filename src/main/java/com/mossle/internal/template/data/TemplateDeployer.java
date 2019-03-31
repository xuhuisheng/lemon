package com.mossle.internal.template.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.template.persistence.domain.TemplateField;
import com.mossle.internal.template.persistence.domain.TemplateInfo;
import com.mossle.internal.template.persistence.manager.TemplateFieldManager;
import com.mossle.internal.template.persistence.manager.TemplateInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(TemplateDeployer.class);
    private TemplateInfoManager templateInfoManager;
    private TemplateFieldManager templateFieldManager;
    private String dataFilePath = "data/template.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init template data");

            return;
        }

        TemplateCallback templateCallback = new TemplateCallback();
        templateCallback.setTemplateInfoManager(templateInfoManager);
        templateCallback.setTemplateFieldManager(templateFieldManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                templateCallback);
    }

    @Resource
    public void setTemplateInfoManager(TemplateInfoManager templateInfoManager) {
        this.templateInfoManager = templateInfoManager;
    }

    @Resource
    public void setTemplateFieldManager(
            TemplateFieldManager templateFieldManager) {
        this.templateFieldManager = templateFieldManager;
    }
}
