package com.mossle.operation.web;

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

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;
import com.mossle.api.internal.StoreConnector;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;

import com.mossle.button.ButtonDTO;
import com.mossle.button.ButtonHelper;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.MultipartHandler;
import com.mossle.ext.auth.CurrentUserHolder;

import com.mossle.keyvalue.FormParameter;
import com.mossle.keyvalue.KeyValue;
import com.mossle.keyvalue.Prop;
import com.mossle.keyvalue.Record;
import com.mossle.keyvalue.RecordBuilder;

import com.mossle.operation.service.OperationService;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

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
 * 流程操作.
 * 
 * @author Lingo
 */
@Controller
@RequestMapping("operation")
public class ProcessOperationController {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessOperationController.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private OperationService operationService;
    private KeyValue keyValue;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private ProcessConnector processConnector;
    private HumanTaskConnector humanTaskConnector;
    private MultipartResolver multipartResolver;
    private StoreConnector storeConnector;
    private ButtonHelper buttonHelper = new ButtonHelper();
    private FormConnector formConnector;
    private JsonMapper jsonMapper = new JsonMapper();

    /**
     * 保存草稿.
     */
    @RequestMapping("process-operation-saveDraft")
    public String saveDraft(HttpServletRequest request) throws Exception {
        this.doSaveRecord(request);

        return "operation/process-operation-saveDraft";
    }

    /**
     * 列出所有草稿.
     */
    @RequestMapping("process-operation-listDrafts")
    public String listDrafts(Model model) throws Exception {
        String userId = currentUserHolder.getUserId();
        List<Record> records = keyValue.findByStatus(STATUS_DRAFT_PROCESS,
                userId);
        model.addAttribute("records", records);

        return "operation/process-operation-listDrafts";
    }

    /**
     * 显示启动流程的表单.
     */
    @RequestMapping("process-operation-viewStartForm")
    public String viewStartForm(
            HttpServletRequest request,
            @RequestParam("bpmProcessId") String bpmProcessId,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            Model model) throws Exception {
        FormParameter formParameter = new FormParameter();
        formParameter.setBpmProcessId(bpmProcessId);
        formParameter.setBusinessKey(businessKey);

        ProcessDTO processDto = processConnector.findProcess(bpmProcessId);

        String processDefinitionId = processDto.getProcessDefinitionId();

        FormDTO formDto = this.findStartForm(processDefinitionId);
        formParameter.setFormDto(formDto);

        if (formDto.isExists()) {
            if (formDto.isRedirect()) {
                // 如果是外部表单，就直接跳转出去
                String redirectUrl = formDto.getUrl() + "?processDefinitionId="
                        + formDto.getProcessDefinitionId();

                return "redirect:" + redirectUrl;
            }

            // 如果找到了form，就显示表单
            if (processDto.isConfigTask()) {
                // 如果需要配置负责人
                formParameter.setNextStep("taskConf");
            } else {
                formParameter.setNextStep("confirmStartProcess");
            }

            return this.doViewStartForm(formParameter, model);
        } else if (processDto.isConfigTask()) {
            formParameter.setProcessDefinitionId(processDefinitionId);

            // 如果没找到form，就判断是否配置负责人
            return this.doTaskConf(formParameter, model);
        } else {
            // 如果也不需要配置任务，就直接进入确认发起流程
            return this.doConfirmStartProcess(formParameter, model);
        }
    }

    /**
     * 配置每个任务的参与人.
     * 
     * 如果是执行taskConf，可能是填写表单，也可能是直接进入taskConf。
     */
    @RequestMapping("process-operation-taskConf")
    public String taskConf(HttpServletRequest request, Model model)
            throws Exception {
        FormParameter formParameter = this.doSaveRecord(request);

        ProcessDTO processDto = processConnector.findProcess(formParameter
                .getBpmProcessId());
        String processDefinitionId = processDto.getProcessDefinitionId();
        formParameter.setProcessDefinitionId(processDefinitionId);

        if (processDto.isConfigTask()) {
            // 如果需要配置负责人
            formParameter.setNextStep("confirmStartProcess");

            return this.doTaskConf(formParameter, model);
        } else {
            // 如果不需要配置负责人，就进入确认发起流程的页面
            return this.doConfirmStartProcess(formParameter, model);
        }
    }

    /**
     * 确认发起流程.
     */
    @RequestMapping("process-operation-confirmStartProcess")
    public String confirmStartProcess(HttpServletRequest request, Model model)
            throws Exception {
        FormParameter formParameter = this.doSaveRecord(request);
        formParameter.setNextStep("startProcessInstance");
        this.doConfirmStartProcess(formParameter, model);

        return "operation/process-operation-confirmStartProcess";
    }

