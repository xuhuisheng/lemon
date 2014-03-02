package com.mossle.bridge.user;

import javax.annotation.PostConstruct;

import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class UserConnectorFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(UserConnectorFactoryBean.class);
    private UserConnector userConnector;
    private String type = "database";
    private JdbcTemplate jdbcTemplate;
    private UserCache userCache;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");

        if ("database".equals(type)) {
            processDatabase();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processDatabase() {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");

        DatabaseUserConnector databaseUserConnector = new DatabaseUserConnector();
        databaseUserConnector.setJdbcTemplate(jdbcTemplate);

        if (userCache != null) {
            logger.debug("use cache for UserConnector");

            UserConnectorWrapper userConnectorWrapper = new UserConnectorWrapper();
            userConnectorWrapper.setUserConnector(databaseUserConnector);
            userConnectorWrapper.setUserCache(userCache);
            userConnector = userConnectorWrapper;
        } else {
            userConnector = databaseUserConnector;
        }
    }

    public Object getObject() {
        return userConnector;
    }

    public Class getObjectType() {
        return UserConnector.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }
}
