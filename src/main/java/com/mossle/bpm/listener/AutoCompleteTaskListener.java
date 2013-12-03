package com.mossle.bpm.listener;

import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import org.springframework.stereotype.Component;

@Component("autoCompleteTaskListener")
public class AutoCompleteTaskListener extends DefaultTaskListener {
    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String username = Authentication.getAuthenticatedUserId();
        String assignee = delegateTask.getAssignee();

        if ((username != null) && username.equals(assignee)) {
            ((TaskEntity) delegateTask).complete();
        }
    }
}
