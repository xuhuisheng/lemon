package com.mossle.security.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.userauth.ResourceDTO;

import com.mossle.client.authz.AuthzResourceClient;

import com.mossle.core.http.HttpHandler;
import com.mossle.core.http.HttpHandlerImpl;
import com.mossle.core.mapper.JsonMapper;

import com.mossle.security.api.UrlSourceFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class RemoteUrlSourceFetcher implements UrlSourceFetcher {
    private static Logger logger = LoggerFactory
            .getLogger(RemoteUrlSourceFetcher.class);
    private String sysCode;
    private AuthzResourceClient authzResourceClient;

    public Map<String, String> getSource(String type) {
        try {
            List<ResourceDTO> resourceDtos = authzResourceClient
                    .findResource(sysCode);

            Map<String, String> resourceMap = new LinkedHashMap<String, String>();

            for (ResourceDTO resourceDto : resourceDtos) {
                String resource = resourceDto.getResource();
                String permission = resourceDto.getPermission();

                if (resourceMap.containsKey(resource)) {
                    String value = resourceMap.get(resource);
                    resourceMap.put(resource, value + "," + permission);
                } else {
                    resourceMap.put(resource, permission);
                }
            }

            return resourceMap;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new IllegalStateException("error on fetch url source", ex);
        }
    }

    @Resource
    public void setAuthzResourceClient(AuthzResourceClient authzResourceClient) {
        this.authzResourceClient = authzResourceClient;
    }

    @Value("${authz.client.code}")
    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }
}
