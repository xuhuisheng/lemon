package com.mossle.bpm.support;

import com.mossle.spi.humantask.CounterSignDTO;
import com.mossle.spi.humantask.FormDTO;
import com.mossle.spi.humantask.TaskDefinitionDTO;

import org.activiti.bpmn.model.UserTask;

public class TaskDefinitionBuilder {
    private TaskDefinitionDTO taskDefinition = new TaskDefinitionDTO();
    private UserTask userTask;
    private String processDefinitionId;

    public TaskDefinitionBuilder setUserTask(UserTask userTask) {
        this.userTask = userTask;

        return this;
    }

    public TaskDefinitionBuilder setProcessDefinitionId(
            String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;

        return this;
    }

    public TaskDefinitionDTO build() {
        this.initInfo();
        this.initParticipants();
        this.initForm();
        this.initCounterSign();

        return taskDefinition;
    }

    public void initInfo() {
        taskDefinition.setCode(userTask.getId());
        taskDefinition.setName(userTask.getName());
        taskDefinition.setProcessDefinitionId(processDefinitionId);
    }

    public void initParticipants() {
        taskDefinition.setAssignee(userTask.getAssignee());

        for (String candidateUser : userTask.getCandidateUsers()) {
            taskDefinition.addCandidateUser(candidateUser);
        }

        for (String candidateGroup : userTask.getCandidateGroups()) {
            taskDefinition.addCandidateGroup(candidateGroup);
        }
    }

    public void initForm() {
        String formKey = userTask.getFormKey();

        if (formKey == null) {
            return;
        }

        FormDTO form = new FormDTO();

        if (formKey.startsWith("external:")) {
            form.setType("external");
        } else {
            form.setType("internal");
        }

        form.setKey(formKey);
        taskDefinition.setForm(form);
    }

    public void initCounterSign() {
        if (userTask.getLoopCharacteristics() == null) {
            return;
        }

        CounterSignDTO counterSign = new CounterSignDTO();
        counterSign.setStrategy("percent");
        counterSign.setRate(100);

        if (userTask.getLoopCharacteristics().isSequential()) {
            counterSign.setType("sequential");
        } else {
            counterSign.setType("parallel");
        }

        taskDefinition.setCounterSign(counterSign);
    }
}
