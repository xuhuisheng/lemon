package com.mossle.humantask.listener;

import javax.annotation.Resource;

import com.mossle.api.delegate.DelegateConnector;

import com.mossle.humantask.persistence.domain.TaskInfo;

public class DelegateHumanTaskListener implements HumanTaskListener {
    private DelegateConnector delegateConnector;

    @Override
    public void onCreate(TaskInfo taskInfo) {
        // 自动委托
        String humanTaskId = Long.toString(taskInfo.getId());
        String assignee = taskInfo.getAssignee();
        String processDefinitionId = taskInfo.getProcessDefinitionId();
        String tenantId = taskInfo.getTenantId();
        String attorney = delegateConnector.findAttorney(assignee,
                processDefinitionId, taskInfo.getCode(), tenantId);

        if (attorney != null) {
            taskInfo.setOwner(assignee);
            taskInfo.setAssignee(attorney);

            // new DelegateTaskCmd(delegateTask.getId(), attorney).execute(Context
            // .getCommandContext());
            delegateConnector.recordDelegate(assignee, attorney, humanTaskId,
                    tenantId);
        }
    }

    @Override
    public void onComplete(TaskInfo taskInfo) throws Exception {
    }

    @Resource
    public void setDelegateConnector(DelegateConnector delegateConnector) {
        this.delegateConnector = delegateConnector;
    }
}
