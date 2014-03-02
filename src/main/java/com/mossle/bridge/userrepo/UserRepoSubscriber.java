package com.mossle.bridge.userrepo;

import javax.annotation.Resource;

import com.mossle.api.userrepo.UserRepoCache;
import com.mossle.api.userrepo.UserRepoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.ext.message.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepoSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserRepoSubscriber.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private UserRepoCache userRepoCache;
    private String destinationName = "topic.userrepo.update";

    public void handleMessage(String message) {
        UserRepoDTO userRepoDto = jsonMapper.fromJson(message,
                UserRepoDTO.class);

        if (userRepoDto.getName() == null) {
            userRepoCache.removeUserRepo(userRepoDto);
            logger.info("remove userRepoDto : {}", message);
        } else {
            userRepoCache.updateUserRepo(userRepoDto);
            logger.info("update userRepoDto : {}", message);
        }
    }

    public String getTopic() {
        return destinationName;
    }

    @Resource
    public void setUserRepoCache(UserRepoCache userRepoCache) {
        this.userRepoCache = userRepoCache;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
