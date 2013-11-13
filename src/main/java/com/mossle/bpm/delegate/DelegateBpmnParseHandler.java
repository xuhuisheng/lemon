package com.mossle.bpm.delegate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.UserTask;

import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.parse.BpmnParseHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegateBpmnParseHandler implements BpmnParseHandler {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateBpmnParseHandler.class);

    public void parse(BpmnParse bpmnParse, BaseElement baseElement) {
        if (!(baseElement instanceof UserTask)) {
            return;
        }

        UserTask userTask = (UserTask) baseElement;
        logger.info("bpmnParse : {}, userTask : {}", bpmnParse, userTask);

        TaskDefinition taskDefinition = (TaskDefinition) bpmnParse
                .getCurrentActivity().getProperty(
                        UserTaskParseHandler.PROPERTY_TASK_DEFINITION);

        ActivitiListener activitiListener = new ActivitiListener();
        activitiListener.setEvent(TaskListener.EVENTNAME_ASSIGNMENT);
        activitiListener
                .setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        activitiListener.setImplementation("#{delegateTaskListener}");
        taskDefinition
                .addTaskListener(TaskListener.EVENTNAME_ASSIGNMENT, bpmnParse
                        .getListenerFactory()
                        .createDelegateExpressionTaskListener(activitiListener));

        // candidateuser 或者group 包含被代理人, 则添加代理人为candidateuser
        ActivitiListener delegateActivitiCandidateListener = new ActivitiListener();
        delegateActivitiCandidateListener
                .setEvent(TaskListener.EVENTNAME_CREATE);
        delegateActivitiCandidateListener
                .setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        delegateActivitiCandidateListener
                .setImplementation("#{delegateTaskCreateListener}");
        taskDefinition.addTaskListener(
                TaskListener.EVENTNAME_CREATE,
                bpmnParse.getListenerFactory()
                        .createDelegateExpressionTaskListener(
                                delegateActivitiCandidateListener));
    }

    public Collection<Class<? extends BaseElement>> getHandledTypes() {
        List types = Collections.singletonList(UserTask.class);

        return types;
    }
}
