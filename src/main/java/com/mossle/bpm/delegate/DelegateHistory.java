package com.mossle.bpm.delegate;

import java.util.Date;

public class DelegateHistory {
    private Long id;
    private String assignee;
    private String assigneeDisplayName;
    private String attorney;
    private String attorneyDisplayName;
    private Date delegateTime;
    private String taskId;
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAttorney() {
        return attorney;
    }

    public void setAttorney(String attorney) {
        this.attorney = attorney;
    }

    public Date getDelegateTime() {
        return delegateTime;
    }

    public void setDelegateTime(Date delegateTime) {
        this.delegateTime = delegateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // ~ ==================================================
    public String getAssigneeDisplayName() {
        return assigneeDisplayName;
    }

    public void setAssigneeDisplayName(String assigneeDisplayName) {
        this.assigneeDisplayName = assigneeDisplayName;
    }

    public String getAttorneyDisplayName() {
        return attorneyDisplayName;
    }

    public void setAttorneyDisplayName(String attorneyDisplayName) {
        this.attorneyDisplayName = attorneyDisplayName;
    }
}
