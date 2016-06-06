package com.mossle.api.keyvalue;

import java.util.Collections;
import java.util.List;

import com.mossle.api.form.FormDTO;

import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;

public class FormParameter {
    public static final String OPERATION_BUSINESS_KEY = "businessKey";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    public static final String OPERATION_HUMAN_TASK_ID = "humanTaskId";
    public static final String OPERATION_COMMENT = "_humantask_comment_";
    public static final String OPERATION_ACTION = "_humantask_action_";
    private String businessKey = null;
    private String bpmProcessId = null;
    private String humanTaskId = null;
    private String action;
    private String comment = null;
    private MultiValueMap<String, String> multiValueMap;
    private MultiValueMap<String, MultipartFile> multiFileMap;
    private FormDTO formDto;
    private String nextStep;
    private String processDefinitionId;

    public FormParameter() {
    }

    public FormParameter(MultiValueMap<String, String> multiValueMap) {
        this.multiValueMap = multiValueMap;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBpmProcessId() {
        return bpmProcessId;
    }

    public void setBpmProcessId(String bpmProcessId) {
        this.bpmProcessId = bpmProcessId;
    }

    public String getHumanTaskId() {
        return humanTaskId;
    }

    public void setHumanTaskId(String humanTaskId) {
        this.humanTaskId = humanTaskId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        return multiValueMap;
    }

    public void setMultiValueMap(MultiValueMap<String, String> multiValueMap) {
        this.multiValueMap = multiValueMap;
    }

    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return multiFileMap;
    }

    public void setMultiFileMap(
            MultiValueMap<String, MultipartFile> multiFileMap) {
        this.multiFileMap = multiFileMap;
    }

    public FormDTO getFormDto() {
        return formDto;
    }

    public void setFormDto(FormDTO formDto) {
        this.formDto = formDto;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    // ~ ==================================================
    public List<String> getList(String key) {
        if (multiValueMap == null) {
            return Collections.emptyList();
        }

        return multiValueMap.get(key);
    }
}
