package com.mossle.form.web.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.bpm.FormInfo;
import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.FindStartFormCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

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
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import org.apache.struts2.ServletActionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 电子表单与流程集成的地方.
 * 
 * @author Lingo
 */
public class FormAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(FormAction.class);
    private ProcessEngine processEngine;
    private DynamicModel dynamicModel;
    private DynamicModelManager dynamicModelManager;
    private BpmProcessManager bpmProcessManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private String businessKey;
    private String processDefinitionId;
    private String processDefinitionKey;
    private int processDefinitionVersion;
    private String taskId;
    private Long id;
    private FormTemplateManager formTemplateManager;
    private ModelService modelService;
    private JsonMapper jsonMapper = new JsonMapper();
    private String json = "{}";
    private FormInfo formInfo;
    private FormTemplate formTemplate;
    private List<DynamicModel> dynamicModels;
    private List<TaskDefinition> taskDefinitions;
    private List<String> taskDefinitionKeys;
    private List<String> taskAssignees;

    /**
     * 根据id，加载form模板，显示布局.
     */
    public String loadForm() throws Exception {
        FormTemplate theFormTemplate = formTemplateManager.get(id);
        json = jsonMapper.toJson(theFormTemplate);

        return "loadForm";
    }

    /**
     * 显示草稿箱的内容.
     */
    public String loadModelData() throws Exception {
        if ((businessKey != null) && (!"".equals(businessKey))) {
            // 如果有businessKey就用businessKey查询
            dynamicModel = dynamicModelManager.get(Long.parseLong(businessKey));
        } else {
            // 如果没有businessKey，就用executionId查询
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

    /**
     * 保存草稿.
     */
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
        Long businessKeyLong = null;

        if ((businessKeyString != null) && (!"".equals(businessKeyString))) {
            businessKeyLong = Long.parseLong(businessKeyString);
        }

        if ((taskId != null) && (!"".equals(taskId))) {
            Task task = processEngine.getTaskService().createTaskQuery()
                    .taskId(taskId).singleResult();
            String executionId = task.getExecutionId();
            modelService
                    .saveTaskDraft(businessKeyLong, executionId, parameters);
        } else {
            modelService.saveProcessInstanceDraft(businessKeyLong,
                    processDefinitionId, parameters);
        }

        return "saveDraft";
    }

    /**
     * 列出所有草稿.
     */
    public String listDrafts() throws Exception {
        dynamicModels = dynamicModelManager.findBy("status", 0);

        return "listDrafts";
    }

    /**
     * 显示启动流程的表单.
     */
    public String viewStartForm() throws Exception {
        if (processDefinitionId == null) {
            this.processDefinitionId = processEngine.getRepositoryService()
                    .createProcessDefinitionQuery()
                    .processDefinitionKey(this.processDefinitionKey)
                    .processDefinitionVersion(this.processDefinitionVersion)
                    .singleResult().getId();
        }

        formInfo = processEngine.getManagementService().executeCommand(
                new FindStartFormCmd(processDefinitionId));

        if (formInfo.isFormExists()) {
            this.formTemplate = formTemplateManager.findUniqueBy("name",
                    formInfo.getFormKey());

            if ((businessKey != null) && (!"".equals(businessKey))) {
                dynamicModel = dynamicModelManager.get(Long
                        .parseLong(businessKey));

                Map map = new HashMap();

                for (DynamicModelData dynamicModelData : dynamicModel
                        .getDynamicModelDatas()) {
                    map.put(dynamicModelData.getName(),
                            dynamicModelData.getValue());
                }

                json = jsonMapper.toJson(map);
            }

            return "viewStartForm";
        } else {
            return taskConf();
        }
    }

    /**
     * 配置每个任务的参与人.
     */
    public String taskConf() {
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService().getProcessDefinition(
                        processDefinitionId);
        BpmProcess bpmProcess = bpmProcessManager
                .findUnique(
                        "from BpmProcess where processDefinitionKey=? and processDefinitionVersion=?",
                        processDefinition.getKey(),
                        processDefinition.getVersion());

        if ((bpmProcess != null)
                && Integer.valueOf(1).equals(bpmProcess.getUseTaskConf())) {
            FindTaskDefinitionsCmd cmd = new FindTaskDefinitionsCmd(
                    processDefinitionId);
            taskDefinitions = processEngine.getManagementService()
                    .executeCommand(cmd);

            return "taskConf";
        } else {
            return "confirmStartProcessInstance";
        }
    }

    /**
     * 发起流程.
     */
    public String startProcessInstance() throws Exception {
        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());
        // 获得form的信息
        formInfo = processEngine.getManagementService().executeCommand(
                new FindStartFormCmd(processDefinitionId));

        Map<String, String> formTypeMap = new HashMap<String, String>();

        if (formInfo.isFormExists()) {
            this.formTemplate = formTemplateManager.findUniqueBy("name",
                    formInfo.getFormKey());

            String content = formTemplate.getContent();
            logger.info("content : {}", content);

            Map map = jsonMapper.fromJson(content, Map.class);
            logger.info("map : {}", map);

            List<Map> list = (List<Map>) map.get("fields");
            logger.info("list : {}", list);

            for (Map item : list) {
                formTypeMap.put((String) item.get("name"),
                        (String) item.get("type"));
            }
        }

        Long businessKeyLong = null;
        Map<String, Object> processParameters = new HashMap<String, Object>();
        Map<String, String> modelParameters = new HashMap<String, String>();

        if (taskDefinitionKeys != null) {
            DynamicModel dynamicModel;

            if ((businessKey == null) || "".equals(businessKey)) {
                // 如果就没有表单，也要生成一个businessKey，否则后边没法关联
                dynamicModel = modelService.createOrGetDynamicModel(null);
                businessKeyLong = dynamicModel.getId();
                businessKey = Long.toString(businessKeyLong);
            } else {
                // 如果有表单，就从数据库获取数据
                businessKeyLong = Long.parseLong(businessKey);
                dynamicModel = modelService
                        .createOrGetDynamicModel(businessKeyLong);

                for (DynamicModelData data : dynamicModel
                        .getDynamicModelDatas()) {
                    String key = data.getName();
                    String value = data.getValue();
                    String formType = this.getFormType(formTypeMap, key);

                    if ("userPicker".equals(formType)) {
                        processParameters.put(key,
                                new ArrayList(Arrays.asList(value.split(","))));
                    } else {
                        processParameters.put(key, value);
                    }

                    modelParameters.put(key, value);
                }
            }

            // 如果是从配置任务负责人的页面过来，就保存TaskConf，再从草稿中得到数据启动流程
            int index = 0;

            for (String taskDefinitionKey : taskDefinitionKeys) {
                String taskAssignee = taskAssignees.get(index++);
                BpmTaskConf bpmTaskConf = new BpmTaskConf();
                bpmTaskConf.setBusinessKey(businessKey);
                bpmTaskConf.setTaskDefinitionKey(taskDefinitionKey);
                bpmTaskConf.setAssignee(taskAssignee);
                bpmTaskConfManager.save(bpmTaskConf);
            }
        } else {
            // 如果是从startForm过来，就直接保存数据
            Map<String, String[]> parameterMap = ServletActionContext
                    .getRequest().getParameterMap();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                String formType = this.getFormType(formTypeMap, key);

                if (value.length == 1) {
                    if ("userPicker".equals(formType)) {
                        processParameters.put(
                                key,
                                new ArrayList(
                                        Arrays.asList(value[0].split(","))));
                    } else {
                        processParameters.put(key, value[0]);
                    }

                    modelParameters.put(key, value[0]);
                }
            }

            if ((businessKey != null) && (!"".equals(businessKey))) {
                businessKeyLong = Long.parseLong(businessKey);
            }

            modelService.saveProcessInstance(businessKeyLong,
                    processDefinitionId, null, modelParameters);
        }

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId, businessKey,
                        processParameters);
        modelService.saveProcessInstance(businessKeyLong, processDefinitionId,
                processInstance.getId(), modelParameters);

        return "startProcessInstance";
    }

    /**
     * 工具方法，获取表单的类型.
     */
    private String getFormType(Map<String, String> formTypeMap, String name) {
        if (formTypeMap.containsKey(name)) {
            return formTypeMap.get(name);
        } else {
            return "textfield";
        }
    }

    /**
     * 显示任务表单.
     */
    public String viewTaskForm() throws Exception {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        FormService formService = processEngine.getFormService();
        String taskFormKey = formService.getTaskFormKey(
                task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        formTemplate = formTemplateManager.findUniqueBy("name", taskFormKey);
        this.loadModelDataComplete();
        formInfo = new FormInfo();
        formInfo.setTaskId(taskId);

        return "viewTaskForm";
    }

    /**
     * 完成任务.
     */
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

        processEngine.getManagementService().executeCommand(
                new CompleteTaskWithCommentCmd(taskId, parameters, "完成"));

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

            if (dynamicModel == null) {
                dynamicModel = dynamicModelManager.findUniqueBy("instanceId",
                        task.getProcessInstanceId());
            }
        }

        Map map = new HashMap();

        for (DynamicModelData dynamicModelData : dynamicModel
                .getDynamicModelDatas()) {
            map.put(dynamicModelData.getName(), dynamicModelData.getValue());
        }

        json = jsonMapper.toJson(map);
    }

    // ~ ======================================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setDynamicModelManager(DynamicModelManager dynamicModelManager) {
        this.dynamicModelManager = dynamicModelManager;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
    }

    // ~ ======================================================================
    public DynamicModel getDynamicModel() {
        return dynamicModel;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public void setProcessDefinitionVersion(int processDefinitionVersion) {
        this.processDefinitionVersion = processDefinitionVersion;
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

    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitionKeys(List<String> taskDefinitionKeys) {
        this.taskDefinitionKeys = taskDefinitionKeys;
    }

    public void setTaskAssignees(List<String> taskAssignees) {
        this.taskAssignees = taskAssignees;
    }
}
