package com.mossle.bpm.behavior.usertask;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomParallelMultiInstanceBehavior extends
        ParallelMultiInstanceBehavior {
    private static Logger log = LoggerFactory
            .getLogger(CustomParallelMultiInstanceBehavior.class);

    public CustomParallelMultiInstanceBehavior(ActivityImpl activity,
            AbstractBpmnActivityBehavior originalActivityBehavior) {
        super(activity, originalActivityBehavior);
    }

    protected void createInstances(ActivityExecution execution)
            throws Exception {
        log.info("创建多实例开始啦: {}  ", execution);
        super.createInstances(execution);
    }

    public void setCompletionConditionExpression(
            Expression completionConditionExpression) {
        log.info("你要表达式做什么用?: {}  ",
                completionConditionExpression.getExpressionText());
        super.setCompletionConditionExpression(completionConditionExpression);
    }
}
