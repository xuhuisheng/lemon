package com.mossle.bpm.cmd;

import java.lang.reflect.Method;

import org.activiti.engine.impl.bpmn.behavior.FlowNodeActivityBehavior;
import org.activiti.engine.impl.cmd.NeedsActiveExecutionCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignalStartEventCmd extends NeedsActiveExecutionCmd<Object> {
    private static Logger logger = LoggerFactory
            .getLogger(SignalStartEventCmd.class);

    public SignalStartEventCmd(String executionId) {
        super(executionId);
    }

    protected Object execute(CommandContext commandContext,
            ExecutionEntity execution) {
        try {
            FlowNodeActivityBehavior activityBehavior = (FlowNodeActivityBehavior) execution
                    .getActivity().getActivityBehavior();
            Method method = FlowNodeActivityBehavior.class.getDeclaredMethod(
                    "leave", ActivityExecution.class);
            method.setAccessible(true);
            method.invoke(activityBehavior, execution);
            method.setAccessible(false);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    protected String getSuspendedExceptionMessage() {
        return "Cannot signal an execution that is suspended";
    }
}
