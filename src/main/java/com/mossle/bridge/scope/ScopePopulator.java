package com.mossle.bridge.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.scope.ScopeCache;
import com.mossle.api.scope.ScopeDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

public class ScopePopulator {
    private static Logger logger = LoggerFactory
            .getLogger(ScopePopulator.class);
    private JdbcTemplate jdbcTemplate;
    private ScopeCache scopeCache;
    private String sql = "select si.ID as scopeId,si.NAME as scopeName,si.CODE as scopeCode,"
            + " si.REF as scopeRef,si.SHARED as shared,si.USER_REPO_REF as userRepoRef"
            + " from SCOPE_INFO si";
    private boolean debug;

    @PostConstruct
    public void execute() {
        if (debug) {
            logger.info("skip scope populator");

            return;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> map : list) {
            ScopeDTO scopeDto = new ScopeDTO();
            scopeDto.setId(map.get("scopeId").toString());
            scopeDto.setName(map.get("scopeName").toString());
            scopeDto.setCode(map.get("scopeCode").toString());
            scopeDto.setRef(map.get("scopeRef").toString());
            scopeDto.setShared(Integer.valueOf(1).equals(map.get("shared")));
            scopeDto.setUserRepoRef(map.get("userRepoRef").toString());

            scopeCache.updateScope(scopeDto);
        }
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
