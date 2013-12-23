package com.mossle.bridge.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseScopeConnector implements ScopeConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseScopeConnector.class);
    private JdbcTemplate jdbcTemplate;

    // ~
    private String sqlFindById = "select id as id,code as code,name as name,ref as ref,"
            + " shared as shared,user_repo_ref as userRepoRef"
            + " from SCOPE_INFO where id=?";
    private String sqlFindByCode = "select id as id,code as code,name as name,ref as ref,"
            + " shared as shared,user_repo_ref as userRepoRef"
            + " from SCOPE_INFO where code=?";
    private String sqlFindByRef = "select id as id,code as code,name as name,ref as ref,"
            + " shared as shared,user_repo_ref as userRepoRef"
            + " from SCOPE_INFO where ref=?";
    private String sqlFindAll = "select id as id,code as code,name as name,ref as ref,"
            + " shared as shared,user_repo_ref as userRepoRef"
            + " from SCOPE_INFO";
    private String sqlFindSharedScopes = "select id as id,code as code,name as name,ref as ref,"
            + " shared as shared,user_repo_ref as userRepoRef"
            + " from SCOPE_INFO where shared=1";

    public ScopeInfo findById(String id) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindById, id);

            return convertScopeInfo(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.info("scope[{}] is not exists.", id, ex);

            return null;
        }
    }

    public ScopeInfo findByCode(String code) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindByCode,
                    code);

            return convertScopeInfo(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("scope[{}] is not exists.", code);

            return null;
        }
    }

    public ScopeInfo findByRef(String ref) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindByRef,
                    ref);

            return convertScopeInfo(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("scope[{}] is not exists.", ref);

            return null;
        }
    }

    public List<ScopeInfo> findAll() {
        List<ScopeInfo> scopeInfos = new ArrayList<ScopeInfo>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlFindAll);

        for (Map<String, Object> map : list) {
            ScopeInfo scopeInfo = convertScopeInfo(map);
            scopeInfos.add(scopeInfo);
        }

        return scopeInfos;
    }

    public List<ScopeInfo> findSharedScopes() {
        List<ScopeInfo> scopeInfos = new ArrayList<ScopeInfo>();
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList(sqlFindSharedScopes);

        for (Map<String, Object> map : list) {
            ScopeInfo scopeInfo = convertScopeInfo(map);
            scopeInfos.add(scopeInfo);
        }

        return scopeInfos;
    }

    protected ScopeInfo convertScopeInfo(Map<String, Object> map) {
        if ((map == null) || map.isEmpty()) {
            logger.info("scope[{}] is null.", map);

            return null;
        }

        logger.debug("{}", map);

        ScopeInfo scopeInfo = new ScopeInfo();
        scopeInfo.setId(getValue(map.get("id")));
        scopeInfo.setCode(this.getValue(map.get("code")));
        scopeInfo.setName(this.getValue(map.get("name")));
        scopeInfo.setUserRepoRef(this.getValue(map.get("userRepoRef")));
        scopeInfo.setShared(Integer.valueOf(1).equals(map.get("shared")));

        return scopeInfo;
    }

    private String getValue(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
