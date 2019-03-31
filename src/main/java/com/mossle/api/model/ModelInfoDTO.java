package com.mossle.api.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelInfoDTO implements Serializable {
    /** 物理id. */
    private Long id;

    /** 逻辑id，工单流水号, businessKey. */
    private String code;

    /** 流程名称. */
    private String name;

    /** 流程实例id. */
    private String instanceId;

    /** 流程分类. */
    private String category;

    /** 流程定义id. */
    private String processId;

    /** 流程定义名称. */
    private String processName;

    /** 流程定义编码. */
    private String processKey;

    /** 流程定义版本. */
    private Integer processVersion;

    /** 创建人. */
    private String initiator;

    /** 创建人部门. */
    private String initiatorDept;

    /** 申请人. */
    private String applicant;

    /** 申请人部门. */
    private String applicantDept;

    /** 创建时间. */
    private Date createTime;

    /** 发起时间. */
    private Date startTime;

    /** 结束时间. */
    private Date endTime;

    /** 类型. */
    private String type;

    /** 步骤id. */
    private String activityId;

    /** 步骤名称. */
    private String activityName;

    /** 步骤. */
    private String step;

    /** 状态. */
    private String status;

    /** 删除原因. */
    private String deleteReason;

    /** 备注. */
    private String description;

    /** 明细. */
    private Map<String, ModelItemDTO> itemMap = new HashMap<String, ModelItemDTO>();

    /** 子表. */
    private List<ModelInfoDTO> infos = new ArrayList<ModelInfoDTO>();

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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
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

    // ~
    public Map<String, ModelItemDTO> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<String, ModelItemDTO> itemMap) {
        this.itemMap = itemMap;
    }

    public List<ModelInfoDTO> getInfos() {
        return infos;
    }

    public void setInfos(List<ModelInfoDTO> infos) {
        this.infos = infos;
    }

    // ~
    public List<ModelItemDTO> getItems() {
        return Collections.unmodifiableList(new ArrayList<ModelItemDTO>(
                this.itemMap.values()));
    }

    public ModelItemDTO findItem(String code) {
        return itemMap.get(code);
    }

    public void addItem(ModelItemDTO modelItemDto) {
        itemMap.put(modelItemDto.getCode(), modelItemDto);
    }

    public Object findItemValue(String name) {
        ModelItemDTO modelItem = this.findItem(name);

        if (modelItem == null) {
            return null;
        }

        return modelItem.getValue();
    }
}
