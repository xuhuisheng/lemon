package com.mossle.internal.template.data;

import java.util.Date;
import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.template.persistence.domain.TemplateField;
import com.mossle.internal.template.persistence.domain.TemplateInfo;
import com.mossle.internal.template.persistence.manager.TemplateFieldManager;
import com.mossle.internal.template.persistence.manager.TemplateInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(TemplateCallback.class);
    private String defaultTenantId = "1";
    private TemplateInfoManager templateInfoManager;
    private TemplateFieldManager templateFieldManager;

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String subject = list.get(2);
        String content = list.get(3);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateTemplateInfo(code, name, subject, content, lineNo);
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

    public void setTemplateInfoManager(TemplateInfoManager templateInfoManager) {
        this.templateInfoManager = templateInfoManager;
    }

    public void setTemplateFieldManager(
            TemplateFieldManager templateFieldManager) {
        this.templateFieldManager = templateFieldManager;
    }
}
