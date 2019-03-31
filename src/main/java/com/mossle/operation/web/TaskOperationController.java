package com.mossle.operation.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.form.FormDTO;
import com.mossle.api.form.FormMetadata;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.Prop;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.process.ProcessBaseInfo;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.button.ButtonDTO;
import com.mossle.button.ButtonHelper;

import com.mossle.client.employee.EmployeeClient;
import com.mossle.client.store.StoreClient;

import com.mossle.core.MultipartHandler;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.BaseDTO;

import com.mossle.operation.service.OperationService;
import com.mossle.operation.service.ProcessModelService;
import com.mossle.operation.service.ViewService;
import com.mossle.operation.support.FormData;
import com.mossle.operation.support.FormDataBuilder;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 任务工具条.
 * 
 * @author Lingo
 */
@Controller
@RequestMapping("operation")
public class TaskOperationController {
    private static Logger logger = LoggerFactory
            .getLogger(TaskOperationController.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private OperationService operationService;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private ProcessConnector processConnector;
    private HumanTaskConnector humanTaskConnector;
    private MultipartResolver multipartResolver;
    private StoreClient storeClient;
    private ButtonHelper buttonHelper = new ButtonHelper();
    private JsonMapper jsonMapper = new JsonMapper();
    private TenantHolder tenantHolder;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private ModelConnector modelConnector;
    private ProcessModelService processModelService;
    private EmployeeClient employeeClient;
    private ViewService viewService;

    /**
     * 保存草稿.
     */
    @RequestMapping("task-operation-saveDraft")
    public String saveDraft(HttpServletRequest request) throws Exception {
        this.doSaveRecord(request);

        return "operation/task-operation-saveDraft";
    }

    /**
     * 根据businessKey显示任务表单.
     */
    @RequestMapping("task-operation-viewTaskFormByBusinessKey")
    public String viewTaskForm(@RequestParam("businessKey") String businessKey) {
        ModelInfoDTO modelInfoDto = this.modelConnector.findByCode(businessKey);
        List<HumanTaskDTO> humanTaskDtos = this.humanTaskConnector.findHumanTasksByProcessInstanceId(modelInfoDto.getInstanceId());
        HumanTaskDTO humanTaskDto = humanTaskDtos.get(humanTaskDtos.size() - 1);
        return "redirect:/operation/task-operation-viewTaskForm.do?humanTaskId=" + humanTaskDto.getId();
    }

    /**
     * 显示任务表单.
     */
    @RequestMapping("task-operation-viewTaskForm")
    public String viewTaskForm(@RequestParam("humanTaskId") String humanTaskId,
            Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            messageHelper.addFlashMessage(redirectAttributes, "任务不存在");

            return "redirect:/humantask/workspace-listPersonalTasks.do";
        }

        // 处理转发抄送任务，设置为已读
        if (HumanTaskConstants.CATALOG_COPY.equals(humanTaskDto.getCatalog())) {
            humanTaskDto.setStatus("complete");
            humanTaskDto.setAction("read");
            humanTaskConnector.saveHumanTask(humanTaskDto);
        }

        // 表单
        FormDTO formDto = this.findTaskForm(humanTaskDto);

        if (formDto.isRedirect()) {
            String processInstanceId = humanTaskDto.getProcessInstanceId();

            String businessKey = processConnector
                    .findBusinessKeyByProcessInstanceId(processInstanceId);
            String tenantId = tenantHolder.getTenantId();

            // 如果是外部表单，就直接跳转出去
            // url
            String url = formDto.getUrl();

            // metadata
            FormMetadata formMetadata = new FormMetadata();
            formMetadata.setBusinessKey(businessKey);
            formMetadata.setHumanTaskId(humanTaskId);

            String userId = currentUserHolder.getUserId();
            EmployeeDTO employeeDto = employeeClient.findById(userId, tenantId);
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

        model.addAttribute("formDto", formDto);
        model.addAttribute("humanTaskId", humanTaskId);
        model.addAttribute("humanTask", humanTaskDto);

        if (humanTaskDto.getParentId() != null) {
            model.addAttribute("parentHumanTask", humanTaskConnector
                    .findHumanTask(humanTaskDto.getParentId()));
        }

        // 表单和数据
        if ((humanTaskId != null) && (!"".equals(humanTaskId))) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            String processInstanceId = humanTaskDto.getProcessInstanceId();

            String businessKey = processConnector
                    .findBusinessKeyByProcessInstanceId(processInstanceId);
            Xform xform = this.processModelService.processFormData(businessKey,
                    formDto);
            model.addAttribute("xform", xform);
        }

        // 操作
        List<ButtonDTO> buttons = new ArrayList<ButtonDTO>();

        for (String button : formDto.getButtons()) {
            buttons.add(buttonHelper.findButton(button.trim()));
        }

        if (buttons.isEmpty()) {
            buttons.add(buttonHelper.findButton("saveDraft"));
            buttons.add(buttonHelper.findButton("completeTask"));
        }

        model.addAttribute("buttons", buttons);

        // 沟通
        List<HumanTaskDTO> children = humanTaskConnector
                .findSubTasks(humanTaskId);
        model.addAttribute("children", children);

        // 审批记录
        List<HumanTaskDTO> logHumanTaskDtos = humanTaskConnector
                .findHumanTasksByProcessInstanceId(humanTaskDto
                        .getProcessInstanceId());
        model.addAttribute("logHumanTaskDtos", logHumanTaskDtos);

        return "operation/task-operation-viewTaskForm";
    }

