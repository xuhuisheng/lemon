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
 * 电子表单与流程集成的地方.
 * 
 * @author Lingo
 */
public class FormAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(FormAction.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmTaskConfManager bpmTaskConfManager;
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

    /**
     * 根据id显示表单模板，把表单模板生成json，返回到页面显示.
     * 
     * @todo: 放到rest里？
     */
    public String loadForm() throws Exception {
        FormTemplate theFormTemplate = formTemplateManager.get(id);
        json = jsonMapper.toJson(theFormTemplate);

        return "loadForm";
    }

    /**
     * 保存草稿.
     */
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
        // 先保存草稿
        this.saveDraft();

        if ((!"taskConf".equals(status)) && "taskConf".equals(taskConf())) {
            return "taskConf";
        }

        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());
        // 获得form的信息
        formInfo = processEngine.getManagementService().executeCommand(
                new FindStartFormCmd(processDefinitionId));

        // 尝试根据表单里字段的类型，进行转换
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

        Record record = keyValue.findByCode(businessKey);

        Map<String, Object> processParameters = new HashMap<String, Object>();

        // 如果有表单，就从数据库获取数据
        for (Prop prop : record.getProps().values()) {
            String key = prop.getCode();
            String value = prop.getValue();
            String formType = this.getFormType(formTypeMap, key);

            if ("userPicker".equals(formType)) {
                processParameters.put(key,
                        new ArrayList(Arrays.asList(value.split(","))));
            } else {
                processParameters.put(key, value);
            }
        }

        if (taskDefinitionKeys != null) {
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
        }

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId, businessKey,
                        processParameters);
        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstance.getId());
        keyValue.save(record);

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
        formInfo = new FormInfo();
        formInfo.setTaskId(taskId);

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
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());
        this.saveDraft();

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        FormService formService = processEngine.getFormService();
        String taskFormKey = formService.getTaskFormKey(
                task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        formInfo = new FormInfo();
        formInfo.setTaskId(taskId);
        formInfo.setFormKey(taskFormKey);

        // 尝试根据表单里字段的类型，进行转换
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

        String processInstanceId = task.getProcessInstanceId();
        Record record = keyValue.findByRef(processInstanceId);

        Map<String, Object> processParameters = new HashMap<String, Object>();

        // 如果有表单，就从数据库获取数据
        for (Prop prop : record.getProps().values()) {
            String key = prop.getCode();
            String value = prop.getValue();
            String formType = this.getFormType(formTypeMap, key);

            if ("userPicker".equals(formType)) {
                processParameters.put(key,
                        new ArrayList(Arrays.asList(value.split(","))));
            } else {
                processParameters.put(key, value);
            }
        }

        processEngine.getManagementService()
                .executeCommand(
                        new CompleteTaskWithCommentCmd(taskId,
                                processParameters, "完成"));
        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstanceId);
        keyValue.save(record);

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
}
