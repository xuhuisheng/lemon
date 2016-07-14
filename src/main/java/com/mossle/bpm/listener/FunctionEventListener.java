package com.mossle.bpm.listener;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;

import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionEventListener implements ActivitiEventListener {
    private static Logger logger = LoggerFactory
            .getLogger(FunctionEventListener.class);
    private BpmConfListenerManager bpmConfListenerManager;

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
        case ACTIVITY_STARTED:
            this.onActivityStart((ActivitiActivityEvent) event);

            break;

        case ACTIVITY_COMPLETED:
            this.onActivityEnd((ActivitiActivityEvent) event);

            break;

        default:
            logger.debug("Event received: {}", event.getType());
        }
    }

    public void onActivityStart(ActivitiActivityEvent event) {
        logger.debug("activity start {}", event);
        this.invokeExpression(event.getProcessDefinitionId(),
                event.getActivityId(), 0);
    }

    public void onActivityEnd(ActivitiActivityEvent event) {
        logger.debug("activity end {}", event);
        this.invokeExpression(event.getProcessDefinitionId(),
                event.getActivityId(), 1);
    }

    public void invokeExpression(String processDefinitionId, String activityId,
            int type) {
        String hql = "from BpmConfListener where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=? and type=?";
        List<BpmConfListener> bpmConfListeners = bpmConfListenerManager.find(
                hql, processDefinitionId, activityId, type);

        for (BpmConfListener bpmConfListener : bpmConfListeners) {
            String expressionText = bpmConfListener.getValue();

            try {
                ExpressionManager expressionManager = Context
                        .getProcessEngineConfiguration().getExpressionManager();

                Object result = expressionManager.createExpression(
                        expressionText).getValue(
                        Context.getExecutionContext().getExecution());
                logger.info("result : {}", result);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Resource
    public void setBpmConfListenerManager(
            BpmConfListenerManager bpmConfListenerManager) {
        this.bpmConfListenerManager = bpmConfListenerManager;
    }
}
