package com.mossle.bpm.listener;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.IdentityLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动完成第一个任务.
 */
public class AutoCompleteFirstTaskListener extends DefaultTaskListener {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(AutoCompleteFirstTaskListener.class);
    private HumanTaskConnector humanTaskConnector;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String initiatorId = Authentication.getAuthenticatedUserId();

        if (initiatorId == null) {
            return;
        }

        String assignee = delegateTask.getAssignee();

        if (assignee == null) {
            return;
        }

        PvmActivity targetActivity = this.findFirstActivity(delegateTask
                .getProcessDefinitionId());

        if (!targetActivity.getId().equals(
                delegateTask.getExecution().getCurrentActivityId())) {
            return;
        }

        if (!initiatorId.equals(assignee)) {
            return;
        }

        logger.debug("auto complete first task : {}", delegateTask);

        for (IdentityLink identityLink : delegateTask.getCandidates()) {
            String userId = identityLink.getUserId();
            String groupId = identityLink.getGroupId();

            if (userId != null) {
                delegateTask.deleteCandidateUser(userId);
            }

            if (groupId != null) {
                delegateTask.deleteCandidateGroup(groupId);
            }
        }

        // 对提交流程的任务进行特殊处理
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(delegateTask.getId());
        humanTaskDto.setCatalog(HumanTaskConstants.CATALOG_START);
        humanTaskConnector.saveHumanTask(humanTaskDto);

        // ((TaskEntity) delegateTask).complete();
        // Context.getCommandContext().getHistoryManager().recordTaskId((TaskEntity) delegateTask);
        new CompleteTaskWithCommentCmd(delegateTask.getId(), null, "发起流程")
                .execute(Context.getCommandContext());
    }

    /**
     * 获得第一个节点.
     */
    public PvmActivity findFirstActivity(String processDefinitionId) {
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration().getProcessDefinitionCache()
                .get(processDefinitionId);

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
            logger.debug("first activity is not userTask, just skip");

            return null;
        }

        return targetActivity;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
