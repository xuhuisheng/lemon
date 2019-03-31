package com.mossle.bpm.support;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringEventListener implements ActivitiEventListener,
        ApplicationContextAware {
    private ApplicationContext applicationContext;

    public void onEvent(ActivitiEvent event) {
        DelegateEventListener delegateEventListener = applicationContext
                .getBean(DelegateEventListener.class);
        delegateEventListener.onEvent(event);
    }

    public boolean isFailOnException() {
        return false;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
