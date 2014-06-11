package com.mossle.form.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.bpm.FormInfo;
import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.FindStartFormCmd;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.ApplicationContextHelper;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.manager.FormTemplateManager;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

/**
 * 发起流程.
 */
public class StartProcessOperation extends AbstractOperation<Void> {
    private static Logger logger = LoggerFactory
            .getLogger(StartProcessOperation.class);
    public static final String OPERATION_TASK_ID = "taskId";
    public static final String OPERATION_BPM_PROCESS_ID = "bpmProcessId";
    public static final String OPERATION_COMMENT = "完成";
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private JsonMapper jsonMapper = new JsonMapper();

    public Map<String, String> fetchFormTypeMap(String content) {
        logger.debug("content : {}", content);

        Map map = jsonMapper.fromJson(content, Map.class);
        logger.debug("map : {}", map);

        List<Map> sections = (List<Map>) map.get("sections");
        logger.debug("sections : {}", sections);

        Map<String, String> formTypeMap = new HashMap<String, String>();

        for (Map section : sections) {
            if (!"grid".equals(section.get("type"))) {
                continue;
            }

            List<Map> fields = (List<Map>) section.get("fields");

            for (Map field : fields) {
                formTypeMap.put((String) field.get("name"),
                        (String) field.get("type"));
            }
        }

        return formTypeMap;
    }

    public Void execute(CommandContext commandContext) {
        ProcessEngine processEngine = getProcessEngine();
        FormTemplateManager formTemplateManager = getFormTemplateManager();
        KeyValue keyValue = getKeyValue();
        String bpmProcessId = getParameter(OPERATION_BPM_PROCESS_ID);
        BpmProcess bpmProcess = getBpmProcessManager().get(
                Long.parseLong(bpmProcessId));
        String processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();

        // 先保存草稿
        String businessKey = new ConfAssigneeOperation()
                .execute(getParameters());

        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUserId());

        // 获得form的信息
        FormInfo formInfo = new FindStartFormCmd(processDefinitionId)
                .execute(commandContext);

        // 尝试根据表单里字段的类型，进行转换
        Map<String, String> formTypeMap = new HashMap<String, String>();

        if (formInfo.isFormExists()) {
            FormTemplate formTemplate = formTemplateManager.get(Long
                    .parseLong(formInfo.getFormKey()));

            String content = formTemplate.getContent();
            formTypeMap = this.fetchFormTypeMap(content);
        }

        Record record = keyValue.findByCode(businessKey);

        Map<String, Object> processParameters = new HashMap<String, Object>();

        // 如果有表单，就从数据库获取数据
        for (Prop prop : record.getProps().values()) {
            String key = prop.getCode();
            String value = prop.getValue();
            String formType = this.getFormType(formTypeMap, key);

            if ("userpicker".equals(formType)) {
                processParameters.put(key,
                        new ArrayList(Arrays.asList(value.split(","))));
            } else if (formType != null) {
                processParameters.put(key, value);
            }
        }

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId, businessKey,
                        processParameters);
        record = new RecordBuilder().build(record, STATUS_RUNNING,
                processInstance.getId());
        keyValue.save(record);

        return null;
    }

    /**
     * 工具方法，获取表单的类型.
     */
    private String getFormType(Map<String, String> formTypeMap, String name) {
        if (formTypeMap.containsKey(name)) {
            return formTypeMap.get(name);
        } else {
            return null;
        }
    }

    public FormTemplateManager getFormTemplateManager() {
        return ApplicationContextHelper.getBean(FormTemplateManager.class);
    }

    public KeyValue getKeyValue() {
        return ApplicationContextHelper.getBean(KeyValue.class);
    }

    public BpmTaskConfManager getBpmTaskConfManager() {
        return ApplicationContextHelper.getBean(BpmTaskConfManager.class);
    }

    public BpmProcessManager getBpmProcessManager() {
        return ApplicationContextHelper.getBean(BpmProcessManager.class);
    }
}
