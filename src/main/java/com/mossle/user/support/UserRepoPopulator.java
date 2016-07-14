package com.mossle.user.support;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.userrepo.UserRepoCache;
import com.mossle.api.userrepo.UserRepoDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class UserRepoPopulator {
    private static Logger logger = LoggerFactory
            .getLogger(UserRepoPopulator.class);
    private JdbcTemplate jdbcTemplate;
    private UserRepoCache userRepoCache;
    private String sql = "select id,code,name from USER_REPO";
    private boolean debug;

    @PostConstruct
    public void execute() {
        if (debug) {
            logger.info("skip userRepo populator");

            return;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> map : list) {
            UserRepoDTO userRepoDto = new UserRepoDTO();
            userRepoDto.setId(map.get("id").toString());
            userRepoDto.setCode(map.get("code").toString());
            userRepoDto.setName(map.get("name").toString());

            userRepoCache.updateUserRepo(userRepoDto);
        }
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setUserRepoCache(UserRepoCache userRepoCache) {
        this.userRepoCache = userRepoCache;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
