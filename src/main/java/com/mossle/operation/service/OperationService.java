package com.mossle.operation.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.model.ModelBuilder;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.employee.EmployeeClient;

import com.mossle.spi.process.InternalProcessConnector;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class OperationService {
    private static Logger logger = LoggerFactory
            .getLogger(OperationService.class);
    public static final String OPERATION_BUSINESS_KEY = "businessKey";
    public static final String OPERATION_TASK_ID = "taskId";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private HumanTaskConnector humanTaskConnector;
    private ProcessConnector processConnector;
    private ModelConnector modelConnector;
    private ProcessModelService processModelService;
    private TenantHolder tenantHolder;
    private EmployeeClient employeeClient;
    private InternalProcessConnector internalProcessConnector;

    /**
     * 保存草稿.
     */
    public String saveDraft(String userId, String tenantId,
            FormParameter formParameter) throws Exception {
        String humanTaskId = formParameter.getHumanTaskId();
        String businessKey = formParameter.getBusinessKey();
        String bpmProcessId = formParameter.getBpmProcessId();

        if (StringUtils.isNotBlank(humanTaskId)) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            businessKey = this.findBusinessKeyByHumanTaskId(humanTaskId);
            this.processModelService.updateData(businessKey, formParameter,
                    tenantId);
        } else if (StringUtils.isNotBlank(businessKey)) {
            // 如果是流程草稿，直接通过businessKey获得record，更新数据
            this.processModelService.updateData(businessKey, formParameter,
                    tenantId);
            // event
            this.internalProcessConnector.fireEvent("process-draft",
                    businessKey, userId, "", "");
        } else if (StringUtils.isNotBlank(bpmProcessId)) {
            EmployeeDTO employeeDto = employeeClient.findById(userId, "1");
            logger.debug("deptCode : {}", employeeDto.getDepartmentCode());

            // 如果是第一次保存草稿，肯定是流程草稿，先初始化record，再保存数据
            ModelInfoDTO modelInfoDto = this
                    .buildModelByBpmProcessId(bpmProcessId, null, userId,
                            employeeDto.getDepartmentCode());
            modelInfoDto = this.processModelService.createModel(modelInfoDto,
                    tenantId);
            businessKey = modelInfoDto.getCode();
            this.processModelService.updateData(businessKey, formParameter,
                    tenantId);
            // event
            this.internalProcessConnector.fireEvent("process-draft",
                    businessKey, userId, "", "");
        } else {
            logger.error(
                    "humanTaskId, businessKey, bpmProcessId all null : {}",
                    formParameter.getMultiValueMap());
            throw new IllegalArgumentException(
                    "humanTaskId, businessKey, bpmProcessId all null");
        }

        return businessKey;
    }

    public String findBusinessKeyByHumanTaskId(String humanTaskId) {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String processInstanceId = humanTaskDto.getProcessInstanceId();

        return processConnector
                .findBusinessKeyByProcessInstanceId(processInstanceId);
    }

    public ModelInfoDTO buildModelByBpmProcessId(String bpmProcessId,
            String businessKey, String initiator, String initiatorDept) {
        ProcessDTO processDto = processConnector.findProcess(bpmProcessId);
        ModelInfoDTO modelInfoDto = new ModelInfoDTO();

        if (StringUtils.isNotBlank(businessKey)) {
            modelInfoDto.setCode(businessKey);
        }

        modelInfoDto.setCategory(processDto.getCategory());
        modelInfoDto.setProcessId(processDto.getProcessDefinitionId());
        modelInfoDto.setProcessName(processDto.getProcessDefinitionName());
        modelInfoDto.setProcessKey(processDto.getKey());
        modelInfoDto.setProcessVersion(processDto.getVersion());
        // 创建人
        modelInfoDto.setInitiator(initiator);
        modelInfoDto.setInitiatorDept(initiatorDept);
        // 申请人
        modelInfoDto.setApplicant(initiator);
        modelInfoDto.setApplicantDept(initiatorDept);

        return modelInfoDto;
    }

    /**
     * 发起流程.
     */
    public void startProcessInstance(String userId, String businessKey,
            String processDefinitionId, Map<String, Object> processParameters,
            ModelInfoDTO modelInfoDto) {
        ModelInfoDTO theModelInfoDto = this.modelConnector
                .findByCode(businessKey);
        processParameters.put("initiator", theModelInfoDto.getInitiator());
        processParameters.put("initiatorDept",
                theModelInfoDto.getInitiatorDept());
        processParameters.put("initiateDate", new Date());

        String processInstanceId = processConnector.startProcess(userId,
                businessKey, processDefinitionId, processParameters);

        // model
        theModelInfoDto = this.modelConnector.findByCode(businessKey);
        theModelInfoDto.setInstanceId(processInstanceId);
        theModelInfoDto.setName("");
        theModelInfoDto.setStartTime(new Date());
        theModelInfoDto.setStatus("active");
        modelConnector.save(theModelInfoDto);
        // event
        this.internalProcessConnector.fireEvent("process-start",
                businessKey, userId, "", "审批中");
    }

    /**
     * 完成任务.
     */
    public void completeTask(String humanTaskId, String userId,
            FormParameter formParameter, Map<String, Object> taskParameters,
            ModelInfoDTO modelInfoDto) throws Exception {
        String businessKey = this.findBusinessKeyByHumanTaskId(humanTaskId);

        String tenantId = tenantHolder.getTenantId();
        humanTaskConnector.completeTask(humanTaskId, userId,
                formParameter.getAction(), formParameter.getComment(),
                taskParameters);

        // model
        this.processModelService.updateData(businessKey, formParameter,
                tenantId);
        // event
        this.internalProcessConnector.fireEvent("complete",
                businessKey, userId, "", "");
    }

    public void removeAttachment(String businessKey, String name) {
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        // TODO: remove by code
        modelInfoDto.findItem(name).setValue("");
        modelConnector.save(modelInfoDto);
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setEmployeeClient(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }
}
