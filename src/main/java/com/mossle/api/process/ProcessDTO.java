package com.mossle.api.process;

public class ProcessDTO {
    private String processDefinitionId;
    private String processDefinitionName;
    private boolean configTask;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public boolean isConfigTask() {
        return configTask;
    }

    public void setConfigTask(boolean configTask) {
        this.configTask = configTask;
    }
}
