package com.mossle.operation.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.form.FormMetadata;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.Prop;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.process.ProcessBaseInfo;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.bpm.service.TraceService;

import com.mossle.button.ButtonDTO;
import com.mossle.button.ButtonHelper;

import com.mossle.client.employee.EmployeeClient;
import com.mossle.client.store.StoreClient;

import com.mossle.core.MultipartHandler;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.operation.service.OperationService;
import com.mossle.operation.service.ProcessModelService;
import com.mossle.operation.service.ViewService;
import com.mossle.operation.support.FormData;
import com.mossle.operation.support.FormDataBuilder;

import com.mossle.spi.process.InternalProcessConnector;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartResolver;

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
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private ProcessConnector processConnector;
    private InternalProcessConnector internalProcessConnector;
    private HumanTaskConnector humanTaskConnector;
    private MultipartResolver multipartResolver;
    private StoreClient storeClient;
    private ButtonHelper buttonHelper = new ButtonHelper();
    private FormConnector formConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private TenantHolder tenantHolder;
    private UserConnector userConnector;
    private ModelConnector modelConnector;
    private ProcessModelService processModelService;
    private EmployeeClient employeeClient;
    private ProcessEngine processEngine;
    private TraceService traceService;
    private ViewService viewService;

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
    public String listDrafts(@ModelAttribute Page page, Model model)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        // page = keyValueConnector.pagedQuery(page, STATUS_DRAFT_PROCESS, userId,
        // tenantId);
        page = modelConnector.findDraft(page.getPageNo(), page.getPageSize(),
                userId);
        model.addAttribute("page", page);

        return "operation/process-operation-listDrafts";
    }

    /**
     * 删除草稿.
     */
    @RequestMapping("process-operation-removeDraft")
    public String removeDraft(@RequestParam("code") String code) {
        // keyValueConnector.removeByCode(code);
        modelConnector.removeDraft(code);

        return "redirect:/operation/process-operation-listDrafts.do";
    }

    @RequestMapping("process-operation-viewStartFormByKey")
    public String viewStartFormByKey(@RequestParam("key") String key) {
        String processDefinitionId = this.internalProcessConnector
                .findProcessDefinitionId(key);
        ProcessDTO processDto = this.processConnector
                .findProcessByProcessDefinitionId(processDefinitionId);
        String bpmProcessId = processDto.getId();

        return "redirect:/operation/process-operation-viewStartForm.do?bpmProcessId="
                + bpmProcessId;
    }

    /**
     * 显示启动流程的表单.
     */
    @RequestMapping("process-operation-viewStartForm")
    public String viewStartForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "bpmProcessId", required = false) String bpmProcessId,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            Model model) throws Exception {
        ProcessDTO processDto = null;

        if (StringUtils.isBlank(bpmProcessId)) {
            ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
            processDto = processConnector
                    .findProcessByProcessDefinitionId(modelInfoDto
                            .getProcessId());
            bpmProcessId = processDto.getId();
        } else {
            processDto = processConnector.findProcess(bpmProcessId);
        }

        String tenantId = tenantHolder.getTenantId();
        FormParameter formParameter = new FormParameter();
        formParameter.setBpmProcessId(bpmProcessId);
        formParameter.setBusinessKey(businessKey);

        String processDefinitionId = processDto.getProcessDefinitionId();

        FormDTO formDto = this.processConnector
                .findStartForm(processDefinitionId);
        formParameter.setFormDto(formDto);

        if (formDto.isExists()) {
            if (formDto.isRedirect()) {
                // 如果是外部表单，就直接跳转出去
                // url
                String url = formDto.getUrl();

                // metadata
                FormMetadata formMetadata = new FormMetadata();
                formMetadata.setBpmProcessId(bpmProcessId);
                formMetadata.setBusinessKey(businessKey);

                String userId = currentUserHolder.getUserId();
                EmployeeDTO employeeDto = employeeClient.findById(userId,
                        tenantId);
                formMetadata.setUserId(userId);
                formMetadata.setDisplayName(employeeDto.getName());
                formMetadata.setDepartmentId(employeeDto.getDepartmentCode());
                formMetadata.setDepartmentName(employeeDto.getDepartmentName());

                // data
                ModelInfoDTO modelInfoDto = null;

                if (businessKey != null) {
                    modelInfoDto = modelConnector.findByCode(businessKey);
                }

                this.viewExternalForm(request, response, url, formMetadata,
                        modelInfoDto);

                return null;
            }

            // 如果找到了form，就显示表单
            if (processDto.isConfigTask()) {
                // 如果需要配置负责人
                formParameter.setNextStep("taskConf");
            } else {
                formParameter.setNextStep("confirmStartProcess");
            }

            return this.doViewStartForm(formParameter, model, tenantId);
        } else if (processDto.isConfigTask()) {
            formParameter.setProcessDefinitionId(processDefinitionId);

            // 如果没找到form，就判断是否配置负责人
            return this.doTaskConf(formParameter, model);
        } else {
            // 如果也不需要配置任务，就直接进入确认发起流程
            return this.doConfirmStartProcess(formParameter, model);
        }
    }

    public void viewExternalForm(HttpServletRequest request,
            HttpServletResponse response, String path,
            FormMetadata formMetadata, ModelInfoDTO modelInfoDto)
            throws Exception {
        request.setAttribute("formMetadata", formMetadata);

        if (modelInfoDto != null) {
            FormData formData = new FormDataBuilder().setModelInfoDto(
                    modelInfoDto).build();
            String formDataJson = jsonMapper.toJson(formData);
            request.setAttribute("formData", formData);
            request.setAttribute("formDataJson", formDataJson);
        }

        request.getRequestDispatcher(path).forward(request, response);
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
        String businessKey = formParameter.getBusinessKey();
        this.doConfirmStartProcess(formParameter, model);

        // Record record = keyValueConnector.findByCode(formParameter
        // .getBusinessKey());
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        ProcessDTO processDto = processConnector.findProcess(formParameter
                .getBpmProcessId());
        String processDefinitionId = processDto.getProcessDefinitionId();

        // 获得form的信息
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        Xform xform = this.processModelService.processFormData(businessKey,
                formDto);
        Map<String, Object> processParameters = xform.getMapData();
        logger.info("processParameters : {}", processParameters);

        String userId = currentUserHolder.getUserId();
        this.operationService.startProcessInstance(userId, businessKey,
                processDefinitionId, processParameters, modelInfoDto);

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
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        FormParameter formParameter = null;

        try {
            multipartHandler.handle(request);
            logger.debug("multiValueMap : {}",
                    multipartHandler.getMultiValueMap());
            logger.debug("multiFileMap : {}",
                    multipartHandler.getMultiFileMap());

            formParameter = this.buildFormParameter(multipartHandler);

            String businessKey = operationService.saveDraft(userId, tenantId,
                    formParameter);

            if ((formParameter.getBusinessKey() == null)
                    || "".equals(formParameter.getBusinessKey().trim())) {
                formParameter.setBusinessKey(businessKey);
            }

            // Record record = keyValueConnector.findByCode(businessKey);
            ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);

            // record = new RecordBuilder().build(record, multipartHandler,
            // storeConnector, tenantId);
            // keyValueConnector.save(record);
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
    public String doViewStartForm(FormParameter formParameter, Model model,
            String tenantId) throws Exception {
        model.addAttribute("formDto", formParameter.getFormDto());
        model.addAttribute("bpmProcessId", formParameter.getBpmProcessId());
        model.addAttribute("businessKey", formParameter.getBusinessKey());
        model.addAttribute("nextStep", formParameter.getNextStep());

        List<ButtonDTO> buttons = new ArrayList<ButtonDTO>();
        buttons.add(buttonHelper.findButton("saveDraft"));
        buttons.add(buttonHelper.findButton(formParameter.getNextStep()));
        model.addAttribute("buttons", buttons);

        model.addAttribute("formDto", formParameter.getFormDto());

        String businessKey = formParameter.getBusinessKey();

        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        FormDTO formDto = formConnector.findForm(formParameter.getFormDto()
                .getCode(), tenantId);
        Xform xform = this.processModelService.processFormData(businessKey,
                formDto);
        model.addAttribute("xform", xform);

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

    @RequestMapping("process-operation-view")
    public String view(
            @RequestParam("processInstanceId") String processInstanceId,
            Model model) throws Exception {
        HistoricProcessInstance historicProcessInstance = viewService
                .findHistoricProcessInstance(processInstanceId);
        model.addAttribute("historicProcessInstance", historicProcessInstance);

        // 显示三部分
        // 1. 流程基本信息
        ProcessBaseInfo processBaseInfo = viewService
                .findProcess(processInstanceId);
        model.addAttribute("processBaseInfo", processBaseInfo);

        // 2. 表单
        Map<String, Object> formResultMap = viewService
                .findProcessForm(processInstanceId);
        model.addAttribute("formDto", formResultMap.get("formDto"));
        model.addAttribute("xform", formResultMap.get("xform"));
        model.addAttribute("formMetadata", formResultMap.get("formMetadata"));
        model.addAttribute("formData", formResultMap.get("formData"));
        model.addAttribute("formDataJson", formResultMap.get("formDataJson"));

        // 3. 审批记录
        List<HumanTaskDTO> humanTaskDtos = viewService
                .findHumanTasks(processInstanceId);
        model.addAttribute("humanTaskDtos", humanTaskDtos);

        // 4. 操作按钮
        List<Map<String, String>> buttons = viewService
                .findProcessToolbar(processInstanceId);
        model.addAttribute("buttons", buttons);

        return "operation/process-operation-view";
    }

    // ~ ======================================================================
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
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
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
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setModelConnector(ModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }

    @Resource
    public void setProcessModelService(ProcessModelService processModelService) {
        this.processModelService = processModelService;
    }

    @Resource
    public void setEmployeeClient(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setTraceService(TraceService traceService) {
        this.traceService = traceService;
    }

    @Resource
    public void setViewService(ViewService viewService) {
        this.viewService = viewService;
    }
}
