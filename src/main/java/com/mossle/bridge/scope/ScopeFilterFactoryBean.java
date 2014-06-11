package com.mossle.bridge.scope;

import javax.annotation.PostConstruct;

import javax.servlet.Filter;

import com.mossle.api.scope.ScopeConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class ScopeFilterFactoryBean implements FactoryBean {
    private static Logger logger = LoggerFactory
            .getLogger(ScopeFilterFactoryBean.class);
    private Filter scopeFilter;
    private String type = "prefix";
    private ScopeConnector scopeConnector;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(type, "type cannot be null");

        if ("mock".equals(type)) {
            processMock();
        } else if ("prefix".equals(type)) {
            processPrefix();
        } else if ("headerCode".equals(type)) {
            processHeaderCode();
        } else if ("headerRef".equals(type)) {
            processHeaderRef();
        } else {
            throw new IllegalArgumentException("unsupported type : " + type);
        }
    }

    public void processMock() {
        MockScopeFilter mockScopeFilter = new MockScopeFilter();
        mockScopeFilter.setScopeConnector(scopeConnector);
        scopeFilter = mockScopeFilter;
    }

    public void processPrefix() {
        PrefixScopeFilter prefixScopeFilter = new PrefixScopeFilter();
        prefixScopeFilter.setScopeConnector(scopeConnector);
        scopeFilter = prefixScopeFilter;
    }

    public void processHeaderCode() {
        HeaderCodeScopeFilter headerCodeScopeFilter = new HeaderCodeScopeFilter();
        headerCodeScopeFilter.setScopeConnector(scopeConnector);
        scopeFilter = headerCodeScopeFilter;
    }

    public void processHeaderRef() {
        HeaderRefScopeFilter headerRefScopeFilter = new HeaderRefScopeFilter();
        headerRefScopeFilter.setScopeConnector(scopeConnector);
        scopeFilter = headerRefScopeFilter;
    }

    public Object getObject() {
        return scopeFilter;
    }

    public Class getObjectType() {
        return Filter.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
