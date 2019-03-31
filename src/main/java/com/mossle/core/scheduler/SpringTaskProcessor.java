package com.mossle.core.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

public class SpringTaskProcessor implements ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(SpringTaskProcessor.class);
    private boolean enabled = true;
    private Properties properties;
    private String prefix = "scheduler.";
    private TaskScheduler taskScheduler;
    private ApplicationContext applicationContext;
    private Map<String, String> map = new HashMap<String, String>();

    @PostConstruct
    public void init() {
        if (!enabled) {
            logger.info("scheduler skip");

            return;
        }

        if (properties == null) {
            logger.info("cannot find properties, skip");

            return;
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if ("scheduler.enabled".equals(key)) {
                continue;
            }

            if (!key.startsWith(prefix)) {
                continue;
            }

            String name = key.substring(prefix.length());
            map.put(name, value);
            logger.info("{} : {}", name, value);

            this.process(name, value);
        }
    }

    public void process(String jobClassName, String jobConfig) {
        try {
            Runnable runnable = (Runnable) applicationContext.getBean(Class
                    .forName(jobClassName));
            Trigger trigger = new CronTrigger(jobConfig);
            taskScheduler.schedule(runnable, trigger);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Resource
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
