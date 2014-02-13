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
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.struts2.BaseAction;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.manager.FormTemplateManager;
import com.mossle.form.operation.CompleteTaskOperation;
import com.mossle.form.operation.ConfAssigneeOperation;
import com.mossle.form.operation.SaveDraftOperation;
import com.mossle.form.operation.StartProcessOperation;

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
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 电子表单与流程集成的地方.
 * 
 * @author Lingo
 */
@Results({ @Result(name = FormAction.RELOAD_REDIRECT, location = "${redirectUrl}", type = "redirect") })
public class FormAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(FormAction.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    public static final String RELOAD_REDIRECT = "reload-redirect";
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private String businessKey;
    private String processDefinitionId;
    private String processDefinitionKey;
    private int processDefinitionVersion;
    private String taskId;
    private Long id;
    private FormTemplateManager formTemplateManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private String json = "{}";
    private FormInfo formInfo;
    private FormTemplate formTemplate;
    private List<TaskDefinition> taskDefinitions;
    private List<String> taskDefinitionKeys;
    private List<String> taskAssignees;
    private KeyValue keyValue;
    private List<Record> records;
    private String status;
    private String redirectUrl;
    private String nextStep = "startProcess";
    private long bpmProcessId;

    /**
     * 保存草稿.
     */
    public String saveDraft() throws Exception {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        new SaveDraftOperation().execute(parameterMap);

        return "saveDraft";
    }

    /**
     * 列出所有草稿.
     */
    public String listDrafts() throws Exception {
        records = keyValue.findByStatus(STATUS_DRAFT_PROCESS);

        return "listDrafts";
    }

    /**
     * 显示启动流程的表单.
     */
    public String viewStartForm() throws Exception {
        BpmProcess bpmProcess = bpmProcessManager.get(bpmProcessId);
        processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();

        formInfo = processEngine.getManagementService().executeCommand(
                new FindStartFormCmd(processDefinitionId));

        if (formInfo.isFormExists()) {
            // 如果找到了form，就显示表单
            if (Integer.valueOf(1).equals(bpmProcess.getUseTaskConf())) {
                // 如果需要配置负责人
                nextStep = "taskConf";
            } else {
                nextStep = "confirmStartProcess";
            }

            this.formTemplate = formTemplateManager.findUniqueBy("name",
                    formInfo.getFormKey());

            if (Integer.valueOf(1).equals(formTemplate.getType())) {
                redirectUrl = formTemplate.getContent();

                return RELOAD_REDIRECT;
            }

            Record record = keyValue.findByCode(businessKey);

            if (record != null) {
                Map map = new HashMap();

                for (Prop prop : record.getProps().values()) {
                    map.put(prop.getCode(), prop.getValue());
                }

                json = jsonMapper.toJson(map);
            }

            return "viewStartForm";
        } else {
            // 如果没找到form，就判断是否配置负责人
            return taskConf();
        }
    }

    /**
     * 配置每个任务的参与人.
     */
    public String taskConf() {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        businessKey = new SaveDraftOperation().execute(parameterMap);

        BpmProcess bpmProcess = bpmProcessManager.get(bpmProcessId);
        processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();

        if (Integer.valueOf(1).equals(bpmProcess.getUseTaskConf())) {
            // 如果需要配置负责人
            nextStep = "confirmStartProcess";

            FindTaskDefinitionsCmd cmd = new FindTaskDefinitionsCmd(
                    processDefinitionId);
            taskDefinitions = processEngine.getManagementService()
                    .executeCommand(cmd);

            return "taskConf";
        } else {
            // 如果不需要配置负责人，就进入确认发起流程的页面
            return confirmStartProcess();
        }
    }

    public String confirmStartProcess() {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        businessKey = new ConfAssigneeOperation().execute(parameterMap);
        nextStep = "startProcessInstance";

        return "confirmStartProcess";
    }

    /**
     * 发起流程.
     */
    public String startProcessInstance() throws Exception {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();

        new StartProcessOperation().execute(parameterMap);

        return "startProcessInstance";
    }

    /**
     * 工具方法，获取表单的类型.
     */
    private String getFormType(Map<String, String> formTypeMap, String name) {
        if (formTypeMap.containsKey(name)) {
            return formTypeMap.get(name);
        } else {
            return null;
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
        formInfo = new FormInfo();
        formInfo.setTaskId(taskId);

        List<BpmConfOperation> bpmConfOperations = bpmConfOperationManager
                .find("from BpmConfOperation where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        task.getProcessDefinitionId(),
                        task.getTaskDefinitionKey());

        for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
            formInfo.getButtons().add(bpmConfOperation.getValue());
        }

        if ((formTemplate != null)
                && Integer.valueOf(1).equals(formTemplate.getType())) {
            redirectUrl = formTemplate.getContent() + "?taskId=" + taskId;

            return RELOAD_REDIRECT;
        }

        if ((taskId != null) && (!"".equals(taskId))) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            String processInstanceId = task.getProcessInstanceId();
            Record record = keyValue.findByRef(processInstanceId);

            if (record != null) {
                Map map = new HashMap();

                for (Prop prop : record.getProps().values()) {
                    map.put(prop.getCode(), prop.getValue());
                }

                json = jsonMapper.toJson(map);
            }
        }

        return "viewTaskForm";
    }

    /**
     * 完成任务.
     */
    public String completeTask() throws Exception {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();
        new CompleteTaskOperation().execute(parameterMap);

        return "completeTask";
    }

    // ~ ======================================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
    }

    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
    }

    // ~ ======================================================================
    public String getBusinessKey() {
        return businessKey;
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

    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitionKeys(List<String> taskDefinitionKeys) {
        this.taskDefinitionKeys = taskDefinitionKeys;
    }

    public void setTaskAssignees(List<String> taskAssignees) {
        this.taskAssignees = taskAssignees;
    }

    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getNextStep() {
        return nextStep;
    }

    public long getBpmProcessId() {
        return bpmProcessId;
    }

    public void setBpmProcessId(long bpmProcessId) {
        this.bpmProcessId = bpmProcessId;
    }
}
