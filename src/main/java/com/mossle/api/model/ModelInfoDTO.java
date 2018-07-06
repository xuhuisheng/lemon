package com.mossle.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelInfoDTO {
    private Long id;
    private String code;
    private String name;
    private String instanceId;
    private String category;
    private String processId;
    private String processName;
    private String processKey;
    private Integer processVersion;
    private String initiator;
    private String initiatorDept;
    private String applicant;
    private String applicantDept;
    private Date createTime;
    private Date startTime;
    private Date endTime;
    private String type;
    private String status;
    private String deleteReason;
    private String description;
    private List<ModelItemDTO> items = new ArrayList<ModelItemDTO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProcessId() {
        return this.processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessKey() {
        return this.processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public Integer getProcessVersion() {
        return this.processVersion;
    }

    public void setProcessVersion(Integer processVersion) {
        this.processVersion = processVersion;
    }

    public String getInitiator() {
        return this.initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getInitiatorDept() {
        return this.initiatorDept;
    }

    public void setInitiatorDept(String initiatorDept) {
        this.initiatorDept = initiatorDept;
    }

    public String getApplicant() {
        return this.applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplicantDept() {
        return this.applicantDept;
    }

    public void setApplicantDept(String applicantDept) {
        this.applicantDept = applicantDept;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return this.startTime;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ModelItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ModelItemDTO> items) {
        this.items = items;
    }
}
