package com.mossle.bpm.support;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

public class DelegateEventListener implements ActivitiEventListener {
    private List<ActivitiEventListener> listeners = new ArrayList<ActivitiEventListener>();

    public void onEvent(ActivitiEvent event) {
        for (ActivitiEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public boolean isFailOnException() {
        return false;
    }

    public void setListeners(List<ActivitiEventListener> listeners) {
        this.listeners = listeners;
    }
}
