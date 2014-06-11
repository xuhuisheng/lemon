package com.mossle.bridge.user;

import java.util.ArrayList;
import java.util.Collections;
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

public class MockUserConnector implements UserConnector {
    private static Logger logger = LoggerFactory
            .getLogger(MockUserConnector.class);
    private UserDTO userDto;

    public MockUserConnector() {
        userDto = new UserDTO();
        userDto.setId("1");
        userDto.setUsername("lingo");
        userDto.setDisplayName("lingo");
        userDto.setEmail("lingo.mossle@gmail.com");
        userDto.setMobile("13800138000");
        userDto.setUserRepoRef("1");
        userDto.setStatus(1);
    }

    public UserDTO findById(String id) {
        return userDto;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        return userDto;
    }

    public UserDTO findByRef(String ref, String userRepoRef) {
        return userDto;
    }

    public Page pagedQuery(Page page, Map<String, Object> parameters) {
        page.setTotalCount(1);
        page.setResult(Collections.singletonList(userDto));

        return page;
    }
}
