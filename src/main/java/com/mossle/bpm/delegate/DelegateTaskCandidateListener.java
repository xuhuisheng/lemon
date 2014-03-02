package com.mossle.bpm.delegate;

import java.util.Set;

import javax.annotation.Resource;

import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.task.IdentityLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegateTaskCandidateListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateTaskCandidateListener.class);
    private IdentityService identityService;
    private DelegateService delegateService;

    public void onCreate(DelegateTask delegateTask) throws Exception {
        String assignee = delegateTask.getAssignee();
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        DelegateInfo delegateInfo = delegateService.getDelegateInfo(assignee,
                processDefinitionId);

        if (delegateInfo == null) {
            return;
        }

        String attorney = delegateInfo.getAttorney();
        Set<IdentityLink> ids = delegateTask.getCandidates();

        for (IdentityLink identityLink : ids) {
            if (this.containsUser(identityLink, assignee)
                    || this.containsGroup(identityLink, assignee)) {
                this.addCandidateUser(delegateTask, assignee, attorney);
            }
        }
    }

    private boolean containsUser(IdentityLink identityLink, String assignee) {
        return (identityLink.getUserId() != null)
                && identityLink.getUserId().equals(assignee);
    }

    private boolean containsGroup(IdentityLink identityLink, String assignee) {
        return (identityLink.getGroupId() != null)
                && (identityService.createGroupQuery().groupMember(assignee)
                        .groupId(identityLink.getGroupId()).count() > 0);
    }

    private void addCandidateUser(DelegateTask delegateTask, String assignee,
            String attorney) {
        logger.info("自动委托任务,设置候选人: {} to {}", delegateTask, attorney);
        delegateTask.addCandidateUser(attorney);
        delegateService.saveRecord(assignee, attorney, delegateTask.getId());
    }

    @Resource
    public void setDelegateService(DelegateService delegateService) {
        this.delegateService = delegateService;
    }

    @Resource
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }
}
