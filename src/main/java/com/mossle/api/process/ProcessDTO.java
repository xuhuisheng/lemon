package com.mossle.api.process;

public class ProcessDTO {
    private String processDefinitionId;
    private boolean configTask;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public boolean isConfigTask() {
        return configTask;
    }

    public void setConfigTask(boolean configTask) {
        this.configTask = configTask;
    }
}
