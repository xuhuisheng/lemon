package com.mossle.form.support;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.FormTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormConnectorImpl implements FormConnector {
    private static Logger logger = LoggerFactory
            .getLogger(FormConnectorImpl.class);
    private FormTemplateManager formTemplateManager;

    public List<FormDTO> getAll(String scopeId) {
        List<FormTemplate> formTemplates = formTemplateManager.getAll();
        List<FormDTO> formDtos = new ArrayList<FormDTO>();

        for (FormTemplate formTemplate : formTemplates) {
            FormDTO formDto = new FormDTO();
            formDtos.add(formDto);
            formDto.setId(formTemplate.toString());
            formDto.setCode(formTemplate.getCode());
            formDto.setName(formTemplate.getName());
        }

        return formDtos;
    }

    public FormDTO findForm(String code) {
        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                code);

        if (formTemplate == null) {
            logger.error("cannot find form : {}", code);

            return null;
        }

        FormDTO formDto = new FormDTO();
        formDto.setId(formTemplate.getId().toString());
        formDto.setCode(formTemplate.getCode());
        formDto.setName(formTemplate.getName());

        if (Integer.valueOf(1).equals(formTemplate.getType())) {
            formDto.setRedirect(true);
            formDto.setUrl(formTemplate.getContent());
        } else {
            formDto.setContent(formTemplate.getContent());
        }

        return formDto;
    }

    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }
}