    /**
     * 完成任务.
     */
    @RequestMapping("task-operation-completeTask")
    public String completeTask(HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);

        // Record record = null;
        String humanTaskId = null;
        FormParameter formParameter = null;
        HumanTaskDTO humanTaskDto = null;
        FormDTO formDto = null;
        ModelInfoDTO modelInfoDto = null;

        try {
            multipartHandler.handle(request);
            logger.debug("getMultiValueMap : {}",
                    multipartHandler.getMultiValueMap());
            logger.debug("getMultiFileMap : {}",
                    multipartHandler.getMultiFileMap());

            formParameter = this.buildFormParameter(multipartHandler);

            humanTaskId = formParameter.getHumanTaskId();
            operationService.saveDraft(userId, tenantId, formParameter);

            formDto = humanTaskConnector.findTaskForm(humanTaskId);

            humanTaskDto = humanTaskConnector.findHumanTask(humanTaskId);

            String processInstanceId = humanTaskDto.getProcessInstanceId();

            String businessKey = processConnector
                    .findBusinessKeyByProcessInstanceId(processInstanceId);
            modelInfoDto = modelConnector.findByCode(businessKey);
        } finally {
            multipartHandler.clear();
        }

        String businessKey = modelInfoDto.getCode();
        Xform xform = this.processModelService.processFormData(businessKey,
                formDto);
        Map<String, Object> taskParameters = xform.getMapData();
        logger.info("taskParameters : {}", taskParameters);

        try {
            this.operationService.completeTask(humanTaskId, userId,
                    formParameter, taskParameters, modelInfoDto);
        } catch (IllegalStateException ex) {
            logger.error(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return "redirect:/humantask/workspace-personalTasks.do";
        }

        return "operation/task-operation-completeTask";
    }

