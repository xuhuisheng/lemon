package com.mossle.core.spring;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

public class ProxyTaskScheduler implements TaskScheduler, InitializingBean,
        DisposableBean {
    private static Logger logger = LoggerFactory
            .getLogger(ProxyTaskScheduler.class);
    private boolean enabled = true;
    private ThreadPoolTaskScheduler instance;
    private Properties properties;
    private Map<String, Boolean> skipMap = new HashMap<String, Boolean>();
    private String prefix = "scheduler.";

    public void afterPropertiesSet() {
        if (properties != null) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                if ("scheduler.enabled".equals(key)) {
                    continue;
                }

                if (key.startsWith(prefix)) {
                    String name = key.substring(prefix.length());
                    skipMap.put(name, Boolean.valueOf(value));
                    logger.info("{} : {}", name, skipMap.get(name));
                }
            }
        }

        if (enabled) {
            instance = new ThreadPoolTaskScheduler();
            instance.afterPropertiesSet();
        }
    }

    public void destroy() {
        if (instance != null) {
            instance.destroy();
        }
    }

    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        if (!enabled) {
            logger.debug("skip : {}", task);

            return null;
        }

        ScheduledFuture<?> future = instance.schedule(task, trigger);
        String runnableKey = findRunnableKey(task);

        if (Boolean.FALSE.equals(skipMap.get(runnableKey))) {
            future.cancel(true);
        }

        return future;
    }

    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        if (!enabled) {
            logger.debug("skip : {}", task);

            return null;
        }

        ScheduledFuture<?> future = instance.schedule(task, startTime);
        String runnableKey = findRunnableKey(task);

        if (Boolean.FALSE.equals(skipMap.get(runnableKey))) {
            future.cancel(true);
        }

        return future;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task,
            Date startTime, long period) {
        if (!enabled) {
            logger.debug("skip : {}", task);

            return null;
        }

        ScheduledFuture<?> future = instance.scheduleAtFixedRate(task,
                startTime, period);
        String runnableKey = findRunnableKey(task);

        if (Boolean.FALSE.equals(skipMap.get(runnableKey))) {
            future.cancel(true);
        }

        return future;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        if (!enabled) {
            logger.debug("skip : {}", task);

            return null;
        }

        ScheduledFuture<?> future = instance.scheduleAtFixedRate(task, period);
        String runnableKey = findRunnableKey(task);

        if (Boolean.FALSE.equals(skipMap.get(runnableKey))) {
            future.cancel(true);
        }

        return future;
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task,
            Date startTime, long delay) {
        if (!enabled) {
            logger.debug("skip : {}", task);

            return null;
        }

        ScheduledFuture<?> future = instance.scheduleWithFixedDelay(task,
                startTime, delay);
        String runnableKey = findRunnableKey(task);

        if (Boolean.FALSE.equals(skipMap.get(runnableKey))) {
            future.cancel(true);
        }

        return future;
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        if (!enabled) {
            logger.debug("skip : {}", task);

            return null;
        }

        ScheduledFuture<?> future = instance
                .scheduleWithFixedDelay(task, delay);
        String runnableKey = findRunnableKey(task);

        if (Boolean.FALSE.equals(skipMap.get(runnableKey))) {
            future.cancel(true);
        }

        return future;
    }

    public String findRunnableKey(Runnable runnable) {
        logger.info("findRunnableKey : {}", runnable);

        if (runnable instanceof ScheduledMethodRunnable) {
            ScheduledMethodRunnable scheduledMethodRunnable = (ScheduledMethodRunnable) runnable;
            Method method = scheduledMethodRunnable.getMethod();
            Class clz = method.getDeclaringClass();

            logger.info("{}.{}", clz.getCanonicalName(), method.getName());

            return clz.getCanonicalName() + "." + method.getName();
        } else {
            return runnable.toString();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
