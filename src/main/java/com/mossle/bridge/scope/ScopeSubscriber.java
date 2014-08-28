package com.mossle.bridge.scope;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.scope.ScopeCache;
import com.mossle.api.scope.ScopeDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.ext.message.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(ScopeSubscriber.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private ScopeCache scopeCache;
    private String destinationName = "topic.scope.update";

    public void handleMessage(String message) {
        try {
            ScopeDTO scopeDto = jsonMapper.fromJson(message, ScopeDTO.class);

            if (scopeDto.getName() == null) {
                scopeCache.removeScope(scopeDto);
                logger.info("remove scopeDto : {}", message);
            } else {
                scopeCache.updateScope(scopeDto);
                logger.info("update scopeDto : {}", message);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean isTopic() {
        return true;
    }

    public String getName() {
        return destinationName;
    }

    @Resource
    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
