package com.mossle.auth.support;

import javax.annotation.PostConstruct;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.user.UserConnector;
import com.mossle.api.userauth.UserAuthCache;
import com.mossle.api.userauth.UserAuthConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class UserAuthConnectorFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(UserAuthConnectorFactoryBean.class);
    private UserAuthConnector userAuthConnector;
    private TenantConnector tenantConnector;
    private UserConnector userConnector;
    private String type = "database";
    private JdbcTemplate jdbcTemplate;
    private UserAuthCache userAuthCache;
    private String sqlFindPermissions;
    private String sqlFindRoles;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");
        Assert.notNull(userConnector, "userConnector cannot be null");
        Assert.notNull(tenantConnector, "tenantConnector cannot be null");

        if ("mock".equals(type)) {
            this.processMock();
        } else if ("database".equals(type)) {
            this.processDatabase();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processMock() {
        MockUserAuthConnector mockUserAuthConnector = new MockUserAuthConnector();
        userAuthConnector = mockUserAuthConnector;
    }

    public void processDatabase() {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");

        DatabaseUserAuthConnector databaseUserAuthConnector = new DatabaseUserAuthConnector();
        databaseUserAuthConnector.setJdbcTemplate(jdbcTemplate);
        databaseUserAuthConnector.setUserConnector(userConnector);
        databaseUserAuthConnector.setTenantConnector(tenantConnector);

        if (sqlFindPermissions != null) {
            databaseUserAuthConnector.setSqlFindPermission(sqlFindPermissions);
        }

        if (sqlFindRoles != null) {
            databaseUserAuthConnector.setSqlFindRole(sqlFindRoles);
        }

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

    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public void setUserAuthCache(UserAuthCache userAuthCache) {
        this.userAuthCache = userAuthCache;
    }

    public void setSqlFindPermission(String sqlFindPermissions) {
        this.sqlFindPermissions = sqlFindPermissions;
    }

    public void setSqlFindRole(String sqlFindRoles) {
        this.sqlFindRoles = sqlFindRoles;
    }
}
