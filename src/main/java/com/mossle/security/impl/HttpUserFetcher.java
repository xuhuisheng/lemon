package com.mossle.security.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.core.http.HttpHandler;
import com.mossle.core.http.HttpHandlerImpl;
import com.mossle.core.mapper.JsonMapper;

import com.mossle.security.api.UserFetcher;
import com.mossle.security.api.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUserFetcher implements UserFetcher {
    private static Logger logger = LoggerFactory
            .getLogger(HttpUserFetcher.class);
    private HttpHandler httpHandler = new HttpHandlerImpl();
    private String url;
    private Long defaultAppId = 1L;
    private Long defaultRepoCode = 1L;

    public UserInfo getUserInfo(String username) {
        return getUserInfo(username, defaultAppId, defaultRepoCode);
    }

    public UserInfo getUserInfo(String username, Long appId) {
        return getUserInfo(username, appId, defaultRepoCode);
    }

    public UserInfo getUserInfo(String username, Long appId, Long repoCode) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("username", username);
        parameterMap.put("appId", appId);
        parameterMap.put("repoCode", repoCode);

        try {
            String content = httpHandler.readText(url, parameterMap);
            logger.info(content);

            JsonMapper jsonMapper = new JsonMapper();
            Map map = jsonMapper.fromJson(content, Map.class);
            logger.debug("{}", map);

            long userId = ((Number) map.get("userId")).longValue();

            List<String> authorities = (List<String>) map.get("authorities");
            List<String> attributes = (List<String>) map.get("attributes");

            UserInfoImpl userInfo = new UserInfoImpl();
            userInfo.setUsername(username);
            userInfo.setPassword((String) map.get("password"));
            userInfo.setAuthorities(authorities);
            userInfo.setAttributes(attributes);
            userInfo.getExtra().put("userId", userId);
            userInfo.getExtra().put("appId", appId);

            return userInfo;
        } catch (Exception ex) {
            logger.error("", ex);
            throw new RuntimeException(ex);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDefaultAppId(Long defaultAppId) {
        this.defaultAppId = defaultAppId;
    }

    public void setDefaultRepoCode(Long defaultRepoCode) {
        this.defaultRepoCode = defaultRepoCode;
    }

    public void setHttpHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }
}