    /**
     * 领取任务.
     */
    @RequestMapping("task-operation-claimTask")
    public String claimTask(@RequestParam("humanTaskId") String humanTaskId) {
        String userId = currentUserHolder.getUserId();
        humanTaskConnector.claimTask(humanTaskId, userId);

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 释放任务.
     */
    @RequestMapping("task-operation-releaseTask")
    public String releaseTask(@RequestParam("humanTaskId") String humanTaskId) {
        humanTaskConnector.releaseTask(humanTaskId, "");

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 回退任务，前一个任务.
     */
    @RequestMapping("task-operation-rollbackPrevious")
    public String rollbackPrevious(
            @RequestParam("humanTaskId") String humanTaskId) {
        humanTaskConnector.rollbackPrevious(humanTaskId, "");

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 回退任务，开始事件.
     */
    @RequestMapping("task-operation-rollbackStart")
    public String rollbackStart(@RequestParam("humanTaskId") String humanTaskId) {
        humanTaskConnector.rollbackStart(humanTaskId, "");

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 回退任务，发起人.
     */
    @RequestMapping("task-operation-rollbackInitiator")
    public String rollbackInitiator(
            @RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("_humantask_comment_") String comment) {
        humanTaskConnector.rollbackInitiator(humanTaskId, comment);

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 撤销任务.
     */
    @RequestMapping("task-operation-withdraw")
    public String withdraw(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("comment") String comment) {
        humanTaskConnector.withdraw(humanTaskId, comment);

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 转办.
     */
    @RequestMapping("task-operation-transfer")
    public String transfer(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        humanTaskConnector.transfer(humanTaskId, userId, comment);

        return "redirect:/humantask/workspace-delegatedTasks.do";
    }

    /**
     * 取消.
     */
    @RequestMapping("task-operation-cancel")
    public String cancel(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        humanTaskConnector.cancel(humanTaskId, userId, comment);

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 协办.
     */
    @RequestMapping("task-operation-delegateTask")
    public String delegateTask(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        humanTaskConnector.delegateTask(humanTaskId, userId, comment);

        return "redirect:/humantask/workspace-delegatedTasks.do";
    }

    /**
     * 链状协办.
     */
    @RequestMapping("task-operation-delegateTaskCreate")
    public String delegateTaskCreate(
            @RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        humanTaskConnector.delegateTaskCreate(humanTaskId, userId, comment);

        return "redirect:/humantask/workspace-delegatedTasks.do";
    }

    /**
     * 沟通.
     */
    @RequestMapping("task-operation-communicate")
    public String communicate(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        logger.info(
                "communicate : humanTaskId : {}, userId : {}, comment : {}",
                humanTaskId, userId, comment);
        humanTaskConnector.communicate(humanTaskId, userId, comment);

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 反馈.
     */
    @RequestMapping("task-operation-callback")
    public String callback(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("comment") String comment) {
        humanTaskConnector.callback(humanTaskId, "", comment);

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    /**
     * 加签.
     */
    @RequestMapping("task-operation-createVote")
    public String createVote(@RequestParam("humanTaskId") String humanTaskId,
            @RequestParam("userIds") String userIds,
            @RequestParam("comment") String comment) {
        HumanTaskDTO parentTask = humanTaskConnector.findHumanTask(humanTaskId);
        parentTask.setOwner(parentTask.getAssignee());
        parentTask.setAssignee("");
        humanTaskConnector.saveHumanTask(parentTask, false);

        for (String userId : userIds.split(",")) {
            HumanTaskDTO childTask = humanTaskConnector.createHumanTask();
            // copy
            childTask.setName(parentTask.getName());
            childTask.setPresentationSubject(parentTask
                    .getPresentationSubject());
            childTask.setForm(parentTask.getForm());
            childTask.setProcessInstanceId(parentTask.getProcessInstanceId());
            childTask.setProcessDefinitionId(parentTask
                    .getProcessDefinitionId());
            childTask.setTaskId(parentTask.getTaskId());
            childTask.setCode(parentTask.getCode());
            childTask.setBusinessKey(parentTask.getBusinessKey());
            childTask.setTenantId(parentTask.getTenantId());
            childTask.setStatus("active");
            // config
            childTask.setParentId(humanTaskId);
            childTask.setAssignee(userId);
            childTask.setCatalog(HumanTaskConstants.CATALOG_VOTE);
            humanTaskConnector.saveHumanTask(childTask, false);
        }

        return "redirect:/humantask/workspace-personalTasks.do";
    }

    @RequestMapping("task-operation-removeAttachment")
    @ResponseBody
    public BaseDTO removeAttachment(@RequestParam("name") String name,
            @RequestParam("humanTaskId") String humanTaskId) {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);
        String businessKey = humanTaskDto.getBusinessKey();
        operationService.removeAttachment(businessKey, name);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
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
        formParameter.setComment(multipartHandler.getMultiValueMap().getFirst(
                "_humantask_comment_"));
        formParameter.setAction(multipartHandler.getMultiValueMap().getFirst(
                "_humantask_action_"));

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
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            formParameter = this.buildFormParameter(multipartHandler);

            String businessKey = operationService.saveDraft(userId, tenantId,
                    formParameter);

            if ((formParameter.getBusinessKey() == null)
                    || "".equals(formParameter.getBusinessKey().trim())) {
                formParameter.setBusinessKey(businessKey);
            }
        } finally {
            multipartHandler.clear();
        }

        return formParameter;
    }

    /**
     * 根据taskId获得formDto.
     */
    public FormDTO findTaskForm(HumanTaskDTO humanTaskDto) {
        FormDTO formDto = humanTaskConnector.findTaskForm(humanTaskDto.getId());

        return formDto;
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

    @RequestMapping("task-operation-view")
    public String view(@RequestParam("humanTaskId") String humanTaskId,
            Model model) throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);
        String processInstanceId = humanTaskDto.getProcessInstanceId();

        // 显示三部分
        // 1. 流程基本信息
        ProcessBaseInfo processBaseInfo = viewService
                .findProcess(processInstanceId);
        model.addAttribute("processBaseInfo", processBaseInfo);

        // 2. 表单
        Map<String, Object> formResultMap = viewService
                .findTaskForm(humanTaskId);
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
                .findTaskToolbar(humanTaskId);
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
    public void setViewService(ViewService viewService) {
        this.viewService = viewService;
    }
}
