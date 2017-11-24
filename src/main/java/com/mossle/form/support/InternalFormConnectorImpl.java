package com.mossle.form.support;

import javax.annotation.Resource;

import com.mossle.form.persistence.domain.FormTemplate;
import com.mossle.form.persistence.manager.FormTemplateManager;

import com.mossle.spi.form.InternalFormConnector;

public class InternalFormConnectorImpl implements InternalFormConnector {
    private FormTemplateManager formTemplateManager;

    public void save(String code, String content, Integer type) {
        FormTemplate formTemplate = new FormTemplate();
        formTemplate.setCode(code);
        formTemplate.setContent(content);
        formTemplate.setType(type);
        formTemplateManager.save(formTemplate);
    }

    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }
}
