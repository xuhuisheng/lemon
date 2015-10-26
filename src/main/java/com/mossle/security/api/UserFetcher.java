package com.mossle.security.api;

/**
 * 获取用户信息.
 * 
 * 存在的问题是，如果只从一个用户资源库读取用户，可以保证username唯一 但是如果需要整个多个用户资源库，可能出现用户名不唯一的情况 这时要根据username与repoCode结合确认唯一的用户
 * 
 * 第二个问题是，我们一般不需要把所有app的权限都返回给某一个app 所以还需要根据appId进一步筛选
 */
public interface UserFetcher {
    UserInfo getUserInfo(String username);

    UserInfo getUserInfo(String username, String tenantId);

    UserInfo getUserInfo(String username, String userRepoRef, String tenantId);
}
