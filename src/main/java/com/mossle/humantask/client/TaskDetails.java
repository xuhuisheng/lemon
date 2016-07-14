package com.mossle.humantask.client;

import java.util.Date;
import java.util.List;

public class TaskDetails extends TaskAbstract {
    /** 任务创建者. */
    private String initiator;

    /** 任务控制者. */
    private List<String> stakeholders;

    /** 候选人. */
    private List<String> potentialOwners;

    /** 业务管理员. */
    private List<String> businessAdministrators;

    /** 负责人. */
    private String actualOwner;

    /** 关注人员. */
    private List<String> notificationRecipients;

    /** 创建人. */
    private String createdBy;

    /** 最后修改时间. */
    private Date lastModifiedTime;

    /** 最后修改人. */
    private String lastModifiedBy;

    /** 查询条件. */
    private String searchBy;

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public List<String> getStakeholders() {
        return stakeholders;
    }

    public void setStakeholders(List<String> stakeholders) {
        this.stakeholders = stakeholders;
    }

    public List<String> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<String> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    public List<String> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public void setBusinessAdministrators(List<String> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public List<String> getNotificationRecipients() {
        return notificationRecipients;
    }

    public void setNotificationRecipients(List<String> notificationRecipients) {
        this.notificationRecipients = notificationRecipients;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }
}
