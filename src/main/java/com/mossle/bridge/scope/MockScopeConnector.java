package com.mossle.bridge.scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

public class MockScopeConnector implements ScopeConnector {
    private static Logger logger = LoggerFactory
            .getLogger(MockScopeConnector.class);
    private ScopeDTO scopeDto;

    public MockScopeConnector() {
        scopeDto = new ScopeDTO();
        scopeDto.setId("1");
        scopeDto.setCode("default");
        scopeDto.setName("default");
        scopeDto.setUserRepoRef("1");
        scopeDto.setShared(true);
        scopeDto.setType(0);
    }

    public ScopeDTO findById(String id) {
        return scopeDto;
    }

    public ScopeDTO findByCode(String code) {
        return scopeDto;
    }

    public ScopeDTO findByRef(String ref) {
        return scopeDto;
    }

    public List<ScopeDTO> findAll() {
        return Collections.singletonList(scopeDto);
    }

    public List<ScopeDTO> findSharedScopes() {
        return Collections.singletonList(scopeDto);
    }
}
