package com.mossle.user.support;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserSyncConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class UserConnectorFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(UserConnectorFactoryBean.class);
    private UserConnector userConnector;
    private String type = "database";
    private JdbcTemplate jdbcTemplate;
    private UserCache userCache;
    private UserSyncConnector userSyncConnector;
    private String sqlFindById;
    private String sqlFindByUsername;
    private String sqlFindByRef;
    private String sqlPagedQueryCount;
    private String sqlPagedQuerySelect;
    private Map<String, String> aliasMap;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");

        if ("mock".equals(type)) {
            this.processMock();
        } else if ("database".equals(type)) {
            this.processDatabase();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processMock() {
        MockUserConnector mockUserConnector = new MockUserConnector();
        userConnector = mockUserConnector;
    }

    public void processDatabase() {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");

        DatabaseUserConnector databaseUserConnector = new DatabaseUserConnector();
        databaseUserConnector.setJdbcTemplate(jdbcTemplate);
        databaseUserConnector.setUserSyncConnector(userSyncConnector);

        if (sqlFindById != null) {
            databaseUserConnector.setSqlFindById(sqlFindById);
        }

        if (sqlFindByUsername != null) {
            databaseUserConnector.setSqlFindByUsername(sqlFindByUsername);
        }

        if (sqlFindByRef != null) {
            databaseUserConnector.setSqlFindByRef(sqlFindByRef);
        }

        if (aliasMap != null) {
            databaseUserConnector.setAliasMap(aliasMap);
        }

        if (sqlPagedQuerySelect != null) {
            databaseUserConnector.setSqlPagedQuerySelect(sqlPagedQuerySelect);
        }

        if (sqlPagedQueryCount != null) {
            databaseUserConnector.setSqlPagedQueryCount(sqlPagedQueryCount);
        }

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

    public void setUserSyncConnector(UserSyncConnector userSyncConnector) {
        this.userSyncConnector = userSyncConnector;
    }

    public void setSqlFindById(String sqlFindById) {
        this.sqlFindById = sqlFindById;
    }

    public void setSqlFindByUsername(String sqlFindByUsername) {
        this.sqlFindByUsername = sqlFindByUsername;
    }

    public void setSqlFindByRef(String sqlFindByRef) {
        this.sqlFindByRef = sqlFindByRef;
    }

    public void setAliasMap(Map<String, String> aliasMap) {
        this.aliasMap = aliasMap;
    }

    public void setSqlPagedQuerySelect(String sqlPagedQuerySelect) {
        this.sqlPagedQuerySelect = sqlPagedQuerySelect;
    }

    public void setSqlPagedQueryCount(String sqlPagedQueryCount) {
        this.sqlPagedQueryCount = sqlPagedQueryCount;
    }
}
