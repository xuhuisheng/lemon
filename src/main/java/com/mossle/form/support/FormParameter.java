package com.mossle.form.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.MultiValueMap;

public class FormParameter {
    public static final String OPERATION_BUSINESS_KEY = "businessKey";
    public static final String OPERATION_TASK_ID = "taskId";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    private MultiValueMap<String, String> multiValueMap;

    public FormParameter(MultiValueMap<String, String> multiValueMap) {
        this.multiValueMap = multiValueMap;
    }

    public String getTaskId() {
        return multiValueMap.getFirst(OPERATION_TASK_ID);
    }

    public String getBusinessKey() {
        return multiValueMap.getFirst(OPERATION_BUSINESS_KEY);
    }

    public String getBpmProcessId() {
        return multiValueMap.getFirst(OPERATION_BPM_PROCESS_ID);
    }

    public Map<String, List<String>> getMultiValueMap() {
        return multiValueMap;
    }

    public List<String> getList(String key) {
        return multiValueMap.get(key);
    }
}
