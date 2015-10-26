package com.mossle.bpm.support;

import org.activiti.engine.delegate.DelegateTask;

public class DelegateTaskHolder {
    private static ThreadLocal<DelegateTask> threadLocal = new ThreadLocal<DelegateTask>();

    public static DelegateTask getDelegateTask() {
        return threadLocal.get();
    }

    public static void setDelegateTask(DelegateTask delegateTask) {
        threadLocal.set(delegateTask);
    }

    public static void clear() {
        threadLocal.remove();
    }
}
