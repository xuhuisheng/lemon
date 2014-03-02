package com.mossle.user.component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.user.persistence.domain.UserRepo;

import org.springframework.jms.core.JmsTemplate;

import org.springframework.stereotype.Component;

@Component
public class UserRepoPublisher {
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

        jmsTemplate.convertAndSend(destinationName, jsonMapper.toJson(map));
    }

    @Resource
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
