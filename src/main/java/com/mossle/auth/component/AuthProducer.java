package com.mossle.auth.component;

import javax.annotation.Resource;

import com.mossle.client.mq.MqProducer;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class AuthProducer {
    private static Logger logger = LoggerFactory.getLogger(AuthProducer.class);
    private MqProducer mqProducer;
    private JsonMapper jsonMapper = new JsonMapper();

    public void sendUpdate() {
        try {
            String payload = Long.toString(System.currentTimeMillis());
            mqProducer.send("mossle_auth_update", payload);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setMqProducer(MqProducer mqProducer) {
        this.mqProducer = mqProducer;
    }
}
