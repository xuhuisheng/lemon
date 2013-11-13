package com.mossle.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.GlobalScopeDTO;
import com.mossle.api.LocalScopeDTO;
import com.mossle.api.ScopeConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseScopeConnector implements ScopeConnector {
    private static Logger logger = LoggerFactory
            .getLogger(ScopeConnector.class);
    private JdbcTemplate jdbcTemplate;

    public Long findGlobalId(String globalCode) {
        String sql = "select id from SCOPE_GLOBAL where name=?";

        try {
            return (Long) jdbcTemplate.queryForObject(sql, Long.class,
                    globalCode);
        } catch (EmptyResultDataAccessException ex) {
            logger.info("ScopeGlobal[{}] is not exists.", globalCode);

            return null;
        }
    }

    public Long findLocalId(String globalCode, String localCode) {
        String sql = "select l.id from SCOPE_GLOBAL g,SCOPE_LOCAL l where g.id=l.global_id and l.name=? and g.name=?";

        try {
            return (Long) jdbcTemplate.queryForObject(sql, Long.class,
                    localCode, globalCode);
        } catch (EmptyResultDataAccessException ex) {
            logger.info("ScopeLocal[{}, {}] is not exists.", globalCode,
                    localCode);

            return null;
        }
    }

    public List<GlobalScopeDTO> findGlobalScopes() {
        String sql = "select id,name from SCOPE_GLOBAL";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<GlobalScopeDTO> globalScopeDtos = new ArrayList<GlobalScopeDTO>();

        for (Map map : list) {
            GlobalScopeDTO globalScopeDto = new GlobalScopeDTO();
            globalScopeDtos.add(globalScopeDto);
            globalScopeDto.setId((Long) map.get("id"));
            globalScopeDto.setName((String) map.get("name"));
        }

        return globalScopeDtos;
    }

    public List<LocalScopeDTO> findLocalScopes() {
        String sql = "select l.id as local_id,l.name as local_name,g.id as global_id,g.name as global_name"
                + " from SCOPE_LOCAL l,SCOPE_GLOBAL g where l.global_id=g.id";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<LocalScopeDTO> localScopeDtos = new ArrayList<LocalScopeDTO>();

        for (Map map : list) {
            LocalScopeDTO localScopeDto = new LocalScopeDTO();
            localScopeDtos.add(localScopeDto);
            localScopeDto.setId((Long) map.get("local_id"));
            localScopeDto.setName((String) map.get("local_name"));
            localScopeDto.setGlobalId((Long) map.get("global_id"));
            localScopeDto.setGlobalName((String) map.get("global_name"));
        }

        return localScopeDtos;
    }

    public List<LocalScopeDTO> findSharedLocalScopes() {
        String sql = "select l.id as local_id,l.name as local_name,g.id as global_id,g.name as global_name"
                + " from SCOPE_LOCAL l,SCOPE_GLOBAL g where l.global_id=g.id and l.shared=1";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<LocalScopeDTO> localScopeDtos = new ArrayList<LocalScopeDTO>();

        for (Map map : list) {
            LocalScopeDTO localScopeDto = new LocalScopeDTO();
            localScopeDtos.add(localScopeDto);
            localScopeDto.setId((Long) map.get("local_id"));
            localScopeDto.setName((String) map.get("local_name"));
            localScopeDto.setGlobalId((Long) map.get("global_id"));
            localScopeDto.setGlobalName((String) map.get("global_name"));
        }

        return localScopeDtos;
    }

    public LocalScopeDTO getLocalScope(Long localScopeId) {
        String sql = "select l.id as local_id,l.name as local_name,g.id as global_id,"
                + "g.name as global_name,shared as shared"
                + " from SCOPE_LOCAL l,SCOPE_GLOBAL g where l.global_id=g.id and l.id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, localScopeId);
        LocalScopeDTO localScopeDto = new LocalScopeDTO();

        localScopeDto.setId((Long) map.get("local_id"));
        localScopeDto.setName((String) map.get("local_name"));
        localScopeDto.setGlobalId((Long) map.get("global_id"));
        localScopeDto.setGlobalName((String) map.get("global_name"));
        localScopeDto.setShared(Integer.valueOf(1).equals(map.get("shared")));

        return localScopeDto;
    }

    public GlobalScopeDTO getGlobalScope(Long globalScopeId) {
        String sql = "select id,name from SCOPE_GLOBAL where id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, globalScopeId);

        GlobalScopeDTO globalScopeDto = new GlobalScopeDTO();

        globalScopeDto.setId((Long) map.get("id"));
        globalScopeDto.setName((String) map.get("name"));

        return globalScopeDto;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
