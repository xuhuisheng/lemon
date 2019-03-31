package com.mossle.operation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.form.FormMetadata;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.model.ModelBuilder;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;
import com.mossle.api.process.ProcessBaseInfo;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.employee.EmployeeClient;

import com.mossle.core.mapper.JsonMapper;

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

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ViewService {
    private static Logger logger = LoggerFactory.getLogger(ViewService.class);
    private HumanTaskConnector humanTaskConnector;
    private ProcessConnector processConnector;
    private ModelConnector modelConnector;
    private ProcessModelService processModelService;
    private TenantHolder tenantHolder;
    private ProcessEngine processEngine;
    private FormConnector formConnector;
    private InternalProcessConnector internalProcessConnector;
    private CurrentUserHolder currentUserHolder;
    private JsonMapper jsonMapper = new JsonMapper();
    private EmployeeClient employeeClient;

    /**
     * 根据流程实例id获取历史流程实例对象.
     */
    public HistoricProcessInstance findHistoricProcessInstance(
            String processInstanceId) {
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        return historicProcessInstance;
    }

    public ProcessBaseInfo findProcess(String processInstanceId) {
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(
                        historicProcessInstance.getProcessDefinitionId())
                .singleResult();

        ProcessBaseInfo processBaseInfo = new ProcessBaseInfo();
        processBaseInfo
                .setBusinessKey(historicProcessInstance.getBusinessKey());
        processBaseInfo.setId(processInstanceId);
        processBaseInfo.setCode(processDefinition.getKey());
        processBaseInfo.setName(processDefinition.getName());
        processBaseInfo.setUserId(historicProcessInstance.getStartUserId());
        processBaseInfo.setStartTime(historicProcessInstance.getStartTime());

        if (historicProcessInstance.getEndTime() == null) {
            processBaseInfo.setStatus("active");
        } else {
            processBaseInfo.setStatus("end");
        }

        List<String> userIds = new ArrayList<String>();
        List<String> activityNames = new ArrayList<String>();
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(processInstanceId).list();

        for (Task task : tasks) {
            userIds.add(task.getAssignee());
            activityNames.add(task.getName());
        }

        processBaseInfo.setAssignee(StringUtils.join(userIds, ","));
        processBaseInfo.setActivityName(StringUtils.join(activityNames, ","));

        return processBaseInfo;
    }

    public void findGraph(String processInstanceId) {
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        // model.addAttribute("nodeDtos",
        // traceService.traceProcessInstance(processInstanceId));
        // model.addAttribute("historyActivities", processEngine
        // .getHistoryService().createHistoricActivityInstanceQuery()
        // .processInstanceId(processInstanceId).list());

        // if (historicProcessInstance.getEndTime() == null) {
        // model.addAttribute("currentActivities", processEngine
        // .getRuntimeService()
        // .getActiveActivityIds(processInstanceId));
        // } else {
        // model.addAttribute("currentActivities", Collections
        // .singletonList(historicProcessInstance.getEndActivityId()));
        // }
    }

    public Map<String, Object> findProcessForm(String processInstanceId)
            throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = historicProcessInstance
                .getProcessDefinitionId();

        FormDTO formDto = this.processConnector
                .findStartForm(processDefinitionId);
        // model.addAttribute("formDto", formDto);
        result.put("formDto", formDto);

        String businessKey = processConnector
                .findBusinessKeyByProcessInstanceId(processInstanceId);
        String tenantId = tenantHolder.getTenantId();
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        formDto = formConnector.findForm(formDto.getCode(), tenantId);

        Xform xform = this.processModelService.processFormData(businessKey,
                formDto);
        // model.addAttribute("xform", xform);
        result.put("xform", xform);
        this.processExternalForm(result, formDto, businessKey, modelInfoDto);

        return result;
    }

    public Map<String, Object> findTaskForm(String humanTaskId)
            throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);
        String processInstanceId = humanTaskDto.getProcessInstanceId();

        FormDTO formDto = humanTaskConnector.findTaskForm(humanTaskId);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("formDto", formDto);

        String businessKey = processConnector
                .findBusinessKeyByProcessInstanceId(processInstanceId);
        String tenantId = tenantHolder.getTenantId();
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        formDto = formConnector.findForm(formDto.getCode(), tenantId);

        Xform xform = this.processModelService.processFormData(businessKey,
                formDto);
        // model.addAttribute("xform", xform);
        result.put("xform", xform);

        this.processExternalForm(result, formDto, businessKey, modelInfoDto);

        return result;
    }

    public void processExternalForm(Map<String, Object> result,
            FormDTO formDto, String businessKey, ModelInfoDTO modelInfoDto)
            throws Exception {
        // 如果是外部表单，就直接跳转出去
        if (!formDto.isRedirect()) {
            return;
        }

        String tenantId = tenantHolder.getTenantId();

        // metadata
        FormMetadata formMetadata = new FormMetadata();
        result.put("formMetadata", formMetadata);
        formMetadata.setBusinessKey(businessKey);

        String userId = currentUserHolder.getUserId();
        EmployeeDTO employeeDto = employeeClient.findById(userId, tenantId);
        formMetadata.setUserId(userId);
        formMetadata.setDisplayName(employeeDto.getName());
        formMetadata.setDepartmentId(employeeDto.getDepartmentCode());
        formMetadata.setDepartmentName(employeeDto.getDepartmentName());

        // data
        if (modelInfoDto == null) {
            return;
        }

        FormData formData = new FormDataBuilder().setModelInfoDto(modelInfoDto)
                .build();
        String formDataJson = jsonMapper.toJson(formData);
        result.put("formData", formData);
        result.put("formDataJson", formDataJson);
    }

    public List<HumanTaskDTO> findHumanTasks(String processInstanceId) {
        // 获取流程对应的所有人工任务（目前还没有区分历史）
        List<HumanTaskDTO> humanTasks = humanTaskConnector
                .findHumanTasksByProcessInstanceId(processInstanceId);
        List<HumanTaskDTO> humanTaskDtos = new ArrayList<HumanTaskDTO>();

        for (HumanTaskDTO humanTaskDto : humanTasks) {
            if (humanTaskDto.getParentId() != null) {
                continue;
            }

            humanTaskDtos.add(humanTaskDto);
        }

        return humanTaskDtos;
    }

    public List<Map<String, String>> findProcessToolbar(String processInstanceId) {
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        boolean isEnd = historicProcessInstance.getEndTime() != null;
        List<Map<String, String>> buttons = new ArrayList<Map<String, String>>();

        if (isEnd) {
            // this.addButton(buttons, "复制",
            // "/workspace-copyProcessInstance.do?processInstanceId="
            // + processInstanceId);
            // this.addButton(buttons, "转发", "javascript:void(0);doTransfer('"
            // + processInstanceId + "')");
            this.addButton(buttons, "复制", "taskOperation.copyProcess()");
            this.addButton(buttons, "转发", "taskOperation.transferProcess()");
        } else {
            // this.addButton(buttons, "终止",
            // "/bpm/workspace-endProcessInstance.do?processInstanceId="
            // + processInstanceId + "&userId=&comment=");
            // this.addButton(buttons, "催办",
            // "/bpm/workspace-remind.do?processInstanceId="
            // + processInstanceId + "&userId=&comment=");
            // 跳过风险太高了，想明白了再加
            // this.addButton(buttons, "跳过",
            // "/bpm/workspace-skip.do?processInstanceId="
            // + processInstanceId + "&userId=&comment=");
            // if (couldProcessWithdraw(processInstanceId)) {
            // this.addButton(buttons, "撤销",
            // "/bpm/workspace-withdraw.do?processInstanceId="
            // + processInstanceId + "&userId=&comment=");
            // }
            this.addButton(buttons, "终止", "taskOperation.endProcess()");
            this.addButton(buttons, "催办", "taskOperation.remind()");
            this.addButton(buttons, "跳过", "taskOperation.skip()");

            if (couldProcessWithdraw(processInstanceId)) {
                this.addButton(buttons, "撤销", "taskOperation.withdrawProcess()");
            }
        }

        return buttons;
    }

    public List<Map<String, String>> findTaskToolbar(String humanTaskId) {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTask(humanTaskId);
        List<Map<String, String>> buttons = new ArrayList<Map<String, String>>();
        // this.addButton(buttons, "转发", "javascript:void(0);doTransfer('"
        // + humanTaskId + "')");
        this.addButton(buttons, "转发", "taskOperation.transferTask();");

        if (couldTaskWithdraw(humanTaskDto)) {
            // this.addButton(buttons, "撤销",
            // "/operation/task-operation-withdraw.do?humanTaskId="
            // + humanTaskId + "&userId=&comment=");
            this.addButton(buttons, "撤销", "taskOperation.withdrawTask();");
        }

        return buttons;
    }

    public boolean couldProcessWithdraw(String processInstanceId) {
        List<HumanTaskDTO> humanTaskDtos = humanTaskConnector
                .findHumanTasksByProcessInstanceId(processInstanceId);

        if (humanTaskDtos.isEmpty()) {
            return false;
        }

        HumanTaskDTO humanTaskDto = humanTaskDtos.get(0);

        return internalProcessConnector.checkWithdraw(humanTaskDto.getTaskId());
    }

    public boolean couldTaskWithdraw(HumanTaskDTO humanTaskDto) {
        return internalProcessConnector.checkWithdraw(humanTaskDto.getTaskId());
    }

    public void addButton(List<Map<String, String>> buttons, String name,
            String url) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        map.put("url", url);
        buttons.add(map);
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
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setEmployeeClient(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }
}
