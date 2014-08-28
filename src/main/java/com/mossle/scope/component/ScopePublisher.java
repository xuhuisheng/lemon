package com.mossle.scope.component;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.scope.domain.ScopeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;

import org.springframework.stereotype.Component;

@Component
public class ScopePublisher {
    private static Logger logger = LoggerFactory
            .getLogger(ScopePublisher.class);
    private ConnectionFactory connectionFactory;
    private String destinationName = "topic.scope.update";
    private JsonMapper jsonMapper = new JsonMapper();

    public void execute(ScopeInfo scopeInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", Long.toString(scopeInfo.getId()));
        map.put("name", scopeInfo.getName());
        map.put("code", scopeInfo.getCode());
        map.put("ref", scopeInfo.getRef());
        map.put("shared", Integer.valueOf(1).equals(scopeInfo.getShared()));
        map.put("userRepoRef", scopeInfo.getUserRepoRef());

        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setPubSubDomain(true);

        try {
            jmsTemplate.convertAndSend(destinationName, jsonMapper.toJson(map));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
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
