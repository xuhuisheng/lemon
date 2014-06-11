package com.mossle.bridge.scope;

import javax.annotation.PostConstruct;

import com.mossle.api.scope.ScopeCache;
import com.mossle.api.scope.ScopeConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class ScopeConnectorFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(ScopeConnectorFactoryBean.class);
    private ScopeConnector scopeConnector;
    private String type = "database";
    private JdbcTemplate jdbcTemplate;
    private ScopeCache scopeCache;
    private String sqlFindById;
    private String sqlFindByCode;
    private String sqlFindByRef;
    private String sqlFindAll;
    private String sqlFindSharedScopes;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");

        if ("mock".equals(type)) {
            processMock();
        } else if ("database".equals(type)) {
            processDatabase();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processMock() {
        MockScopeConnector mockScopeConnector = new MockScopeConnector();
        scopeConnector = mockScopeConnector;
    }

    public void processDatabase() {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");

        DatabaseScopeConnector databaseScopeConnector = new DatabaseScopeConnector();
        databaseScopeConnector.setJdbcTemplate(jdbcTemplate);

        if (sqlFindById != null) {
            databaseScopeConnector.setSqlFindById(sqlFindById);
        }

        if (sqlFindByCode != null) {
            databaseScopeConnector.setSqlFindByCode(sqlFindByCode);
        }

        if (sqlFindByRef != null) {
            databaseScopeConnector.setSqlFindByRef(sqlFindByRef);
        }

        if (sqlFindAll != null) {
            databaseScopeConnector.setSqlFindAll(sqlFindAll);
        }

        if (sqlFindSharedScopes != null) {
            databaseScopeConnector.setSqlFindSharedScopes(sqlFindSharedScopes);
        }

        if (scopeCache != null) {
            logger.debug("use cache for ScopeConnector");

            ScopeConnectorWrapper scopeConnectorWrapper = new ScopeConnectorWrapper();
            scopeConnectorWrapper.setScopeConnector(databaseScopeConnector);
            scopeConnectorWrapper.setScopeCache(scopeCache);
            scopeConnector = scopeConnectorWrapper;
        } else {
            scopeConnector = databaseScopeConnector;
        }
    }

    public Object getObject() {
        return scopeConnector;
    }

    public Class getObjectType() {
        return ScopeConnector.class;
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

    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }

    public void setSqlFindById(String sqlFindById) {
        this.sqlFindById = sqlFindById;
    }

    public void setSqlFindByCode(String sqlFindByCode) {
        this.sqlFindByCode = sqlFindByCode;
    }

    public void setSqlFindByRef(String sqlFindByRef) {
        this.sqlFindByRef = sqlFindByRef;
    }

    public void setSqlFindAll(String sqlFindAll) {
        this.sqlFindAll = sqlFindAll;
    }

    public void setSqlFindSharedScopes(String sqlFindSharedScopes) {
        this.sqlFindSharedScopes = sqlFindSharedScopes;
    }
}
