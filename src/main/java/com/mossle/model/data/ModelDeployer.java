package com.mossle.model.data;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDefinition;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;

import com.mossle.model.persistence.domain.ModelField;
import com.mossle.model.persistence.domain.ModelInfo;
import com.mossle.model.persistence.manager.ModelFieldManager;
import com.mossle.model.persistence.manager.ModelInfoManager;

import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.activiti.engine.repository.ProcessDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.model.data.ModelDeployer")
public class ModelDeployer {
    private Logger logger = LoggerFactory.getLogger(ModelDeployer.class);
    private boolean enable;
    private ProcessConnector processConnector;
    private HumanTaskConnector humanTaskConnector;
    private TaskDefinitionConnector taskDefinitionConnector;
    private FormConnector formConnector;
    private ModelInfoManager modelInfoManager;
    private ModelFieldManager modelFieldManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private String defaultTenantCode;
    private TenantConnector tenantConnector;

    @PostConstruct
    public void init() {
        if (!enable) {
            logger.info("skip model init data");

            return;
        }

        logger.info("start model init data");

        try {
            TenantDTO tenantDto = tenantConnector.findByCode(defaultTenantCode);
            Page page = processConnector.findProcessDefinitions(
                    tenantDto.getId(), new Page());
            List<ProcessDefinition> processDefinitions = (List<ProcessDefinition>) page
                    .getResult();

            for (ProcessDefinition processDefinition : processDefinitions) {
                this.processBusiness(processDefinition);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        logger.info("end model init data");
    }

    public void processBusiness(ProcessDefinition processDefinition) {
        String processDefinitionId = processDefinition.getId();
        List<HumanTaskDefinition> humanTaskDefinitions = humanTaskConnector
                .findHumanTaskDefinitions(processDefinitionId);
        ModelInfo modelInfo = modelInfoManager.findUniqueBy("code",
                processDefinitionId);

        if (modelInfo == null) {
            modelInfo = new ModelInfo();
            modelInfo.setCode(processDefinitionId);
            modelInfo.setName(processDefinition.getName());
            modelInfo.setTenantId(processDefinition.getTenantId());
            modelInfoManager.save(modelInfo);
        }

        Map<String, FormField> formFieldMap = new HashMap<String, FormField>();

        for (HumanTaskDefinition humanTaskDefinition : humanTaskDefinitions) {
            try {
                com.mossle.spi.humantask.FormDTO formDto = taskDefinitionConnector
                        .findForm(humanTaskDefinition.getKey(),
                                processDefinitionId);

                if (formDto == null) {
                    continue;
                }

                String formKey = formDto.getKey();
                this.processForm(processDefinitionId, formKey,
                        modelInfo.getTenantId(), formFieldMap);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        for (FormField formField : formFieldMap.values()) {
            String hql = "from ModelField where name=? and modelInfo=?";
            ModelField modelField = modelFieldManager.findUnique(hql,
                    formField.getName(), modelInfo);

            if (modelField != null) {
                continue;
            }

            modelField = new ModelField();
            modelField.setModelInfo(modelInfo);
            modelField.setCode(formField.getName());
            modelField.setType(formField.getType());
            modelField.setName(formField.getLabel());
            modelField.setSearchable("true");
            modelField.setDisplayable("true");
            modelField.setViewForm("true");
            modelField.setViewList("true");
            modelField.setTenantId(modelInfo.getTenantId());
            modelFieldManager.save(modelField);
        }
    }

    public void processForm(String processDefinitionId, String formKey,
            String tenantId, Map<String, FormField> formFieldMap)
            throws IOException {
        FormDTO formDto = formConnector.findForm(formKey, tenantId);

        if (formDto == null) {
            return;
        }

        if (formDto.isRedirect()) {
            return;
        }

        Map<String, Object> formJson = jsonMapper.fromJson(
                formDto.getContent(), Map.class);
        List<Map<String, Object>> sections = (List<Map<String, Object>>) formJson
                .get("sections");

        for (Map<String, Object> section : sections) {
            if (!"grid".equals(section.get("type"))) {
                continue;
            }

            List<Map<String, Object>> fields = (List<Map<String, Object>>) section
                    .get("fields");

            for (Map<String, Object> field : fields) {
                String type = (String) field.get("type");
                String name = (String) field.get("name");
                String label = name;

                if ("label".equals(type)) {
                    continue;
                }

                FormField formField = formFieldMap.get(name);

                if (formField != null) {
                    continue;
                }

                formField = new FormField();
                formField.setName(name);
                formField.setLabel(label);
                formField.setType(type);
                formFieldMap.put(name, formField);
            }
        }
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }

    @Resource
    public void setModelInfoManager(ModelInfoManager modelInfoManager) {
        this.modelInfoManager = modelInfoManager;
    }

    @Resource
    public void setModelFieldManager(ModelFieldManager modelFieldManager) {
        this.modelFieldManager = modelFieldManager;
    }

    public void setDefaultTenantCode(String defaultTenantCode) {
        this.defaultTenantCode = defaultTenantCode;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    @Value("${model.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
