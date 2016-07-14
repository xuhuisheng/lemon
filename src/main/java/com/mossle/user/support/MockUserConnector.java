package com.mossle.user.support;

import java.util.Collections;
import java.util.Map;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public UserDTO findByNickName(String nickName, String userRepoRef) {
        return userDto;
    }

    public Page pagedQuery(String userRepoRef, Page page,
            Map<String, Object> parameters) {
        page.setTotalCount(1);
        page.setResult(Collections.singletonList(userDto));

        return page;
    }
}
