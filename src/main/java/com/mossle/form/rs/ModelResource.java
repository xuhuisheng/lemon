package com.mossle.form.rs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.FormTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("model")
public class ModelResource {
    private static final String CHARSET = ";charset=UTF-8";
    private static Logger logger = LoggerFactory.getLogger(ModelResource.class);
    private FormTemplateManager formTemplateManager;

    @GET
    @Path("forms")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + CHARSET })
    public List<String> getForms() {
        List<String> forms = new ArrayList<String>();
        List<FormTemplate> formTemplates = formTemplateManager.getAll();

        for (FormTemplate formTemplate : formTemplates) {
            forms.add(formTemplate.getName());
        }

        return forms;
    }

    @Resource
    public void FormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }
}
