package com.mossle.api.database.support;

import java.io.IOException;

import java.util.Collection;
import java.util.Properties;

import com.mossle.api.database.Database;
import com.mossle.api.database.DatabaseBuilder;

import com.mossle.core.util.BeanUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;

/**
 * Database环境.
 * 
 * @author Lingo
 */
public class DatabaseContext {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseContext.class);
    private static DatabaseContext instance = new DatabaseContext();
    private Properties properties;
    private String defaultPrefix = "db";
    private Database database;

    public static DatabaseContext getInstance() {
        return instance;
    }

    public synchronized void init() {
        if (database != null) {
            return;
        }

        database = DatabaseBuilder.newBuilder().setProperties(properties)
                .setDefaultPrefix(defaultPrefix).build();
    }

    public synchronized void close() {
        if (database == null) {
            return;
        }

        try {
            database.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        database = null;
    }

    public Database getDatabase() {
        return database;
    }

    // ~
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