    /**
     * 发起流程.
     */
    @RequestMapping("process-operation-startProcessInstance")
    public String startProcessInstance(HttpServletRequest request, Model model)
            throws Exception {
        FormParameter formParameter = this.doSaveRecord(request);
        doConfirmStartProcess(formParameter, model);

        Record record = keyValue.findByCode(formParameter.getBusinessKey());
        ProcessDTO processDto = processConnector.findProcess(formParameter
                .getBpmProcessId());
        String processDefinitionId = processDto.getProcessDefinitionId();

        // 获得form的信息
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formDto.getContent()).setRecord(record).build();
        Map<String, Object> processParameters = xform.getMapData();
        logger.info("processParameters : {}", processParameters);

        String processInstanceId = processConnector.startProcess(
                currentUserHolder.getUserId(), formParameter.getBusinessKey(),
                processDefinitionId, processParameters);

        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstanceId);
        keyValue.save(record);

        return "operation/process-operation-startProcessInstance";
    }

    // ~ ======================================================================
    /**
     * 通过multipart请求构建formParameter.
     */
    public FormParameter buildFormParameter(MultipartHandler multipartHandler) {
        FormParameter formParameter = new FormParameter();
        formParameter.setMultiValueMap(multipartHandler.getMultiValueMap());
        formParameter.setMultiFileMap(multipartHandler.getMultiFileMap());
        formParameter.setBusinessKey(multipartHandler.getMultiValueMap()
                .getFirst("businessKey"));
        formParameter.setBpmProcessId(multipartHandler.getMultiValueMap()
                .getFirst("bpmProcessId"));
        formParameter.setHumanTaskId(multipartHandler.getMultiValueMap()
                .getFirst("humanTaskId"));

        return formParameter;
    }

    /**
     * 把数据先保存到keyvalue里.
     */
    public FormParameter doSaveRecord(HttpServletRequest request)
            throws Exception {
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        FormParameter formParameter = null;

        try {
            multipartHandler.handle(request);
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            formParameter = this.buildFormParameter(multipartHandler);

            String businessKey = operationService.saveDraft(
                    currentUserHolder.getUserId(), formParameter);

            if ((formParameter.getBusinessKey() == null)
                    || "".equals(formParameter.getBusinessKey().trim())) {
                formParameter.setBusinessKey(businessKey);
            }

            Record record = keyValue.findByCode(businessKey);

            record = new RecordBuilder().build(record, multipartHandler,
                    storeConnector);

            keyValue.save(record);
        } finally {
            multipartHandler.clear();
        }

        return formParameter;
    }

    /**
     * 实际确认发起流程.
     */
    public String doConfirmStartProcess(FormParameter formParameter, Model model) {
        humanTaskConnector.configTaskDefinitions(
                formParameter.getBusinessKey(),
                formParameter.getList("taskDefinitionKeys"),
                formParameter.getList("taskAssignees"));

        model.addAttribute("businessKey", formParameter.getBusinessKey());
        model.addAttribute("nextStep", formParameter.getNextStep());
        model.addAttribute("bpmProcessId", formParameter.getBpmProcessId());

        return "operation/process-operation-confirmStartProcess";
    }

    /**
     * 实际显示开始表单.
     */
    public String doViewStartForm(FormParameter formParameter, Model model)
            throws Exception {
        model.addAttribute("formDto", formParameter.getFormDto());
        model.addAttribute("bpmProcessId", formParameter.getBpmProcessId());
        model.addAttribute("businessKey", formParameter.getBusinessKey());
        model.addAttribute("nextStep", formParameter.getNextStep());

        List<ButtonDTO> buttons = new ArrayList<ButtonDTO>();
        buttons.add(buttonHelper.findButton("saveDraft"));
        buttons.add(buttonHelper.findButton(formParameter.getNextStep()));
        model.addAttribute("buttons", buttons);

        model.addAttribute("formDto", formParameter.getFormDto());

        String json = this.findStartFormData(formParameter.getBusinessKey());

        if (json != null) {
            model.addAttribute("json", json);
        }

        Record record = keyValue.findByCode(formParameter.getBusinessKey());
        FormDTO formDto = formConnector.findForm(formParameter.getFormDto()
                .getCode());

        if (record != null) {
            Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                    .setContent(formDto.getContent()).setRecord(record).build();
            model.addAttribute("xform", xform);
        } else {
            Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                    .setContent(formDto.getContent()).build();
            model.addAttribute("xform", xform);
        }

        return "operation/process-operation-viewStartForm";
    }

    /**
     * 实际展示配置任务的配置.
     */
    public String doTaskConf(FormParameter formParameter, Model model) {
        model.addAttribute("bpmProcessId", formParameter.getBpmProcessId());

        model.addAttribute("businessKey", formParameter.getBusinessKey());
        model.addAttribute("nextStep", formParameter.getNextStep());

        List<HumanTaskDefinition> humanTaskDefinitions = humanTaskConnector
                .findHumanTaskDefinitions(formParameter
                        .getProcessDefinitionId());
        model.addAttribute("humanTaskDefinitions", humanTaskDefinitions);

        return "operation/process-operation-taskConf";
    }

    /**
     * 根据流程定义获得formDto.
     */
    public FormDTO findStartForm(String processDefinitionId) {
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

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

    // ~ ======================================================================
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
    public void setOperationService(OperationService operationService) {
        this.operationService = operationService;
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

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }
}
