package com.mossle.form.operation;

import java.util.Map;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;

import com.mossle.core.spring.ApplicationContextHelper;

import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.task.Task;

import org.springframework.util.Assert;

/**
 * 保存草稿.
 */
public class SaveDraftOperation extends AbstractOperation<String> {
    public static final String OPERATION_BUSINESS_KEY = "businessKey";
    public static final String OPERATION_TASK_ID = "taskId";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;

    public String execute(CommandContext commandContext) {
        String taskId = getParameter(OPERATION_TASK_ID);
        String businessKey = getParameter(OPERATION_BUSINESS_KEY);
        String bpmProcessId = getParameter(OPERATION_BPM_PROCESS_ID);
        String userId = SpringSecurityUtils.getCurrentUserId();
        KeyValue keyValue = getKeyValue();

        if (this.notEmpty(taskId)) {
            // 如果是任务草稿，直接通过processInstanceId获得record，更新数据
            // TODO: 分支肯定有问题
            Task task = getProcessEngine().getTaskService().createTaskQuery()
                    .taskId(taskId).singleResult();

            if (task == null) {
                throw new IllegalStateException("任务不存在");
            }

            String processInstanceId = task.getProcessInstanceId();
            Record record = keyValue.findByRef(processInstanceId);

            if (record != null) {
                record = new RecordBuilder().build(record, STATUS_DRAFT_TASK,
                        getParameters());
                keyValue.save(record);
                businessKey = record.getCode();
            }
        } else if (this.notEmpty(businessKey)) {
            // 如果是流程草稿，直接通过businessKey获得record，更新数据
            Record record = keyValue.findByCode(businessKey);

            record = new RecordBuilder().build(record, STATUS_DRAFT_PROCESS,
                    getParameters());
            keyValue.save(record);
        } else {
            // 如果是第一次保存草稿，肯定是流程草稿，先初始化record，再保存数据
            Record record = new RecordBuilder().build(bpmProcessId,
                    STATUS_DRAFT_PROCESS, getParameters(), userId);
            keyValue.save(record);
            businessKey = record.getCode();
        }

        return businessKey;
    }

    public boolean notEmpty(String str) {
        return (str != null) && (!"".equals(str));
    }

    public KeyValue getKeyValue() {
        return ApplicationContextHelper.getBean(KeyValue.class);
    }
}
