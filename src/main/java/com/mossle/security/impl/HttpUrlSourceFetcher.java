package com.mossle.security.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mossle.core.http.HttpHandler;
import com.mossle.core.http.HttpHandlerImpl;
import com.mossle.core.mapper.JsonMapper;

import com.mossle.security.api.UrlSourceFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUrlSourceFetcher implements UrlSourceFetcher {
    private static Logger logger = LoggerFactory
            .getLogger(HttpUrlSourceFetcher.class);
    private HttpHandler httpHandler = new HttpHandlerImpl();
    private String url;
    private String appId;

    public Map<String, String> getSource(String type) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();

        if (appId != null) {
            parameterMap.put("appId", appId);
        }

        try {
            String content = httpHandler.readText(url, parameterMap);
            logger.info(content);

            JsonMapper jsonMapper = new JsonMapper();
            List<Map> list = jsonMapper.fromJson(content, List.class);

            Map<String, String> resourceMap = new LinkedHashMap<String, String>();

            for (Map map : list) {
                String access = (String) map.get("access");
                String permission = (String) map.get("permission");

                if (resourceMap.containsKey(access)) {
                    String value = resourceMap.get(access);
                    resourceMap.put(access, value + "," + permission);
                } else {
                    resourceMap.put(access, permission);
                }
            }

            return resourceMap;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new IllegalStateException("error on fetch url source", ex);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setHttpHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }
}
