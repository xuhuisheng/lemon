package com.mossle.user.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;
import com.mossle.api.user.UserSyncConnector;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.query.PropertyFilterUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class DatabaseUserConnector implements UserConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserConnector.class);
    private JdbcTemplate jdbcTemplate;
    private UserSyncConnector userSyncConnector;
    private Map<String, String> aliasMap = new HashMap<String, String>();

    // ~
    private String sqlFindById = "SELECT AI.CODE AS CODE,AI.USERNAME AS USERNAME,AI.STATUS AS STATUS,"
            + "AI.NICK_NAME AS NICK_NAME,AI.DISPLAY_NAME AS DISPLAY_NAME,PI.EMAIL AS EMAIL,"
            + "PI.CELLPHONE AS MOBILE,1 AS USER_REPO_REF"
            + " FROM ACCOUNT_INFO AI LEFT JOIN PERSON_INFO PI ON AI.CODE=PI.CODE"
            + " WHERE AI.CODE=?";
    private String sqlFindByUsername = "SELECT AI.CODE AS CODE,AI.USERNAME AS USERNAME,AI.STATUS AS STATUS,"
            + "AI.NICK_NAME AS NICK_NAME,AI.DISPLAY_NAME AS DISPLAY_NAME,PI.EMAIL AS EMAIL,"
            + "PI.CELLPHONE AS MOBILE,AI.TENANT_ID AS USER_REPO_REF"
            + " FROM ACCOUNT_INFO AI LEFT JOIN PERSON_INFO PI ON AI.CODE=PI.CODE"
            + " WHERE AI.USERNAME=? AND AI.TENANT_ID=?";
    private String sqlFindByRef = "SELECT UB.CODE AS CODE,UB.USERNAME AS USERNAME,UB.STATUS AS STATUS,"
            + "NICK_NAME AS NICK_NAME,EMAIL AS EMAIL,MOBILE AS MOBILE,USER_REPO_ID AS USER_REPO_REF"
            + " FROM ACCOUNT_INFO UB WHERE UB.REF=? AND UB.USER_REPO_ID=?";
    private String sqlPagedQueryCount = "SELECT COUNT(*) FROM ACCOUNT_INFO AI";
    private String sqlPagedQuerySelect = "SELECT AI.CODE AS CODE,AI.USERNAME AS USERNAME,AI.STATUS AS STATUS,"
            + "AI.NICK_NAME AS NICK_NAME,AI.DISPLAY_NAME AS DISPLAY_NAME,PI.EMAIL AS EMAIL,"
            + "PI.CELLPHONE AS MOBILE,1 AS USER_REPO_REF"
            + " FROM ACCOUNT_INFO AI LEFT JOIN PERSON_INFO PI ON AI.CODE=PI.CODE";
    private String sqlFindByNickName = "SELECT CODE AS CODE,USERNAME AS USERNAME,STATUS AS STATUS,"
            + "NICK_NAME AS NICK_NAME,EMAIL AS EMAIL,MOBILE AS MOBILE,USER_REPO_ID AS USER_REPO_REF"
            + " FROM ACCOUNT_INFO WHERE NICK_NAME=?";

    public UserDTO findById(String id) {
        Assert.hasText(id, "user id should not be null");

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
        if (username == null) {
            logger.info("username is null");

            return null;
        }

        username = username.toLowerCase();

        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(
                    sqlFindByUsername, username, userRepoRef);

            return convertUserDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user[{}, {}] is not exists.", username, userRepoRef);

            if (userSyncConnector != null) {
                UserDTO userDto = new UserDTO();
                userDto.setRef(username);
                userDto.setUsername(username);
                userDto.setDisplayName(username);
                userDto.setNickName(username);
                userSyncConnector.updateUser(userDto);

                return this.findByUsername(username, userRepoRef);
            }

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

    public Page pagedQuery(String userRepoRef, Page page,
            Map<String, Object> parameters) {
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

    public UserDTO findByNickName(String nickName, String userRepoRef) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(
                    sqlFindByNickName, nickName);

            return convertUserDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user[{}] is not exists.", nickName);

            return null;
        }
    }

    public String findUsernameByAlias(String alias) {
        return alias;
    }

    protected UserDTO convertUserDTO(Map<String, Object> map) {
        if ((map == null) || map.isEmpty()) {
            logger.info("user[{}] is null.", map);

            return null;
        }

        logger.debug("{}", map);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(convertString(map.get("code")));
        userDTO.setUsername(convertString(map.get("username")));
        userDTO.setNickName(convertString(map.get("nick_name")));
        userDTO.setDisplayName(convertString(map.get("display_name")));
        userDTO.setEmail(convertString(map.get("email")));
        userDTO.setMobile(convertString(map.get("mobile")));
        userDTO.setUserRepoRef(convertString(map.get("user_repo_ref")));
        userDTO.setStatus("active".equals(map.get("status")) ? 1 : 0);

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

    @Resource
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
