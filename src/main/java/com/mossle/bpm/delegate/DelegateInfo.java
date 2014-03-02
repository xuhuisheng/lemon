package com.mossle.bpm.delegate;

import java.util.Date;

public class DelegateInfo {
    private Long id;
    private String assignee;
    private String assigneeDisplayName;
    private String attorney;
    private String attorneyDisplayName;
    private Date startTime;
    private Date endTime;
    private String processDefinitionId;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
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
