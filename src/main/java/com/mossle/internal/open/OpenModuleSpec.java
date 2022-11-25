package com.mossle.internal.open;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

/**
 * <ul>
 * <li>dbmigrate
 * <li>
 * <li>data</li>
 * <li>scheduler register</li>
 * <li>rpc register</li>
 * <li>rpc discover</li>
 * </ul>
 */
@Component
public class OpenModuleSpec {
    private static Logger logger = LoggerFactory
            .getLogger(OpenModuleSpec.class);
    public static final String MODULE_NAME = "open";
    private boolean enable = true;
    private boolean dbmigrate = true;

    @PostConstruct
    public void init() {
        if (!enable) {
            logger.info("skip : {}", MODULE_NAME);

            return;
        }
    }

    public boolean isEnable() {
        return enable;
    }

    @Value("${module." + MODULE_NAME + ".enable:true}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDbmigrate() {
        return dbmigrate;
    }

    @Value("${module." + MODULE_NAME + ".dbmigrate:true}")
    public void setDbmigrate(boolean dbmigrate) {
        this.dbmigrate = dbmigrate;
    }
}
