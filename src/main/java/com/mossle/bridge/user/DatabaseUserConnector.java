package com.mossle.bridge.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.hibernate.PropertyFilterUtils;
import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class DatabaseUserConnector implements UserConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserConnector.class);
    private JdbcTemplate jdbcTemplate;
    private Map<String, String> aliasMap = new HashMap<String, String>();

    // ~
    private String sqlFindById = "select id as id,username as username,status as status,"
            + "display_name as display_name,email as email,mobile as mobile,user_repo_id as user_repo_ref"
            + " from USER_BASE where id=?";
    private String sqlFindByUsername = "select ub.id as id,ub.username as username,ub.status as status,"
            + "display_name as display_name,email as email,mobile as mobile,user_repo_id as user_repo_ref"
            + " from USER_BASE ub where ub.username=? and ub.user_repo_id=?";
    private String sqlFindByRef = "select ub.id as id,ub.username as username,ub.status as status,"
            + "display_name as display_name,email as email,mobile as mobile,user_repo_id as user_repo_ref"
            + " from USER_BASE ub where ub.ref=? and ub.user_repo_id=?";
    private String sqlPagedQueryCount = "select count(*) from USER_BASE";
    private String sqlPagedQuerySelect = "select id as id,username as username,status as status,"
            + "display_name as display_name,email as email,mobile as mobile,user_repo_id as user_repo_ref"
            + " from USER_BASE";

    public UserDTO findById(String id) {
        Assert.notNull(id, "user id should not be null");

        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindById, id);

            return convertUserDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user[{}] is not exists.", id);

            return null;
        }
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(
                    sqlFindByUsername, username, userRepoRef);

            return convertUserDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user[{}, {}] is not exists.", username, userRepoRef);

            return null;
        }
    }

    public UserDTO findByRef(String ref, String userRepoRef) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindByRef,
                    ref, userRepoRef);

            return convertUserDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user[{}, {}] is not exists.", ref, userRepoRef);

            return null;
        }
    }

    public Page pagedQuery(Page page, Map<String, Object> parameters) {
        Map<String, Object> parameterMap = this.convertAlias(parameters);

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        StringBuilder buff = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();
        boolean checkWhere = sqlPagedQuerySelect.toLowerCase().indexOf("where") == -1;
        PropertyFilterUtils.buildConfigurations(propertyFilters, buff,
                paramList, checkWhere);
        logger.debug("propertyFilters : {}", propertyFilters);
        logger.debug("buff : {}", buff);
        logger.debug("paramList : {}", paramList);
        logger.debug("checkWhere : {}", checkWhere);

        String sql = buff.toString();
        String countSql = sqlPagedQueryCount + " " + sql;
        String selectSql = sqlPagedQuerySelect + " " + sql + " limit "
                + page.getStart() + "," + page.getPageSize();
        logger.debug("countSql : {}", countSql);
        logger.debug("selectSql : {}", selectSql);

        Object[] params = paramList.toArray();
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class,
                params);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql,
                params);
        List<UserDTO> userDtos = new ArrayList<UserDTO>();

        for (Map<String, Object> map : list) {
            userDtos.add(convertUserDTO(map));
        }

        page.setTotalCount(totalCount);
        page.setResult(userDtos);

        return page;
    }

    protected UserDTO convertUserDTO(Map<String, Object> map) {
        if ((map == null) || map.isEmpty()) {
            logger.info("user[{}] is null.", map);

            return null;
        }

        logger.debug("{}", map);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(convertString(map.get("id")));
        userDTO.setUsername(convertString(map.get("username")));
        userDTO.setDisplayName(convertString(map.get("display_name")));
        userDTO.setEmail(convertString(map.get("email")));
        userDTO.setMobile(convertString(map.get("mobile")));
        userDTO.setUserRepoRef(convertString(map.get("user_repo_ref")));
        userDTO.setStatus(convertInt(map.get("status"), 1));

        return userDTO;
    }

    public String convertString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public Integer convertInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return Integer.parseInt(value.toString());
    }

    protected Map<String, Object> convertAlias(Map<String, Object> parameters) {
        logger.debug("parameters : {}", parameters);

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String key = entry.getKey();

            for (Map.Entry<String, String> aliasEntry : aliasMap.entrySet()) {
                String aliasKey = "_" + aliasEntry.getKey();
                String aliasValue = "_" + aliasEntry.getValue();

                if (key.indexOf(aliasKey) != -1) {
                    key = key.replace(aliasKey, aliasValue);

                    break;
                }
            }

            parameterMap.put(key, entry.getValue());
        }

        logger.debug("parameterMap : {}", parameterMap);

        return parameterMap;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
