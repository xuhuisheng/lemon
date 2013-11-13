package com.mossle.bpm.parser;

import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

public class CustomBpmnParser extends BpmnParser {
    // 当使用自定义activityBehaviorFactory 时，需要重写该方法
    public void setActivityBehaviorFactory(
            ActivityBehaviorFactory activityBehaviorFactory) {
        ((DefaultActivityBehaviorFactory) activityBehaviorFactory)
                .setExpressionManager(expressionManager);
        super.setActivityBehaviorFactory(activityBehaviorFactory);
    }
}
