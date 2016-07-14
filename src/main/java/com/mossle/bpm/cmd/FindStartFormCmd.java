package com.mossle.bpm.cmd;

import com.mossle.api.form.FormDTO;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.form.DefaultFormHandler;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查找发起流程表单的逻辑.
 * 
 * 如果startEvent有formKey，直接返回。 如果startEvent后续的第一个节点是userTask，并且userTask的负责人是流程发起人，也返回它的formKey。
 */
public class FindStartFormCmd implements Command<FormDTO> {
    private static Logger logger = LoggerFactory
            .getLogger(FindStartFormCmd.class);
    private String processDefinitionId;

    public FindStartFormCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public FormDTO execute(CommandContext commandContext) {
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration().getDeploymentManager()
                .findDeployedProcessDefinitionById(processDefinitionId);

        if (processDefinitionEntity == null) {
            throw new IllegalArgumentException(
                    "cannot find processDefinition : " + processDefinitionId);
        }

        FormDTO formDto = new FormDTO();
        formDto.setProcessDefinitionId(processDefinitionId);

        // startEvent存在formKey的情况
        if (processDefinitionEntity.hasStartFormKey()) {
            formDto.setAutoCompleteFirstTask(false);

            DefaultFormHandler formHandler = (DefaultFormHandler) processDefinitionEntity
                    .getStartFormHandler();

            if (formHandler.getFormKey() == null) {
                // 这个逻辑很古怪，上面判断了hasStartFormKey应该就避免这里为null的情况
                logger.info("weired start form key is null");

                return formDto;
            }

            String formKey = formHandler.getFormKey().getExpressionText();
            formDto.setCode(formKey);
            formDto.setActivityId(processDefinitionEntity.getInitial().getId());

            return formDto;
        }

        formDto.setAutoCompleteFirstTask(true);

        ActivityImpl startActivity = processDefinitionEntity.getInitial();

        if (startActivity.getOutgoingTransitions().size() != 1) {
            throw new IllegalStateException(
                    "start activity outgoing transitions cannot more than 1, now is : "
                            + startActivity.getOutgoingTransitions().size());
        }

        PvmTransition pvmTransition = startActivity.getOutgoingTransitions()
                .get(0);
        PvmActivity targetActivity = pvmTransition.getDestination();

        if (!"userTask".equals(targetActivity.getProperty("type"))) {
            logger.info("first activity is not userTask, just skip");

            return formDto;
        }

        String taskDefinitionKey = targetActivity.getId();
        logger.debug("activityId : {}", targetActivity.getId());

        TaskDefinition taskDefinition = processDefinitionEntity
                .getTaskDefinitions().get(taskDefinitionKey);

        Expression expression = taskDefinition.getAssigneeExpression();

        if (expression == null) {
            logger.info("assignee is null, just skip");

            return formDto;
        }

        String expressionText = expression.getExpressionText();
        logger.debug("{}", expressionText);
        logger.debug("{}", startActivity.getProperties());
        logger.debug("{}", processDefinitionEntity.getProperties());

        String initiatorVariableName = (String) processDefinitionEntity
                .getProperty(BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME);

        if (!("${" + initiatorVariableName + "}").equals(expressionText)) {
            logger.info("the assignee of {} is not {}, just skip",
                    taskDefinitionKey, "${" + initiatorVariableName + "}");

            return formDto;
        }

        DefaultFormHandler formHandler = (DefaultFormHandler) taskDefinition
                .getTaskFormHandler();

        if (formHandler.getFormKey() == null) {
            // 满足一切要求，但是xml里没配置formKey，再给一次机会，去上层搜索一遍数据库配置里是不是配置了这个userTask的formKey
            logger.info("cannot find formKey : {}, {}", processDefinitionId,
                    taskDefinitionKey);
            formDto.setActivityId(taskDefinitionKey);

            return formDto;
        }

        String formKey = formHandler.getFormKey().getExpressionText();
        formDto.setCode(formKey);

        formDto.setActivityId(taskDefinitionKey);

        return formDto;
    }
}
