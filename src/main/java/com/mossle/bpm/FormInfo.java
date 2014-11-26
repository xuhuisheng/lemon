package com.mossle.bpm;

import java.util.ArrayList;
import java.util.List;

public class FormInfo {
    private String formKey;
    private String processDefinitionId;
    private String taskId;
    private boolean autoCompleteFirstTask;
    private String activityId;
    private List<String> buttons = new ArrayList<String>();
    private boolean redirect;
    private String url;
    private String content;

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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
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

    public List<String> getButtons() {
        return buttons;
    }

    public void setButtons(List<String> buttons) {
        this.buttons = buttons;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
