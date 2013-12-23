package com.mossle.core.spring;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 在ApplicationContext环境外获取bean的工具类.
 */
public class ApplicationContextHelper implements ApplicationContextAware {
    /**
     * 向ApplicationContextHolder里设置ApplicationContext.
     * 
     * @param applicationContext
     *            applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ApplicationContextHolder.getInstance().setApplicationContext(
                applicationContext);
    }

    /**
     * 获得ApplicationContext.
     * 
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return ApplicationContextHolder.getInstance().getApplicationContext();
    }

    /**
     * 根据class获得bean.
     * 
     * @param clz
     *            Class
     * @return T
     */
    public static <T> T getBean(Class<T> clz) {
        return ApplicationContextHolder.getInstance().getApplicationContext()
                .getBean(clz);
    }

    /**
     * 根据id获得bean.
     * 
     * @param id
     *            String
     * @return T
     */
    public static <T> T getBean(String id) {
        return (T) ApplicationContextHolder.getInstance()
                .getApplicationContext().getBean(id);
    }
}
