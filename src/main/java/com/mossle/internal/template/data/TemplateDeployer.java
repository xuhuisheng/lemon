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

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.internal.template.data.TemplateDeployer")
public class TemplateDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(TemplateDeployer.class);
    private TemplateInfoManager templateInfoManager;
    private TemplateFieldManager templateFieldManager;
    private String dataFilePath = "data/template/template.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip template init data");

            return;
        }

        logger.info("start template init data");

        TemplateCallback templateCallback = new TemplateCallback();
        templateCallback.setTemplateInfoManager(templateInfoManager);
        templateCallback.setTemplateFieldManager(templateFieldManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                templateCallback);
        logger.info("end template init data");
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

    @Value("${template.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
