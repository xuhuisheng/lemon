package com.mossle.core.jersey;

import java.util.Map;

import javax.ws.rs.Path;

import com.mossle.core.spring.ApplicationContextHelper;

import org.glassfish.jersey.server.ResourceConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

public class SpringApplication extends ResourceConfig {
    private static Logger logger = LoggerFactory
            .getLogger(SpringApplication.class);

    public SpringApplication() {
        init();
    }

    public void init() {
        ApplicationContext ctx = ApplicationContextHelper
                .getApplicationContext();
        Map<String, Object> map = ctx.getBeansWithAnnotation(Path.class);

        for (Object item : map.values()) {
            Class clz = item.getClass();
            logger.info("register : {}", clz);
            register(clz);
        }
    }
}
