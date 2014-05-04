package com.mossle.bridge.userauth;

import javax.annotation.PostConstruct;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.user.UserConnector;
import com.mossle.api.userauth.UserAuthCache;
import com.mossle.api.userauth.UserAuthConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class UserAuthConnectorFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(UserAuthConnectorFactoryBean.class);
    private UserAuthConnector userAuthConnector;
    private ScopeConnector scopeConnector;
    private UserConnector userConnector;
    private String type = "database";
    private JdbcTemplate jdbcTemplate;
    private UserAuthCache userAuthCache;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");
        Assert.notNull(userConnector, "userConnector cannot be null");
        Assert.notNull(scopeConnector, "scopeConnector cannot be null");

        if ("database".equals(type)) {
            processDatabase();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processDatabase() {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");

        DatabaseUserAuthConnector databaseUserAuthConnector = new DatabaseUserAuthConnector();
        databaseUserAuthConnector.setJdbcTemplate(jdbcTemplate);
        databaseUserAuthConnector.setUserConnector(userConnector);
        databaseUserAuthConnector.setScopeConnector(scopeConnector);

        if (userAuthCache != null) {
            logger.debug("use cache for UserAuthConnector");

            UserAuthConnectorWrapper userAuthConnectorWrapper = new UserAuthConnectorWrapper();
            userAuthConnectorWrapper
                    .setUserAuthConnector(databaseUserAuthConnector);

            userAuthConnectorWrapper.setUserAuthCache(userAuthCache);
            userAuthConnector = userAuthConnectorWrapper;
        } else {
            userAuthConnector = databaseUserAuthConnector;
        }
    }

    public Object getObject() {
        return userAuthConnector;
    }

    public Class getObjectType() {
        return UserAuthConnector.class;
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

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public void setUserAuthCache(UserAuthCache userAuthCache) {
        this.userAuthCache = userAuthCache;
    }
}
