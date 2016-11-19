package com.mossle.operation.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.KeyValueConnector;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class OperationService {
    private static Logger logger = LoggerFactory
            .getLogger(OperationService.class);
    public static final String OPERATION_BUSINESS_KEY = "businessKey";
    public static final String OPERATION_TASK_ID = "taskId";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private KeyValueConnector keyValueConnector;
    private HumanTaskConnector humanTaskConnector;
    private ProcessConnector processConnector;

    /**
     * 保存草稿.
     */
    public String saveDraft(String userId, String tenantId,
            FormParameter formParameter) {
        String humanTaskId = formParameter.getHumanTaskId();
        String businessKey = formParameter.getBusinessKey();
        String bpmProcessId = formParameter.getBpmProcessId();

        if (StringUtils.isNotBlank(humanTaskId)) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            HumanTaskDTO humanTaskDto = humanTaskConnector
                    .findHumanTask(humanTaskId);

            if (humanTaskDto == null) {
                throw new IllegalStateException("任务不存在");
            }

            String processInstanceId = humanTaskDto.getProcessInstanceId();
            Record record = keyValueConnector.findByRef(processInstanceId);

            if (record != null) {
                record = new RecordBuilder().build(record, STATUS_DRAFT_TASK,
                        formParameter);
                keyValueConnector.save(record);
                businessKey = record.getCode();
            }
        } else if (StringUtils.isNotBlank(businessKey)) {
            // 如果是流程草稿，直接通过businessKey获得record，更新数据
            Record record = keyValueConnector.findByCode(businessKey);

            record = new RecordBuilder().build(record, STATUS_DRAFT_PROCESS,
                    formParameter);
            keyValueConnector.save(record);
        } else if (StringUtils.isNotBlank(bpmProcessId)) {
            // 如果是第一次保存草稿，肯定是流程草稿，先初始化record，再保存数据
            Record record = new RecordBuilder().build(bpmProcessId,
                    STATUS_DRAFT_PROCESS, formParameter, userId, tenantId);
            ProcessDTO processDto = processConnector.findProcess(bpmProcessId);
            record.setName(processDto.getProcessDefinitionName());
            keyValueConnector.save(record);
            businessKey = record.getCode();
        } else {
            logger.error(
                    "humanTaskId, businessKey, bpmProcessId all null : {}",
                    formParameter.getMultiValueMap());
            throw new IllegalArgumentException(
                    "humanTaskId, businessKey, bpmProcessId all null");
        }

        return businessKey;
    }

    /**
     * 发起流程.
     */
    public void startProcessInstance(String userId, String businessKey,
            String processDefinitionId, Map<String, Object> processParameters,
            Record record) {
        String processInstanceId = processConnector.startProcess(userId,
                businessKey, processDefinitionId, processParameters);

        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstanceId);
        keyValueConnector.save(record);
    }

    /**
     * 完成任务.
     */
    public void completeTask(String humanTaskId, String userId,
            FormParameter formParameter, Map<String, Object> taskParameters,
            Record record, String processInstanceId) {
        // try {
        humanTaskConnector.completeTask(humanTaskId, userId,
                formParameter.getAction(), formParameter.getComment(),
                taskParameters);

        // } catch (IllegalStateException ex) {
        // logger.error(ex.getMessage(), ex);
        // messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());
        // return "redirect:/humantask/workspace-personalTasks.do";
        // }
        if (record == null) {
            record = new Record();
        }

        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstanceId);
        keyValueConnector.save(record);
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

    @Resource
    public void setKeyValueConnector(KeyValueConnector keyValueConnector) {
        this.keyValueConnector = keyValueConnector;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }
}
