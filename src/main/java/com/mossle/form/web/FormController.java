package com.mossle.form.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.FormInfo;
import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.FindStartFormCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 电子表单与流程集成的地方.
 * 
 * @author Lingo
 */
@Controller
@RequestMapping("form")
public class FormController {
    private static Logger logger = LoggerFactory
            .getLogger(FormController.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfFormManager bpmConfFormManager;
    private FormTemplateManager formTemplateManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private KeyValue keyValue;
    private MessageHelper messageHelper;

    /**
     * 保存草稿.
     */
    @RequestMapping("form-saveDraft")
    public String saveDraft(
            @RequestParam MultiValueMap<String, String> multiValueMap)
            throws Exception {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            parameterMap.put(entry.getKey(),
                    entry.getValue().toArray(new String[0]));
        }

        new SaveDraftOperation().execute(parameterMap);

        return "form/form-saveDraft";
    }

    /**
     * 列出所有草稿.
     */
    @RequestMapping("form-listDrafts")
    public String listDrafts(Model model) throws Exception {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<Record> records = keyValue.findByStatus(STATUS_DRAFT_PROCESS,
                userId);
        model.addAttribute("records", records);

        return "form/form-listDrafts";
    }

    /**
     * 显示启动流程的表单.
     */
    @RequestMapping("form-viewStartForm")
    public String viewStartForm(
            @RequestParam("bpmProcessId") Long bpmProcessId,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            Model model) throws Exception {
        model.addAttribute("bpmProcessId", bpmProcessId);
        model.addAttribute("businessKey", businessKey);

        BpmProcess bpmProcess = bpmProcessManager.get(bpmProcessId);
        String processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();

        FormInfo formInfo = processEngine.getManagementService()
                .executeCommand(new FindStartFormCmd(processDefinitionId));
        model.addAttribute("formInfo", formInfo);

        String nextStep = null;

        if (formInfo.isFormExists()) {
            // 如果找到了form，就显示表单
            if (Integer.valueOf(1).equals(bpmProcess.getUseTaskConf())) {
                // 如果需要配置负责人
                nextStep = "taskConf";
            } else {
                nextStep = "confirmStartProcess";
            }

            model.addAttribute("nextStep", nextStep);

            List<BpmConfForm> bpmConfForms = bpmConfFormManager
                    .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                            formInfo.getProcessDefinitionId(),
                            formInfo.getActivityId());

            if (!bpmConfForms.isEmpty()) {
                if (Integer.valueOf(1).equals(bpmConfForms.get(0).getType())) {
                    String redirectUrl = bpmConfForms.get(0).getValue()
                            + "?processDefinitionId="
                            + formInfo.getProcessDefinitionId();

                    return "redirect:" + redirectUrl;
                }
            }

            FormTemplate formTemplate = formTemplateManager.get(Long
                    .parseLong(formInfo.getFormKey()));

            if (Integer.valueOf(1).equals(formTemplate.getType())) {
                String redirectUrl = formTemplate.getContent()
                        + "?processDefinitionId="
                        + formInfo.getProcessDefinitionId();
                ;

                return "redirect:" + redirectUrl;
            }

            model.addAttribute("formTemplate", formTemplate);

            Record record = keyValue.findByCode(businessKey);

            if (record != null) {
                Map map = new HashMap();

                for (Prop prop : record.getProps().values()) {
                    map.put(prop.getCode(), prop.getValue());
                }

                String json = jsonMapper.toJson(map);
                model.addAttribute("json", json);
            }

            return "form/form-viewStartForm";
        } else {
            // 如果没找到form，就判断是否配置负责人
            return taskConf(new LinkedMultiValueMap(), bpmProcessId,
                    businessKey, nextStep, model);
        }
    }

    /**
     * 配置每个任务的参与人.
     */
    @RequestMapping("form-taskConf")
    public String taskConf(
            @RequestParam MultiValueMap<String, String> multiValueMap,
            @RequestParam("bpmProcessId") Long bpmProcessId,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestParam(value = "nextStep", required = false) String nextStep,
            Model model) {
        model.addAttribute("bpmProcessId", bpmProcessId);

        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            parameterMap.put(entry.getKey(),
                    entry.getValue().toArray(new String[0]));
        }

        businessKey = new SaveDraftOperation().execute(parameterMap);
        model.addAttribute("businessKey", businessKey);

        BpmProcess bpmProcess = bpmProcessManager.get(bpmProcessId);
        String processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();

        if (Integer.valueOf(1).equals(bpmProcess.getUseTaskConf())) {
            // 如果需要配置负责人
            nextStep = "confirmStartProcess";
            model.addAttribute("nextStep", nextStep);

            FindTaskDefinitionsCmd cmd = new FindTaskDefinitionsCmd(
                    processDefinitionId);
            List<TaskDefinition> taskDefinitions = processEngine
                    .getManagementService().executeCommand(cmd);
            model.addAttribute("taskDefinitions", taskDefinitions);

            return "form/form-taskConf";
        } else {
            // 如果不需要配置负责人，就进入确认发起流程的页面
            return confirmStartProcess(bpmProcessId, multiValueMap, model);
        }
    }

    @RequestMapping("form-confirmStartProcess")
    public String confirmStartProcess(
            @RequestParam("bpmProcessId") Long bpmProcessId,
            @RequestParam MultiValueMap<String, String> multiValueMap,
            Model model) {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            parameterMap.put(entry.getKey(),
                    entry.getValue().toArray(new String[0]));
        }

        String businessKey = new ConfAssigneeOperation().execute(parameterMap);
        String nextStep = "startProcessInstance";
        model.addAttribute("businessKey", businessKey);
        model.addAttribute("nextStep", nextStep);
        model.addAttribute("bpmProcessId", bpmProcessId);

        return "form/form-confirmStartProcess";
    }

    /**
     * 发起流程.
     */
    @RequestMapping("form-startProcessInstance")
    public String startProcessInstance(
            @RequestParam MultiValueMap<String, String> multiValueMap,
            Model model) throws Exception {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            parameterMap.put(entry.getKey(),
                    entry.getValue().toArray(new String[0]));
        }

        new StartProcessOperation().execute(parameterMap);

        return "form/form-startProcessInstance";
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
    @RequestMapping("form-viewTaskForm")
    public String viewTaskForm(@RequestParam("taskId") String taskId,
            Model model, RedirectAttributes redirectAttributes)
            throws Exception {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            messageHelper.addFlashMessage(redirectAttributes, "任务不存在");

            return "redirect:/bpm/workspace-listPersonalTasks.do";
        }

        FormService formService = processEngine.getFormService();
        String taskFormKey = formService.getTaskFormKey(
                task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        FormTemplate formTemplate = formTemplateManager.get(Long
                .parseLong(taskFormKey));
        model.addAttribute("formTemplate", formTemplate);

        FormInfo formInfo = new FormInfo();
        formInfo.setTaskId(taskId);
        model.addAttribute("formInfo", formInfo);

        List<BpmConfOperation> bpmConfOperations = bpmConfOperationManager
                .find("from BpmConfOperation where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        task.getProcessDefinitionId(),
                        task.getTaskDefinitionKey());

        for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
            formInfo.getButtons().add(bpmConfOperation.getValue());
        }

        String processDefinitionId = task.getProcessDefinitionId();
        String activitiyId = task.getTaskDefinitionKey();
        List<BpmConfForm> bpmConfForms = bpmConfFormManager
                .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, activitiyId);

        if (!bpmConfForms.isEmpty()) {
            if (Integer.valueOf(1).equals(bpmConfForms.get(0).getType())) {
                String redirectUrl = bpmConfForms.get(0).getValue()
                        + "?taskId=" + taskId;

                return "redirect:" + redirectUrl;
            }
        }

        if ((formTemplate != null)
                && Integer.valueOf(1).equals(formTemplate.getType())) {
            String redirectUrl = formTemplate.getContent() + "?taskId="
                    + taskId;

            return "redirect:" + redirectUrl;
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

                String json = jsonMapper.toJson(map);
                model.addAttribute("json", json);
            }
        }

        return "form/form-viewTaskForm";
    }

    /**
     * 完成任务.
     */
    @RequestMapping("form-completeTask")
    public String completeTask(
            @RequestParam MultiValueMap<String, String> multiValueMap,
            RedirectAttributes redirectAttributes) throws Exception {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            parameterMap.put(entry.getKey(),
                    entry.getValue().toArray(new String[0]));
        }

        try {
            new CompleteTaskOperation().execute(parameterMap);
        } catch (IllegalStateException ex) {
            logger.error(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return "redirect:/bpm/workspace-listPersonalTasks.do";
        }

        return "form/form-completeTask";
    }

    // ~ ======================================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
    }

    @Resource
    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
    }

    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }

    @Resource
    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
    }
}
