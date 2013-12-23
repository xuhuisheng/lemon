package com.mossle.core.spring;

import org.springframework.context.ApplicationContext;

/**
 * 保存ApplicationContext的单例.
 */
public class ApplicationContextHolder {
    /** instance. */
    private static ApplicationContextHolder instance = new ApplicationContextHolder();

    /** ApplicationContext. */
    private ApplicationContext applicationContext;

    /**
     * get ApplicationContext.
     * 
     * @return ApplicationContext
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * set ApplicationContext.
     * 
     * @param applicationContext
     *            ApplicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * get instance.
     * 
     * @return ApplicationContextHolder
     */
    public static ApplicationContextHolder getInstance() {
        return instance;
    }
}
