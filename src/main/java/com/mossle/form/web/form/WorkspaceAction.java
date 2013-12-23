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

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.manager.FormTemplateManager;

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
 * workspace action.
 * 
 * @author Lingo
 */
public class WorkspaceAction extends BaseAction {
    private static Logger logger = LoggerFactory
            .getLogger(WorkspaceAction.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private ProcessEngine processEngine;
    private BpmTaskConfManager bpmTaskConfManager;
    private String businessKey;
    private String processDefinitionId;
    private String taskId;
    private Long id;
    private FormTemplateManager formTemplateManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private String json;
    private FormInfo formInfo;
    private FormTemplate formTemplate;
    private List<TaskDefinition> taskDefinitions;
    private List<String> taskDefinitionKeys;
    private List<String> taskAssignees;
    private KeyValue keyValue;
    private List<Record> records;

    public String loadForm() throws Exception {
        FormTemplate theFormTemplate = formTemplateManager.get(id);
        json = jsonMapper.toJson(theFormTemplate);

        return "loadForm";
    }

    public String saveDraft() throws Exception {
        Map<String, String[]> parameterMap = ServletActionContext.getRequest()
                .getParameterMap();

        if ((taskId != null) && (!"".equals(taskId))) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            Task task = processEngine.getTaskService().createTaskQuery()
                    .taskId(taskId).singleResult();
            String processInstanceId = task.getProcessInstanceId();
            Record record = keyValue.findByRef(processInstanceId);

            record = new RecordBuilder().build(record, STATUS_DRAFT_TASK,
                    parameterMap);
            keyValue.save(record);
            businessKey = record.getCode();
        } else if ((businessKey != null) && (!"".equals(businessKey))) {
            // 如果是流程草稿，直接通过businessKey获得record，更新数据
            Record record = keyValue.findByCode(businessKey);

            record = new RecordBuilder().build(record, STATUS_DRAFT_PROCESS,
                    parameterMap);
            keyValue.save(record);
        } else {
            // 如果是第一次保存草稿，肯定是流程草稿，先初始化record，再保存数据
            Record record = new RecordBuilder().build(processDefinitionId,
                    STATUS_DRAFT_PROCESS, parameterMap);
            keyValue.save(record);
            businessKey = record.getCode();
        }

        return "saveDraft";
    }

    public String listDrafts() throws Exception {
        records = keyValue.findByStatus(STATUS_DRAFT_PROCESS);

        return "listDrafts";
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

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
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

    // ~ ==================================================
    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    public List<Record> getRecords() {
        return records;
    }
}
