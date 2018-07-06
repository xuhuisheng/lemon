package com.mossle.internal.template.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
            logger.info("skip init user data");

            return;
        }

        InputStream is = TemplateDeployer.class.getClassLoader()
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
        String subject = this.processItem(array[2]);
        String content = this.processItem(array[3]);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, line);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateTemplateInfo(code, name, subject, content, lineNo);
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

    public void createOrUpdateTemplateInfo(String code, String name,
            String subject, String content, int lineNo) throws Exception {
        TemplateInfo templateInfo = templateInfoManager.findUniqueBy("code",
                code);

        if (templateInfo == null) {
            // insert
            templateInfo = new TemplateInfo();
            templateInfo.setCode(code);
            templateInfo.setName(name);
            templateInfo.setTenantId(defaultTenantId);
            templateInfoManager.save(templateInfo);
        }

        String hql = "from TemplateField where templateInfo=? and name=?";
        TemplateField templateField;
        // subject
        templateField = templateFieldManager.findUnique(hql, templateInfo,
                "subject");

        if (templateField == null) {
            templateField = new TemplateField();
            templateField.setName("subject");
            templateField.setContent(subject);
            templateField.setTenantId(defaultTenantId);
            templateField.setType("ckeditor");
            templateField.setTemplateInfo(templateInfo);
            templateFieldManager.save(templateField);
        }

        // content
        templateField = templateFieldManager.findUnique(hql, templateInfo,
                "content");

        if (templateField == null) {
            templateField = new TemplateField();
            templateField.setName("content");
            templateField.setContent(content);
            templateField.setTenantId(defaultTenantId);
            templateField.setType("ckeditor");
            templateField.setTemplateInfo(templateInfo);
            templateFieldManager.save(templateField);
        }
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
