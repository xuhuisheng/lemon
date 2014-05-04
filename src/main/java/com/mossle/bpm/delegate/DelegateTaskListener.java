package com.mossle.bpm.delegate;

import javax.annotation.Resource;

import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;

public class DelegateTaskListener extends DefaultTaskListener {
    private DelegateService delegateService;

    @Override
    public void onAssignment(DelegateTask delegateTask) throws Exception {
        String assignee = delegateTask.getAssignee();
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        DelegateInfo delegateInfo = delegateService.getDelegateInfo(assignee,
                processDefinitionId);

        if (delegateInfo == null) {
            return;
        }

        String attorney = delegateInfo.getAttorney();
        delegateTask.setAssignee(attorney);
        delegateService.saveRecord(assignee, attorney, delegateTask.getId());
    }

    @Resource
    public void setDelegateService(DelegateService delegateService) {
        this.delegateService = delegateService;
    }
}
