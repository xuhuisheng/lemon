package com.mossle.simulator.mq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mossle.client.mq.MqConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryMqConsumer implements MqConsumer {
    private static Logger logger = LoggerFactory
            .getLogger(MemoryMqConsumer.class);
    private String id = UUID.randomUUID().toString();
    private String group;
    private Collection<String> topics;
    private Map<String, Long> offsetMap = new HashMap<String, Long>();

    public List<String> receive(long timeout) {
        List<MqMessage> messages = MqBroker.getInstance().receive(group,
                topics, timeout);
        List<String> list = new ArrayList<String>();

        for (MqMessage message : messages) {
            list.add(message.getBody());

            String topic = message.getTopic();
            Long offset = offsetMap.get(topic);

            if (offset == null) {
                offset = 0L;
            }

            offset = Math.max(offset, message.getId());
            offsetMap.put(topic, offset);
        }

        logger.info("recieve : {}", offsetMap);

        return list;
    }

    public void subscribe(Collection<String> topics) {
        MqBroker.getInstance().subscribe(id, group, topics);
        this.topics = topics;
    }

    public void commitSync() {
        MqBroker.getInstance().commitSync(group, offsetMap);
    }

    public void close() {
        MqBroker.getInstance().unregisterConsumer(id, group, topics);
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
