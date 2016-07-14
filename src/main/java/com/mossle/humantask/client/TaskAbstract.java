package com.mossle.humantask.client;

import java.util.Date;

public class TaskAbstract {
    /** 唯一标识. */
    private String id;

    /** 类型. */
    private String type;

    /** 名称. */
    private String name;

    /** 状态. */
    private String status;

    /** 优先级. */
    private int priority;

    /** 创建时间. */
    private Date createdTime;

    /** 激活时间. */
    private Date activationTime;

    /** 过期时间. */
    private Date expirationTime;

    /** 是否可略过. */
    private boolean skipable;

    /** 是否拥有候选人. */
    private boolean hasPotentialOwners;

    /** 存在开始期限. */
    private boolean startByTimeExists;

    /** 存在完成期限. */
    private boolean completeByTimeExists;

    /** 展示名称. */
    private String presentationName;

    /** 展示描述. */
    private String presentationSubject;

    /** 是否存在渲染方法. */
    private boolean renderingMethodExists;

    /** 是否有输出. */
    private boolean hasOutput;

    /** 是否有错误. */
    private boolean hasFault;

    /** 是否有评论. */
    private boolean hasAttachments;

    /** 是否有注释. */
    private boolean hasComments;

    /** 是否是升级到二线的任务. */
    private boolean escalated;

    /** 后续. */
    private String outcome;

    /** 父任务主键. */
    private String parentTaskId;

    /** 是否子任务. */
    private boolean hasSubTasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(boolean skipable) {
        this.skipable = skipable;
    }

    public boolean isHasPotentialOwners() {
        return hasPotentialOwners;
    }

    public void setHasPotentialOwners(boolean hasPotentialOwners) {
        this.hasPotentialOwners = hasPotentialOwners;
    }

    public boolean isStartByTimeExists() {
        return startByTimeExists;
    }

    public void setStartByTimeExists(boolean startByTimeExists) {
        this.startByTimeExists = startByTimeExists;
    }

    public boolean isCompleteByTimeExists() {
        return completeByTimeExists;
    }

    public void setCompleteByTimeExists(boolean completeByTimeExists) {
        this.completeByTimeExists = completeByTimeExists;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    public String getPresentationSubject() {
        return presentationSubject;
    }

    public void setPresentationSubject(String presentationSubject) {
        this.presentationSubject = presentationSubject;
    }

    public boolean isRenderingMethodExists() {
        return renderingMethodExists;
    }

    public void setRenderingMethodExists(boolean renderingMethodExists) {
        this.renderingMethodExists = renderingMethodExists;
    }

    public boolean isHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(boolean hasOutput) {
        this.hasOutput = hasOutput;
    }

    public boolean isHasFault() {
        return hasFault;
    }

    public void setHashFault(boolean hasFault) {
        this.hasFault = hasFault;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public boolean isHasComments() {
        return hasComments;
    }

    public void setHasComments(boolean hasComments) {
        this.hasComments = hasComments;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParewntTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public boolean isHasSubTasks() {
        return hasSubTasks;
    }

    public void setHasSubTasks(boolean hasSubTasks) {
        this.hasSubTasks = hasSubTasks;
    }
}
