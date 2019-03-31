package com.mossle.operation.web;
import java.util.List;
import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.form.FormMetadata;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.client.employee.EmployeeClient;
import com.mossle.client.store.StoreClient;

import com.mossle.core.MultipartHandler;
import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.operation.service.ProcessModelService;
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
import org.springframework.web.multipart.MultipartResolver;

@Controller
@RequestMapping("operation")
public class FormOperationController {
    private static Logger logger = LoggerFactory
            .getLogger(FormOperationController.class);
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private MessageHelper messageHelper;
    private MultipartResolver multipartResolver;
    private StoreClient storeClient;
    private FormConnector formConnector;
    private TenantHolder tenantHolder;
    private ModelConnector modelConnector;
    private ProcessConnector processConnector;
    private CurrentUserHolder currentUserHolder;
    private EmployeeClient employeeClient;
    private ProcessModelService processModelService;
    private HumanTaskConnector humanTaskConnector;

    @RequestMapping("form-operation-preview")
    public String preview(@RequestParam("code") String code, Model model)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        FormDTO formDto = formConnector.findForm(code, tenantId);

        // Record record = keyValueConnector.findByRef(code);
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(code);

        // if (record == null) {
        // record = new Record();
        // record.setName(formDto.getName());
        // record.setRef(formDto.getCode());
        // keyValueConnector.save(record);
        // }
        // model.addAttribute("record", record);
        if (modelInfoDto == null) {
            modelInfoDto = new ModelInfoDTO();
            modelInfoDto.setName(formDto.getName());
            modelInfoDto.setCode(code);
            modelConnector.save(modelInfoDto);
        }

        // Xform xform = new XformBuilder().setStoreConnector(storeConnector)
        // .setContent(formDto.getContent()).setRecord(record).build();
        Xform xform = new XformBuilder().setStoreClient(storeClient)
                .setContent(formDto.getContent()).setModelInfoDto(modelInfoDto)
                .build();
        model.addAttribute("xform", xform);

        return "operation/form-operation-preview";
    }

    @RequestMapping("form-operation-test")
    public String test(HttpServletRequest request) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        FormDTO formDto = null;

        try {
            multipartHandler.handle(request);
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            String ref = multipartHandler.getMultiValueMap().getFirst("ref");

            formDto = formConnector.findForm(ref, tenantId);

            // Record record = keyValueConnector.findByRef(ref);

            // record = new RecordBuilder().build(record, multipartHandler,
            // storeConnector, tenantId);

            // keyValueConnector.save(record);
        } finally {
            multipartHandler.clear();
        }

        if (formDto == null) {
            return "redirect:/form/form-template-list.do";
        } else {
            return "redirect:/operation/form-operation-preview.do?code="
                    + formDto.getCode();
        }
    }

    @RequestMapping("form-operation-view")
    public String view(@RequestParam("businessKey") String businessKey,
            Model model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);

        if (modelInfoDto == null) {
            logger.info("cannot find : {}", businessKey);

            return "redirect:/";
        }

        ProcessDTO processDto = processConnector
                .findProcessByProcessDefinitionId(modelInfoDto.getProcessId());
        String bpmProcessId = processDto.getId();
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
                // ModelInfoDTO modelInfoDto = null;
                if (businessKey != null) {
                    modelInfoDto = modelConnector.findByCode(businessKey);
                }

                this.viewExternalForm(request, response, url, formMetadata,
                        modelInfoDto);

                return null;
            }


	        // 审批记录
	        List<HumanTaskDTO> logHumanTaskDtos = this.humanTaskConnector
	                .findHumanTasksByProcessInstanceId(modelInfoDto
	                        .getInstanceId());
	        model.addAttribute("logHumanTaskDtos", logHumanTaskDtos);

            return this.doViewStartForm(formParameter, model, tenantId);
        }

        return "form/form-operation-view-blank";
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

        model.addAttribute("formDto", formParameter.getFormDto());

        String businessKey = formParameter.getBusinessKey();

        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        FormDTO formDto = formConnector.findForm(formParameter.getFormDto()
                .getCode(), tenantId);
        Xform xform = this.processModelService.processFormData(businessKey,
                formDto);
        model.addAttribute("xform", xform);

        return "operation/form-operation-view";
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

    // ~ ======================================================================
    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
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
    public void setModelConnector(ModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setEmployeeClient(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    @Resource
    public void setProcessModelService(ProcessModelService processModelService) {
        this.processModelService = processModelService;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
