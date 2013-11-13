package com.mossle.bpm.behavior;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cfg.SpringBeanFactoryProxyMap;

public class ProcessEngineBeanFactory {
    private static SpringBeanFactoryProxyMap beanFactory;

    static {
        if (beanFactory == null) {
            ProcessEngineImpl processEngine = (ProcessEngineImpl) ProcessEngines
                    .getDefaultProcessEngine();
            beanFactory = (SpringBeanFactoryProxyMap) processEngine
                    .getProcessEngineConfiguration().getBeans();
        }
    }

    public static <T> T getBean(String beanName) {
        return (T) beanFactory.get(beanName);
    }
}
