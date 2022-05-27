package com.mossle.api.database.spring;

import java.io.IOException;

import java.util.Collection;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.mossle.api.database.Database;
import com.mossle.api.database.support.DatabaseContext;

import com.mossle.core.util.BeanUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;

/**
 * spring构造Database.
 * 
 * @author Lingo
 */
public class DatabaseFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseFactoryBean.class);
    private DatabaseContext databaseContext;
    private Properties properties;
    private String defaultPrefix = "db";

    @PostConstruct
    public void init() {
        databaseContext = DatabaseContext.getInstance();
        databaseContext.setProperties(properties);
        databaseContext.setDefaultPrefix(defaultPrefix);
        databaseContext.init();
    }

    @PreDestroy
    public void close() {
        databaseContext.close();
        databaseContext = null;
    }

    // ~ ======================================================================
    public Object getObject() {
        return databaseContext.getDatabase();
    }

    public Class getObjectType() {
        return Database.class;
    }

    public boolean isSingleton() {
        return true;
    }

    // ~ ======================================================================
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setDefaultPrefix(String defaultPrefix) {
        if (StringUtils.isEmpty(defaultPrefix)) {
            logger.info("defaultPrefix cannot be null or empty");

            return;
        }

        this.defaultPrefix = defaultPrefix;
    }
}
