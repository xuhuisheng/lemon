package com.mossle.user.component;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.user.persistence.domain.UserRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;

import org.springframework.stereotype.Component;

@Component
public class UserRepoPublisher {
    private static Logger logger = LoggerFactory
            .getLogger(UserRepoPublisher.class);
    private ConnectionFactory connectionFactory;
    private String destinationName = "topic.userrepo.update";
    private JsonMapper jsonMapper = new JsonMapper();

    public void execute(UserRepo userRepo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", Long.toString(userRepo.getId()));
        map.put("code", userRepo.getCode());
        map.put("name", userRepo.getName());

        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setPubSubDomain(true);

        try {
            jmsTemplate.convertAndSend(destinationName, jsonMapper.toJson(map));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    @Resource
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
