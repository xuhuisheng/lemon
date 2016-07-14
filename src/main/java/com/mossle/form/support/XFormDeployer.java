package com.mossle.form.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.form.persistence.domain.FormTemplate;
import com.mossle.form.persistence.manager.FormTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.core.io.Resource;

public class XFormDeployer implements ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(XFormDeployer.class);
    private boolean autoDeploy;
    private String deployPath = "classpath:xform/*.json";
    private ApplicationContext applicationContext;
    private JsonMapper jsonMapper = new JsonMapper();
    private FormTemplateManager formTemplateManager;
    private TenantConnector tenantConnector;
    private String defaultTenantCode = "default";

    @PostConstruct
    public void init() {
        if (!autoDeploy) {
            return;
        }

        TenantDTO tenantDto = tenantConnector.findByCode(defaultTenantCode);
        String tenantId = tenantDto.getId();

        try {
            for (Resource resource : applicationContext
                    .getResources(deployPath)) {
                String text = readText(resource);
                Map<String, Object> map = jsonMapper.fromJson(text, Map.class);
                logger.debug("deploy : {}", map);

                String code = (String) map.get("code");
                String name = (String) map.get("name");
                String hql = "from FormTemplate where code=? and tenantId=?";
                FormTemplate formTemplate = formTemplateManager.findUnique(hql,
                        code, tenantId);

                if (formTemplate != null) {
                    continue;
                }

                formTemplate = new FormTemplate();
                formTemplate.setCode(code);
                formTemplate.setName(name);
                formTemplate.setContent(text);
                formTemplate.setType(0);
                formTemplate.setTenantId(tenantId);
                formTemplateManager.save(formTemplate);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String readText(Resource resource) throws Exception {
        InputStream is = resource.getInputStream();
        int size = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((size = is.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, size);
        }

        is.close();

        return new String(baos.toByteArray(), "utf-8");
    }

    public void setDeployPath(String deployPath) {
        this.deployPath = deployPath;
    }

    public void setAutoDeploy(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @javax.annotation.Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }

    public void setDefaultTenantCode(String defaultTenantCode) {
        this.defaultTenantCode = defaultTenantCode;
    }

    @javax.annotation.Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
