package com.mossle.api.user;

import java.util.Map;

import com.mossle.core.page.Page;

public class MockUserConnector implements UserConnector {
    /**
     * 根据唯一标识获取用户信息.
     * 
     * @param id
     *            用户的唯一标识，即便是不同用户库的用户id也是唯一的
     */
    public UserDTO findById(String id) {
        return null;
    }

    /**
     * 根据username和userRepoRef获取用户.
     * 
     * @param username
     *            登录账号，每个用户库中的用户登录名都是唯一的
     * @param userRepoRef
     *            用户库
     */
    public UserDTO findByUsername(String username, String userRepoRef) {
        return null;
    }

    /**
     * 根据reference和userRepoRef获取用户.
     * 
     * @param ref
     *            针对某个用户库的用户的唯一标识
     * @param userRepoRef
     *            用户库
     */
    public UserDTO findByRef(String ref, String userRepoRef) {
        return null;
    }

    /**
     * 分页查询用户.
     */
    public Page pagedQuery(String userRepoRef, Page page,
            Map<String, Object> parameters) {
        return null;
    }

    public UserDTO findByNickName(String nickName, String userRepoRef) {
        return null;
    }
}
