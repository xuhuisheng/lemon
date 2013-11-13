package com.mossle.bpm.behavior;

import com.mossle.bpm.behavior.usertask.CustomParallelMultiInstanceBehavior;
import com.mossle.bpm.behavior.usertask.CustomUserTaskActivityBehavior;

import org.activiti.bpmn.model.UserTask;

import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomActivityBehaviorFactory extends
        DefaultActivityBehaviorFactory {
    private static Logger log = LoggerFactory
            .getLogger(CustomUserTaskActivityBehavior.class);

    // test
    public UserTaskActivityBehavior createUserTaskActivityBehavior(
            UserTask userTask, TaskDefinition taskDefinition) {
        log.info("change usertask Behavior : {}  ", userTask);

        return new CustomUserTaskActivityBehavior(taskDefinition);
    }

    // test multiInstance
    public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(
            ActivityImpl activity,
            AbstractBpmnActivityBehavior innerActivityBehavior) {
        return new CustomParallelMultiInstanceBehavior(activity,
                innerActivityBehavior);
    }

    // 同样可以覆盖别的方法,加入其他元素的自定义行为,参考 @see ActivityBehaviorFactory
    // 该类控制执行到某一元素时触发
}
