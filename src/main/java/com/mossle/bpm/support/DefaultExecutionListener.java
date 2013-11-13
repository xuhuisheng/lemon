package com.mossle.bpm.support;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExecutionListener implements ExecutionListener {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultExecutionListener.class);

    public void notify(DelegateExecution delegateExecution) {
        String eventName = delegateExecution.getEventName();

        if ("start".equals(eventName)) {
            try {
                this.onStart(delegateExecution);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        if ("end".equals(eventName)) {
            try {
                this.onEnd(delegateExecution);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void onStart(DelegateExecution delegateExecution) throws Exception {
    }

    public void onEnd(DelegateExecution delegateExecution) throws Exception {
    }
}
