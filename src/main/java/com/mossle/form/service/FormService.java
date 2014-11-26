package com.mossle.form.service;

import java.util.*;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.support.FormParameter;

import org.springframework.stereotype.Component;

@Component
public class FormService {
    public static final String OPERATION_BUSINESS_KEY = "businessKey";
    public static final String OPERATION_TASK_ID = "taskId";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private KeyValue keyValue;
    private HumanTaskConnector humanTaskConnector;

    public String saveDraft(String userId, FormParameter formParameter) {
        String taskId = formParameter.getTaskId();
        String businessKey = formParameter.getBusinessKey();
        String bpmProcessId = formParameter.getBpmProcessId();

        if (this.notEmpty(taskId)) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            HumanTaskDTO humanTaskDto = humanTaskConnector
                    .findHumanTask(taskId);

            if (humanTaskDto == null) {
                throw new IllegalStateException("任务不存在");
            }

            String processInstanceId = humanTaskDto.getProcessInstanceId();
            Record record = keyValue.findByRef(processInstanceId);

            if (record != null) {
                record = new RecordBuilder().build(record, STATUS_DRAFT_TASK,
                        formParameter);
                keyValue.save(record);
                businessKey = record.getCode();
            }
        } else if (this.notEmpty(businessKey)) {
            // 如果是流程草稿，直接通过businessKey获得record，更新数据
            Record record = keyValue.findByCode(businessKey);

            record = new RecordBuilder().build(record, STATUS_DRAFT_PROCESS,
                    formParameter);
            keyValue.save(record);
        } else {
            // 如果是第一次保存草稿，肯定是流程草稿，先初始化record，再保存数据
            Record record = new RecordBuilder().build(bpmProcessId,
                    STATUS_DRAFT_PROCESS, formParameter, userId);
            keyValue.save(record);
            businessKey = record.getCode();
        }

        return businessKey;
    }

    public String getParameter(Map<String, String[]> parameters, String name) {
        String[] value = parameters.get(name);

        if ((value == null) || (value.length == 0)) {
            return null;
        }

        return value[0];
    }

    public List<String> getParameterValues(Map<String, String[]> parameters,
            String name) {
        String[] value = parameters.get(name);

        if ((value == null) || (value.length == 0)) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.asList(value);
    }

    public Map<String, Object> getVariables(Map<String, String[]> parameters) {
        Map<String, Object> variables = new HashMap<String, Object>();

        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            if ((value == null) || (value.length == 0)) {
                variables.put(key, null);
            } else {
                variables.put(key, value[0]);
            }
        }

        return variables;
    }

    public boolean notEmpty(String str) {
        return (str != null) && (!"".equals(str));
    }

    @Resource
    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
