package com.mossle.bpm.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.cmd.FindProcessDefinitionEntityCmd;
import com.mossle.bpm.support.ProcessInstanceDTO;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;

import org.springframework.stereotype.Component;

@Component
public class ProcessInstanceConverter {
    private UserConnector userConnector;
    private ProcessEngine processEngine;

    public List<ProcessInstanceDTO> convertProcessInstances(
            List<ProcessInstance> processInstances) {
        List<ProcessInstanceDTO> processInstanceDtos = new ArrayList<ProcessInstanceDTO>();

        for (ProcessInstance processInstance : processInstances) {
            ProcessInstanceDTO processInstanceDto = convertProcessInstance(processInstance);
            processInstanceDtos.add(processInstanceDto);
        }

        return processInstanceDtos;
    }

    public ProcessInstanceDTO convertProcessInstance(
            ProcessInstance processInstance) {
        // ProcessInstanceDTO processInstanceDto = new ProcessInstanceDTO();
        // return processInstanceDto;
        throw new UnsupportedOperationException();
    }

    public List<ProcessInstanceDTO> convertHistoryProcessInstances(
            List<HistoricProcessInstance> historicProcessInstances) {
        List<ProcessInstanceDTO> processInstanceDtos = new ArrayList<ProcessInstanceDTO>();

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            ProcessInstanceDTO processInstanceDto = convertHistoryProcessInstance(historicProcessInstance);
            processInstanceDtos.add(processInstanceDto);
        }

        return processInstanceDtos;
    }

    public ProcessInstanceDTO convertHistoryProcessInstance(
            HistoricProcessInstance historicProcessInstance) {
        ProcessInstanceDTO processInstanceDto = new ProcessInstanceDTO();
        String processDefinitionId = historicProcessInstance
                .getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = processEngine
                .getManagementService()
                .executeCommand(
                        new FindProcessDefinitionEntityCmd(processDefinitionId));
        processInstanceDto.setId(historicProcessInstance.getId());
        processInstanceDto.setProcessDefinitionId(processDefinitionId);
        processInstanceDto.setProcessDefinitionName(processDefinitionEntity
                .getName());
        processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
        processInstanceDto.setEndTime(historicProcessInstance.getEndTime());

        String userId = historicProcessInstance.getStartUserId();
        processInstanceDto.setUserId(userId);
        processInstanceDto.setUsername(userConnector.findById(userId)
                .getDisplayName());

        return processInstanceDto;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
