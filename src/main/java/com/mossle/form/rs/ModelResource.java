package com.mossle.form.rs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.form.persistence.domain.FormTemplate;
import com.mossle.form.persistence.manager.FormTemplateManager;

import org.springframework.stereotype.Component;

@Component
@Path("model")
public class ModelResource {
    private static final String CHARSET = ";charset=UTF-8";
    private FormTemplateManager formTemplateManager;
    private TenantHolder tenantHolder;

    @GET
    @Path("forms")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + CHARSET })
    public List<String> getForms() {
        String tenantId = tenantHolder.getTenantId();
        List<String> forms = new ArrayList<String>();
        List<FormTemplate> formTemplates = formTemplateManager.findBy(
                "tenantId", tenantId);

        for (FormTemplate formTemplate : formTemplates) {
            forms.add(formTemplate.getName());
        }

        return forms;
    }

    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
