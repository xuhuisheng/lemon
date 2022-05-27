package com.mossle.simulator.mq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqBroker {
    private static MqBroker instance = new MqBroker();
    private Map<String, MqTopic> topicMap = new HashMap<String, MqTopic>();

    private MqBroker() {
    }

    public void send(String topic, String body) {
        MqTopic mqTopic = createOrGetTopic(topic);

        mqTopic.addMessage(body);
    }

    public List<MqMessage> receive(String group, Collection<String> topics,
            long timeout) {
        int batchSize = 100;
        int perSize = batchSize / topics.size();
        List<MqMessage> list = new ArrayList<MqMessage>();

        for (String topic : topics) {
            MqTopic mqTopic = topicMap.get(topic);

            if (mqTopic == null) {
                continue;
            }

            list.addAll(mqTopic.fetchMessages(group, perSize));
        }

        return list;
    }

    public void subscribe(String consumer, String group,
            Collection<String> topics) {
        for (String topic : topics) {
            MqTopic mqTopic = createOrGetTopic(topic);
            mqTopic.registerConsumer(consumer, group);
        }
    }

    public void commitSync(String group, Map<String, Long> offsetMap) {
        for (Map.Entry<String, Long> entry : offsetMap.entrySet()) {
            String topic = entry.getKey();
            Long topicGroupOffset = entry.getValue();
            MqTopic mqTopic = topicMap.get(topic);

            if (mqTopic == null) {
                continue;
            }

            mqTopic.updateOffset(group, topicGroupOffset);
        }
    }

    public void unregisterConsumer(String consumer, String group,
            Collection<String> topics) {
        for (String topic : topics) {
            MqTopic mqTopic = topicMap.get(topic);

            if (mqTopic == null) {
                continue;
            }

            mqTopic.unregisterConsumer(consumer, group);
        }
    }

    public MqTopic createOrGetTopic(String topic) {
        MqTopic mqTopic = null;

        synchronized (topicMap) {
            mqTopic = topicMap.get(topic);

            if (mqTopic == null) {
                mqTopic = new MqTopic();
                mqTopic.setName(topic);
                topicMap.put(topic, mqTopic);
            }
        }

        return mqTopic;
    }

    // ~
    public static MqBroker getInstance() {
        return instance;
    }
}
