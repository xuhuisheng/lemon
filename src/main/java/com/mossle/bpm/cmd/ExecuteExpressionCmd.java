package com.mossle.bpm.cmd;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.bpm.support.MockVariableScope;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行表达式.
 */
public class ExecuteExpressionCmd implements Command<Void> {
    private static Logger logger = LoggerFactory
            .getLogger(ExecuteExpressionCmd.class);
    private String expressionText;
    private int eventCode;
    private String eventName;
    private ModelInfoDTO modelInfo;
    private String userId;
    private String activityId;
    private String activityName;

    public ExecuteExpressionCmd(String expressionText, int eventCode,
            String eventName, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) {
        this.expressionText = expressionText;
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.modelInfo = modelInfo;
        this.userId = userId;
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public Void execute(CommandContext commandContext) {
        try {
            ExpressionManager expressionManager = Context
                    .getProcessEngineConfiguration().getExpressionManager();
            MockVariableScope mockVariableScope = new MockVariableScope(
                    eventCode, eventName, modelInfo, userId, activityId,
                    activityName);
            Object result = expressionManager.createExpression(expressionText)
                    .getValue(mockVariableScope);
            logger.info("result : {}", result);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }
}
