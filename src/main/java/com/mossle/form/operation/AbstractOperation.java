package com.mossle.form.operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

import org.springframework.util.Assert;

public abstract class AbstractOperation<T> implements Operation<T>, Command<T> {
    private Map<String, String[]> parameters;

    public T execute(Map<String, String[]> parameters) {
        this.parameters = parameters;

        ProcessEngine processEngine = getProcessEngine();

        return processEngine.getManagementService().executeCommand(this);
    }

    public ProcessEngine getProcessEngine() {
        return ApplicationContextHelper.getBean(ProcessEngine.class);
    }

    public String getParameter(String name) {
        String[] value = parameters.get(name);

        if ((value == null) || (value.length == 0)) {
            return null;
        }

        return value[0];
    }

    public List<String> getParameterValues(String name) {
        String[] value = parameters.get(name);

        if ((value == null) || (value.length == 0)) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.asList(value);
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public Map<String, Object> getVariables() {
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
}
