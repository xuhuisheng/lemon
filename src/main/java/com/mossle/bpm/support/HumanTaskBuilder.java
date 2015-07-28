package com.mossle.bpm.support;

import java.util.Date;

import com.mossle.api.humantask.HumanTaskDTO;

import org.activiti.engine.delegate.DelegateTask;

public class HumanTaskBuilder {
    private HumanTaskDTO humanTaskDto = new HumanTaskDTO();

    public HumanTaskBuilder setDelegateTask(DelegateTask delegateTask) {
        humanTaskDto.setName(delegateTask.getName());
        humanTaskDto.setDescription(delegateTask.getDescription());
        humanTaskDto.setCode(delegateTask.getTaskDefinitionKey());
        humanTaskDto.setAssignee(delegateTask.getAssignee());
        humanTaskDto.setOwner(delegateTask.getOwner());
        humanTaskDto.setDelegateStatus("none");
        humanTaskDto.setPriority(delegateTask.getPriority());
        humanTaskDto.setCreateTime(new Date());
        humanTaskDto.setDuration(delegateTask.getDueDate() + "");
        humanTaskDto.setSuspendStatus("none");
        humanTaskDto.setCategory(delegateTask.getCategory());
        humanTaskDto.setForm(delegateTask.getFormKey());
        humanTaskDto.setTaskId(delegateTask.getId());
        humanTaskDto.setExecutionId(delegateTask.getExecutionId());
        humanTaskDto.setProcessInstanceId(delegateTask.getProcessInstanceId());
        humanTaskDto.setProcessDefinitionId(delegateTask
                .getProcessDefinitionId());
        humanTaskDto.setTenantId(delegateTask.getTenantId());
        humanTaskDto.setStatus("active");

        return this;
    }

    public HumanTaskDTO build() {
        return humanTaskDto;
    }
}
