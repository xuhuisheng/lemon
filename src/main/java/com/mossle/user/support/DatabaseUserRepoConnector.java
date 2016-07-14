package com.mossle.user.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.userrepo.UserRepoConnector;
import com.mossle.api.userrepo.UserRepoDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseUserRepoConnector implements UserRepoConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserRepoConnector.class);
    private JdbcTemplate jdbcTemplate;
    private String sqlFindById = "select id,code,name from user_repo where id=?";
    private String sqlFindByCode = "select id,code,name from user_repo where code=?";
    private String sqlFindAll = "select id,code,name from user_repo";

    public UserRepoDTO findById(String id) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindById, id);

            return convertUserRepoDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user repo(id : {}) is not exists.", id);

            return null;
        }
    }

    public UserRepoDTO findByCode(String code) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sqlFindByCode,
                    code);

            return convertUserRepoDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user repo(code : {}) is not exists.", code);

            return null;
        }
    }

    public List<UserRepoDTO> findAll() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlFindAll);
        List<UserRepoDTO> userRepoDtos = new ArrayList<UserRepoDTO>();

        for (Map<String, Object> map : list) {
            userRepoDtos.add(convertUserRepoDTO(map));
        }

        return userRepoDtos;
    }

    protected UserRepoDTO convertUserRepoDTO(Map<String, Object> map) {
        if ((map == null) || map.isEmpty()) {
            logger.info("user repo[{}] is null.", map);

            return null;
        }

        UserRepoDTO userRepoDto = new UserRepoDTO();
        userRepoDto.setId((map.get("id") == null) ? null : map.get("id")
                .toString());
        userRepoDto.setCode((map.get("code") == null) ? null : map.get("code")
                .toString());
        userRepoDto.setName((map.get("name") == null) ? null : map.get("name")
                .toString());

        return userRepoDto;
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

    public void setSqlFindAll(String sqlFindAll) {
        this.sqlFindAll = sqlFindAll;
    }
}
