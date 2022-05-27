package com.mossle.user.component;

import javax.annotation.Resource;

import com.mossle.client.mq.MqProducer;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.user.support.UserInfoDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class UserProducer {
    private static Logger logger = LoggerFactory.getLogger(UserProducer.class);
    private MqProducer mqProducer;
    private JsonMapper jsonMapper = new JsonMapper();

    public void sendCreate(UserInfoDTO userInfoDto) {
        try {
            mqProducer.send("mossle_user_create",
                    jsonMapper.toJson(userInfoDto));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void sendUpdate(UserInfoDTO userInfoDto) {
        try {
            mqProducer.send("mossle_user_update",
                    jsonMapper.toJson(userInfoDto));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void sendRemove(UserInfoDTO userInfoDto) {
        try {
            mqProducer.send("mossle_user_remove",
                    jsonMapper.toJson(userInfoDto));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setMqProducer(MqProducer mqProducer) {
        this.mqProducer = mqProducer;
    }
}
