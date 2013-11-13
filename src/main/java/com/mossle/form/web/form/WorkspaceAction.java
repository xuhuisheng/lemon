package com.mossle.form.web.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.bpm.FormInfo;
import com.mossle.bpm.cmd.FindStartFormCmd;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.struts2.BaseAction;

import com.mossle.form.domain.DynamicModel;
import com.mossle.form.domain.DynamicModelData;
import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.DynamicModelManager;
import com.mossle.form.manager.FormTemplateManager;
import com.mossle.form.service.ModelService;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import org.apache.struts2.ServletActionContext;

/**
 * workspace action.
 * 
 * @author Lingo
 */
public class WorkspaceAction extends BaseAction {
    private ProcessEngine processEngine;
    private DynamicModel dynamicModel;
    private DynamicModelManager dynamicModelManager;
    private String businessKey;
    private String processDefinitionId;
    private String taskId;
    private Long id;
    private FormTemplateManager formTemplateManager;
    private ModelService modelService;
    private JsonMapper jsonMapper = new JsonMapper();
    private String json;
    private FormInfo formInfo;
    private FormTemplate formTemplate;
    private List<DynamicModel> dynamicModels;

    public String loadForm() throws Exception {
        FormTemplate formTemplate = formTemplateManager.get(id);
        json = jsonMapper.toJson(formTemplate);

        return "loadForm";
    }

    public String loadModelData() throws Exception {
        if ((businessKey != null) && (!"".equals(businessKey))) {
            dynamicModel = dynamicModelManager.get(Long.parseLong(businessKey));
        } else {
            Task task = processEngine.getTaskService().createTaskQuery()
                    .taskId(taskId).singleResult();
            String executionId = task.getExecutionId();
            dynamicModel = dynamicModelManager.findUniqueBy("executionId",
                    executionId);
        }

        StringBuilder buff = new StringBuilder();
        buff.append("{");

        for (DynamicModelData dynamicModelData : dynamicModel
                .getDynamicModelDatas()) {
            buff.append("\'").append(dynamicModelData.getName())
                    .append("\':\'").append(dynamicModelData.getValue())
                    .append("\',");
        }

        buff.deleteCharAt(buff.length() - 1);
        buff.append("}");
        json = jsonMapper.toJson(buff.toString());

        return "loadModelData";
    }

    public String saveDraft() throws Exception {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        Map<String, String> parameters = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            if (value.length == 1) {
                parameters.put(key, value[0]);
            }
        }

        String businessKeyString = parameters.get("businessKey");
        Long businessKey = null;

        if ((businessKeyString != null) && (!"".equals(businessKeyString))) {
            businessKey = Long.parseLong(businessKeyString);
        }

        if ((taskId != null) && (!"".equals(taskId))) {
            Task task = processEngine.getTaskService().createTaskQuery()
                    .taskId(taskId).singleResult();
            String executionId = task.getExecutionId();
            modelService.saveTaskDraft(businessKey, executionId, parameters);
        } else {
            modelService.saveProcessInstanceDraft(businessKey,
                    processDefinitionId, parameters);
        }

        return "saveDraft";
    }

    public String listDrafts() throws Exception {
        dynamicModels = dynamicModelManager.findBy("status", 0);

        return "listDrafts";
    }

    public String prepareStartProcessInstance() throws Exception {
        formInfo = processEngine.getManagementService().executeCommand(
                new FindStartFormCmd(processDefinitionId));

        if (formInfo.isFormExists()) {
            this.formTemplate = formTemplateManager.findUniqueBy("name",
                    formInfo.getFormKey());

            if ((businessKey != null) && (!"".equals(businessKey))) {
                dynamicModel = dynamicModelManager.get(Long
                        .parseLong(businessKey));
            }

            return "prepareStartProcessInstance";
        } else {
            return "confirmStartProcessInstance";
        }
    }

    public String startProcessInstance() throws Exception {
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());

        // this.saveDraft();
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        Map<String, Object> parameters = new HashMap<String, Object>();
        Map<String, String> modelParameters = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            if (value.length == 1) {
                parameters.put(key, value[0]);
                modelParameters.put(key, value[0]);
            }
        }

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId, parameters);

        // ~
        String businessKeyString = modelParameters.get("businessKey");
        Long businessKey = null;

        if ((businessKeyString != null) && (!"".equals(businessKeyString))) {
            businessKey = Long.parseLong(businessKeyString);
        }

        modelService.saveProcessInstance(businessKey, processDefinitionId,
                processInstance.getId(), modelParameters);

        return "startProcessInstance";
    }

    public String prepareCompleteTask() throws Exception {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        FormService formService = processEngine.getFormService();
        String taskFormKey = formService.getTaskFormKey(
                task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        formTemplate = formTemplateManager.findUniqueBy("name", taskFormKey);
        this.loadModelDataComplete();
        formInfo = new FormInfo();
        formInfo.setTaskId(taskId);

        return "prepareCompleteTask";
    }

    public String completeTask() throws Exception {
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());
        this.saveDraft();

        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        Map<String, Object> parameters = new HashMap<String, Object>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            if (value.length == 1) {
                parameters.put(key, value[0]);
            }
        }

        processEngine.getTaskService().complete(taskId, parameters);

        return "completeTask";
    }

    public void loadModelDataComplete() throws Exception {
        if ((businessKey != null) && (!"".equals(businessKey))) {
            dynamicModel = dynamicModelManager.get(Long.parseLong(businessKey));
        } else {
            Task task = processEngine.getTaskService().createTaskQuery()
                    .taskId(taskId).singleResult();
            String executionId = task.getExecutionId();
            dynamicModel = dynamicModelManager.findUniqueBy("executionId",
                    executionId);
        }
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setDynamicModelManager(DynamicModelManager dynamicModelManager) {
        this.dynamicModelManager = dynamicModelManager;
    }

    public DynamicModel getDynamicModel() {
        return dynamicModel;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }

    public String getJson() {
        return json;
    }

    public FormInfo getFormInfo() {
        return formInfo;
    }

    public FormTemplate getFormTemplate() {
        return formTemplate;
    }

    public List<DynamicModel> getDynamicModels() {
        return dynamicModels;
    }
}
