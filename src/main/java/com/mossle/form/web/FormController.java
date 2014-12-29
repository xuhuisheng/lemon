package com.mossle.form.web;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;
import com.mossle.api.internal.StoreConnector;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.MultipartHandler;
import com.mossle.ext.auth.CurrentUserHolder;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.manager.FormTemplateManager;
import com.mossle.form.service.FormService;
import com.mossle.form.support.FormParameter;
import com.mossle.form.xform.Xform;
import com.mossle.form.xform.XformBuilder;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
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
    private MultipartResolver multipartResolver;
    private StoreConnector storeConnector;

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
    public FormDTO findTaskForm(HumanTaskDTO humanTaskDto) {
        FormDTO formDto = humanTaskConnector.findTaskForm(humanTaskDto.getId());

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

    /**
     * 确认发起流程.
     */
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
    public String startProcessInstance(HttpServletRequest request, Model model)
            throws Exception {
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        Record record = null;
        String businessKey = null;
        FormParameter formParameter = null;

        try {
            multipartHandler.handle(request);
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            formParameter = new FormParameter(
                    multipartHandler.getMultiValueMap());
            businessKey = formService.saveDraft(currentUserHolder.getUserId(),
                    formParameter);
            record = keyValue.findByCode(businessKey);

            record = new RecordBuilder().build(record, multipartHandler,
                    storeConnector);

            keyValue.save(record);
        } finally {
            multipartHandler.clear();
        }

        humanTaskConnector.configTaskDefinitions(businessKey,
                formParameter.getList("taskDefinitionKeys"),
                formParameter.getList("taskAssignees"));

        ProcessDTO processDto = processConnector.findProcess(formParameter
                .getBpmProcessId());
        String processDefinitionId = processDto.getProcessDefinitionId();

        // 获得form的信息
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                formDto.getCode());
        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formTemplate.getContent()).setRecord(record)
                .build();
        Map<String, Object> processParameters = xform.getMapData();
        logger.info("processParameters : {}", processParameters);

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
    public String viewTaskForm(@RequestParam("taskId") String humanTaskId,
            Model model, RedirectAttributes redirectAttributes)
            throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            messageHelper.addFlashMessage(redirectAttributes, "任务不存在");

            return "redirect:/bpm/workspace-listPersonalTasks.do";
        }

        FormDTO formDto = this.findTaskForm(humanTaskDto);

        if (formDto.isRedirect()) {
            String redirectUrl = formDto.getUrl() + "?taskId="
                    + formDto.getTaskId();

            return "redirect:" + redirectUrl;
        }

        model.addAttribute("formDto", formDto);

        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                formDto.getCode());
        model.addAttribute("formTemplate", formTemplate);

        if ((humanTaskId != null) && (!"".equals(humanTaskId))) {
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
    public String completeTask(HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws Exception {
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        Record record = null;
        String taskId = null;
        FormParameter formParameter = null;
        HumanTaskDTO humanTaskDto = null;
        FormDTO formDto = null;

        try {
            multipartHandler.handle(request);
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            formParameter = new FormParameter(
                    multipartHandler.getMultiValueMap());

            taskId = formParameter.getTaskId();
            formService.saveDraft(currentUserHolder.getUserId(), formParameter);

            formDto = humanTaskConnector.findTaskForm(taskId);

            humanTaskDto = humanTaskConnector.findHumanTask(taskId);

            String processInstanceId = humanTaskDto.getProcessInstanceId();
            record = keyValue.findByRef(processInstanceId);

            record = new RecordBuilder().build(record, multipartHandler,
                    storeConnector);

            keyValue.save(record);
        } finally {
            multipartHandler.clear();
        }

        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                formDto.getCode());
        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formTemplate.getContent()).setRecord(record)
                .build();
        Map<String, Object> taskParameters = xform.getMapData();
        logger.info("taskParameters : {}", taskParameters);

        try {
            humanTaskConnector.completeTask(taskId,
                    currentUserHolder.getUserId(), taskParameters);
        } catch (IllegalStateException ex) {
            logger.error(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return "redirect:/bpm/workspace-listPersonalTasks.do";
        }

        if (record == null) {
            record = new Record();
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

    @Resource
    public void setMultipartResolver(MultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
