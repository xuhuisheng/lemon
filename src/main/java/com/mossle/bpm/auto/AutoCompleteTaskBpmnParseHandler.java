package com.mossle.bpm.auto;

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

public class AutoCompleteTaskBpmnParseHandler implements BpmnParseHandler {
    private static Logger logger = LoggerFactory
            .getLogger(AutoCompleteTaskBpmnParseHandler.class);

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
        activitiListener.setImplementation("#{autoCompleteTaskListener}");
        taskDefinition
                .addTaskListener(TaskListener.EVENTNAME_CREATE, bpmnParse
                        .getListenerFactory()
                        .createDelegateExpressionTaskListener(activitiListener));
    }

    public Collection<Class<? extends BaseElement>> getHandledTypes() {
        List types = Collections.singletonList(UserTask.class);

        return types;
    }
}
