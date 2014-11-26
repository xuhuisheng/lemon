package com.mossle.form.web;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.auth.CurrentUserHolder;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.manager.FormTemplateManager;
import com.mossle.form.service.FormService;
import com.mossle.form.support.FormParameter;

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
    private FormTemplateManager formTemplateManager;
    private JsonMapper jsonMapper = new JsonMapper();
    private KeyValue keyValue;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private FormService formService;
    private ProcessConnector processConnector;
    private HumanTaskConnector humanTaskConnector;

    /**
     * 保存草稿.
     */
    @RequestMapping("form-saveDraft")
    public String saveDraft(
            @RequestParam MultiValueMap<String, String> multiValueMap)
            throws Exception {
        FormParameter formParameter = new FormParameter(multiValueMap);

        formService.saveDraft(currentUserHolder.getUserId(), formParameter);

        return "form/form-saveDraft";
    }

    /**
     * 列出所有草稿.
     */
    @RequestMapping("form-listDrafts")
    public String listDrafts(Model model) throws Exception {
        String userId = currentUserHolder.getUserId();
        List<Record> records = keyValue.findByStatus(STATUS_DRAFT_PROCESS,
                userId);
        model.addAttribute("records", records);

        return "form/form-listDrafts";
    }

    /**
     * 根据流程定义获得formDto.
     */
    public FormDTO findStartForm(String processDefinitionId) {
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        return formDto;
    }

    /**
     * 根据taskId获得formDto.
     */
    public FormDTO findTaskForm(String taskId) {
        FormDTO formDto = humanTaskConnector.findTaskForm(taskId);

        return formDto;
    }

    /**
     * 读取草稿箱中的表单数据，转换成json.
     */
    public String findStartFormData(String businessKey) throws Exception {
        Record record = keyValue.findByCode(businessKey);

        if (record == null) {
            return null;
        }

        Map map = new HashMap();

        for (Prop prop : record.getProps().values()) {
            map.put(prop.getCode(), prop.getValue());
        }

        String json = jsonMapper.toJson(map);

        return json;
    }

    /**
     * 读取任务对应的表单数据，转换成json.
     */
    public String findTaskFormData(String processInstanceId) throws Exception {
        Record record = keyValue.findByRef(processInstanceId);

        if (record == null) {
            return null;
        }

        Map map = new HashMap();

        for (Prop prop : record.getProps().values()) {
            map.put(prop.getCode(), prop.getValue());
        }

        String json = jsonMapper.toJson(map);

        return json;
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

        ProcessDTO processDto = processConnector.findProcess(Long
                .toString(bpmProcessId));

        String processDefinitionId = processDto.getProcessDefinitionId();

        FormDTO formDto = this.findStartForm(processDefinitionId);
        model.addAttribute("formDto", formDto);

        String nextStep = null;

        if (formDto.isExists()) {
            // 如果找到了form，就显示表单
            if (processDto.isConfigTask()) {
                // 如果需要配置负责人
                nextStep = "taskConf";
            } else {
                nextStep = "confirmStartProcess";
            }

            model.addAttribute("nextStep", nextStep);

            if (formDto.isRedirect()) {
                String redirectUrl = formDto.getUrl() + "?processDefinitionId="
                        + formDto.getProcessDefinitionId();

                return "redirect:" + redirectUrl;
            }

            FormTemplate formTemplate = formTemplateManager.findUniqueBy(
                    "code", formDto.getCode());

            model.addAttribute("formTemplate", formTemplate);

            String json = this.findStartFormData(businessKey);

            if (json != null) {
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

        FormParameter formParameter = new FormParameter(multiValueMap);

        businessKey = formService.saveDraft(currentUserHolder.getUserId(),
                formParameter);
        model.addAttribute("businessKey", businessKey);

        ProcessDTO processDto = processConnector.findProcess(Long
                .toString(bpmProcessId));
        String processDefinitionId = processDto.getProcessDefinitionId();

        if (processDto.isConfigTask()) {
            // 如果需要配置负责人
            nextStep = "confirmStartProcess";
            model.addAttribute("nextStep", nextStep);

            List<HumanTaskDefinition> humanTaskDefinitions = humanTaskConnector
                    .findHumanTaskDefinitions(processDefinitionId);
            model.addAttribute("humanTaskDefinitions", humanTaskDefinitions);

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
        FormParameter formParameter = new FormParameter(multiValueMap);

        String businessKey = formService.saveDraft(
                currentUserHolder.getUserId(), formParameter);
        humanTaskConnector.configTaskDefinitions(businessKey,
                formParameter.getList("taskDefinitionKeys"),
                formParameter.getList("taskAssignees"));

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
        FormParameter formParameter = new FormParameter(multiValueMap);
        String businessKey = formService.saveDraft(
                currentUserHolder.getUserId(), formParameter);
        humanTaskConnector.configTaskDefinitions(businessKey,
                formParameter.getList("taskDefinitionKeys"),
                formParameter.getList("taskAssignees"));

        ProcessDTO processDto = processConnector.findProcess(formParameter
                .getBpmProcessId());
        String processDefinitionId = processDto.getProcessDefinitionId();

        // 获得form的信息
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        // 尝试根据表单里字段的类型，进行转换
        Map<String, String> formTypeMap = new HashMap<String, String>();

        if (formDto.isExists()) {
            FormTemplate formTemplate = formTemplateManager.findUniqueBy(
                    "code", formDto.getCode());

            String content = formTemplate.getContent();
            formTypeMap = this.fetchFormTypeMap(content);
        }

        Record record = keyValue.findByCode(businessKey);

        Map<String, Object> processParameters = new HashMap<String, Object>();

        // 如果有表单，就从数据库获取数据
        for (Prop prop : record.getProps().values()) {
            String key = prop.getCode();
            String value = prop.getValue();
            String formType = this.getFormType(formTypeMap, key);

            if ("userpicker".equals(formType)) {
                processParameters.put(key,
                        new ArrayList(Arrays.asList(value.split(","))));
            } else if (formType != null) {
                processParameters.put(key, value);
            }
        }

        String processInstanceId = processConnector.startProcess(
                currentUserHolder.getUserId(), businessKey,
                processDefinitionId, processParameters);

        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstanceId);
        keyValue.save(record);

        return "form/form-startProcessInstance";
    }

    /**
     * 显示任务表单.
     */
    @RequestMapping("form-viewTaskForm")
    public String viewTaskForm(@RequestParam("taskId") String taskId,
            Model model, RedirectAttributes redirectAttributes)
            throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector.findHumanTask(taskId);

        if (humanTaskDto == null) {
            messageHelper.addFlashMessage(redirectAttributes, "任务不存在");

            return "redirect:/bpm/workspace-listPersonalTasks.do";
        }

        FormDTO formDto = this.findTaskForm(taskId);

        if (formDto.isRedirect()) {
            String redirectUrl = formDto.getUrl() + "?taskId="
                    + formDto.getTaskId();

            return "redirect:" + redirectUrl;
        }

        model.addAttribute("formDto", formDto);

        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                formDto.getCode());
        model.addAttribute("formTemplate", formTemplate);

        if ((taskId != null) && (!"".equals(taskId))) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            String processInstanceId = humanTaskDto.getProcessInstanceId();
            String json = this.findTaskFormData(processInstanceId);

            if (json != null) {
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
        FormParameter formParameter = new FormParameter(multiValueMap);
        String taskId = formParameter.getTaskId();
        formService.saveDraft(
                currentUserHolder.getUserId(), formParameter);

        FormDTO formDto = humanTaskConnector.findTaskForm(taskId);

        /*
         * FormService formService = processEngine.getFormService(); String taskFormKey = formService.getTaskFormKey(
         * task.getProcessDefinitionId(), task.getTaskDefinitionKey()); FormInfo formInfo = new FormInfo();
         * formInfo.setTaskId(taskId); formInfo.setFormKey(taskFormKey);
         */

        // 尝试根据表单里字段的类型，进行转换
        Map<String, String> formTypeMap = new HashMap<String, String>();

        if (formDto.isExists()) {
            FormTemplate formTemplate = formTemplateManager.findUniqueBy(
                    "code", formDto.getCode());

            String content = formTemplate.getContent();
            formTypeMap = this.fetchFormTypeMap(content);
        }

        HumanTaskDTO humanTaskDto = humanTaskConnector.findHumanTask(taskId);

        String processInstanceId = humanTaskDto.getProcessInstanceId();
        Record record = keyValue.findByRef(processInstanceId);
        Map<String, Object> taskParameters = new HashMap<String, Object>();

        if (record != null) {
            // 如果有表单，就从数据库获取数据
            for (Prop prop : record.getProps().values()) {
                String key = prop.getCode();
                String value = prop.getValue();
                String formType = this.getFormType(formTypeMap, key);

                if ("userpicker".equals(formType)) {
                    taskParameters.put(key,
                            new ArrayList(Arrays.asList(value.split(","))));
                } else if (formType != null) {
                    taskParameters.put(key, value);
                }
            }
        }

        try {
            humanTaskConnector.completeTask(taskId,
                    currentUserHolder.getUserId(), taskParameters);
        } catch (IllegalStateException ex) {
            logger.error(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return "redirect:/bpm/workspace-listPersonalTasks.do";
        }

        record = new RecordBuilder().build(record, STATUS_RUNNING,
                humanTaskDto.getProcessInstanceId());
        keyValue.save(record);

        return "form/form-completeTask";
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

    public Map<String, String> fetchFormTypeMap(String content) {
        logger.debug("content : {}", content);

        try {
            Map map = jsonMapper.fromJson(content, Map.class);
            logger.debug("map : {}", map);

            List<Map> sections = (List<Map>) map.get("sections");
            logger.debug("sections : {}", sections);

            Map<String, String> formTypeMap = new HashMap<String, String>();

            for (Map section : sections) {
                if (!"grid".equals(section.get("type"))) {
                    continue;
                }

                List<Map> fields = (List<Map>) section.get("fields");

                for (Map field : fields) {
                    formTypeMap.put((String) field.get("name"),
                            (String) field.get("type"));
                }
            }

            return formTypeMap;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);

            return Collections.emptyMap();
        }
    }

    // ~ ======================================================================
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setFormService(FormService formService) {
        this.formService = formService;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
