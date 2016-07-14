package com.mossle.bpm.listener;

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

public class ProxyUserTaskBpmnParseHandler implements BpmnParseHandler {
    private static Logger logger = LoggerFactory
            .getLogger(ProxyUserTaskBpmnParseHandler.class);
    private String taskListenerId;
    private boolean useDefaultUserTaskParser;

    public void parse(BpmnParse bpmnParse, BaseElement baseElement) {
        if (!(baseElement instanceof UserTask)) {
            return;
        }

        if (useDefaultUserTaskParser) {
            new UserTaskParseHandler().parse(bpmnParse, baseElement);
        }

        UserTask userTask = (UserTask) baseElement;
        logger.debug("bpmnParse : {}, userTask : {}", bpmnParse, userTask);

        TaskDefinition taskDefinition = (TaskDefinition) bpmnParse
                .getCurrentActivity().getProperty(
                        UserTaskParseHandler.PROPERTY_TASK_DEFINITION);

        this.configEvent(taskDefinition, bpmnParse,
                TaskListener.EVENTNAME_CREATE);
        this.configEvent(taskDefinition, bpmnParse,
                TaskListener.EVENTNAME_ASSIGNMENT);
        this.configEvent(taskDefinition, bpmnParse,
                TaskListener.EVENTNAME_COMPLETE);
        this.configEvent(taskDefinition, bpmnParse,
                TaskListener.EVENTNAME_DELETE);
    }

    public void configEvent(TaskDefinition taskDefinition, BpmnParse bpmnParse,
            String eventName) {
        ActivitiListener activitiListener = new ActivitiListener();
        activitiListener.setEvent(eventName);
        activitiListener
                .setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        activitiListener.setImplementation("#{" + taskListenerId + "}");
        taskDefinition
                .addTaskListener(eventName, bpmnParse.getListenerFactory()
                        .createDelegateExpressionTaskListener(activitiListener));
    }

    public Collection<Class<? extends BaseElement>> getHandledTypes() {
        List types = Collections.singletonList(UserTask.class);

        return types;
    }

    public void setTaskListenerId(String taskListenerId) {
        this.taskListenerId = taskListenerId;
    }

    public void setUseDefaultUserTaskParser(boolean useDefaultUserTaskParser) {
        this.useDefaultUserTaskParser = useDefaultUserTaskParser;
    }
}
