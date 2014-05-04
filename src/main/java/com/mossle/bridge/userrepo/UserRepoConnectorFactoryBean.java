package com.mossle.bridge.userrepo;

import javax.annotation.PostConstruct;

import com.mossle.api.userrepo.UserRepoCache;
import com.mossle.api.userrepo.UserRepoConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class UserRepoConnectorFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(UserRepoConnectorFactoryBean.class);
    private UserRepoConnector userRepoConnector;
    private String type = "database";
    private JdbcTemplate jdbcTemplate;
    private UserRepoCache userRepoCache;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");

        if ("database".equals(type)) {
            processDatabase();
        } else if ("memory".equals(type)) {
            processMemory();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processDatabase() {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");

        DatabaseUserRepoConnector databaseUserRepoConnector = new DatabaseUserRepoConnector();
        databaseUserRepoConnector.setJdbcTemplate(jdbcTemplate);

        if (userRepoCache != null) {
            logger.debug("use cache for UserRepoConnector");

            UserRepoConnectorWrapper userRepoConnectorWrapper = new UserRepoConnectorWrapper();
            userRepoConnectorWrapper
                    .setUserRepoConnector(databaseUserRepoConnector);

            userRepoConnectorWrapper.setUserRepoCache(userRepoCache);
            userRepoConnector = userRepoConnectorWrapper;
        } else {
            userRepoConnector = databaseUserRepoConnector;
        }
    }

    public void processMemory() {
        userRepoConnector = new MemoryUserRepoConnector();
    }

    public Object getObject() {
        return userRepoConnector;
    }

    public Class getObjectType() {
        return UserRepoConnector.class;
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

    public void setUserRepoCache(UserRepoCache userRepoCache) {
        this.userRepoCache = userRepoCache;
    }
}
