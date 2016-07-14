package com.mossle.user.publish;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.core.JmsTemplate;

import org.springframework.stereotype.Component;

@Component
public class UserPublisher {
    private static Logger logger = LoggerFactory.getLogger(UserPublisher.class);
    private ConnectionFactory connectionFactory;
    private String notificationPrefix = "topic.user.notify.";
    private String synchronizationPrefix = "queue.user.sync.";
    private JsonMapper jsonMapper = new JsonMapper();

    public void notifyUserCreated(UserDTO userDto) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", userDto.getId());
        map.put("username", userDto.getUsername());
        map.put("displayName", userDto.getDisplayName());
        map.put("email", userDto.getEmail());
        map.put("mobile", userDto.getMobile());
        map.put("userRepoRef", userDto.getUserRepoRef());

        this.sendNotification(notificationPrefix + "created", map);
        this.sendSynchronization(synchronizationPrefix + "created", map);
    }

    public void notifyUserUpdated(UserDTO userDto) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", userDto.getId());
        map.put("username", userDto.getUsername());
        map.put("displayName", userDto.getDisplayName());
        map.put("email", userDto.getEmail());
        map.put("mobile", userDto.getMobile());
        map.put("userRepoRef", userDto.getUserRepoRef());

        this.sendNotification(notificationPrefix + "updated", map);
        this.sendSynchronization(synchronizationPrefix + "updated", map);
    }

    public void notifyUserRemoved(UserDTO userDto) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", userDto.getId());
        map.put("username", userDto.getUsername());
        map.put("displayName", userDto.getDisplayName());
        map.put("email", userDto.getEmail());
        map.put("mobile", userDto.getMobile());
        map.put("userRepoRef", userDto.getUserRepoRef());

        this.sendNotification(notificationPrefix + "removed", map);
        this.sendSynchronization(synchronizationPrefix + "removed", map);
    }

    public void sendNotification(String destinationName, Object object) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setPubSubDomain(true);

        try {
            jmsTemplate.convertAndSend(destinationName,
                    jsonMapper.toJson(object));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void sendSynchronization(String destinationName, Object object) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setPubSubDomain(false);

        try {
            jmsTemplate.convertAndSend(destinationName,
                    jsonMapper.toJson(object));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    @Resource
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setNotificationPrefix(String notificationPrefix) {
        this.notificationPrefix = notificationPrefix;
    }

    public void setSynchronizationPrefix(String synchronizationPrefix) {
        this.synchronizationPrefix = synchronizationPrefix;
    }
}
