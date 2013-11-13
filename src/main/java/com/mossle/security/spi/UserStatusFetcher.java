package com.mossle.security.spi;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 从我们的设计理念来讲，用户数据中心可能和权限控制模块是分离的两个模块， 所以每次用户登录时，都要先判断远程的用户是否存在，然后判断用户状态， 如果两者都没有问题，要么创建一个新用户，要么更新当前用户状态。
 */
public interface UserStatusFetcher {
    int STATUS_ENABLE = 0;
    int STATUS_ACCOUNT_EXPIRED = 1;
    int STATUS_ACCOUNT_LOCKED = 1 << 1;
    int STATUS_CREDENTAIL_EXPIRED = 1 << 2;

    /**
     * 根据用户名获得用户状态.
     * 
     * 如果用户存在，就会返回一个状态 如果用户不存在，就会抛出异常
     */
    int getUserStatus(String username) throws UsernameNotFoundException;
}
