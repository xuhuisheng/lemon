package com.mossle.bpm;

public class FormInfo {
    private String formKey;
    private String processDefinitionId;
    private String taskId;
    private boolean autoCompleteFirstTask;

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isAutoCompleteFirstTask() {
        return autoCompleteFirstTask;
    }

    public void setAutoCompleteFirstTask(boolean autoCompleteFirstTask) {
        this.autoCompleteFirstTask = autoCompleteFirstTask;
    }

    public String getRelatedId() {
        if (isStartForm()) {
            return processDefinitionId;
        } else {
            return taskId;
        }
    }

    public boolean isTaskForm() {
        return taskId != null;
    }

    public boolean isStartForm() {
        return taskId == null;
    }

    public boolean isFormExists() {
        return formKey != null;
    }
}
