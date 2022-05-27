package com.mossle.simulator.mq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqTopic {
    private static Logger logger = LoggerFactory.getLogger(MqTopic.class);
    private String name;
    private AtomicLong offset = new AtomicLong();
    private NavigableMap<Long, MqMessage> messageMap = new ConcurrentSkipListMap<Long, MqMessage>();
    private Map<String, MqGroup> groupMap = new HashMap<String, MqGroup>();
    private int thresholdTotal = 10000;
    private int thresholdPeriod = 100;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, MqMessage> getMessageMap() {
        return messageMap;
    }

    public void addMessage(String body) {
        long id = offset.incrementAndGet();
        MqMessage message = new MqMessage();
        message.setId(id);
        message.setTopic(name);
        message.setBody(body);
        messageMap.put(id, message);

        logger.info("add message : {} {}", name, id);

        if (messageMap.size() > thresholdTotal) {
            for (int i = 0; i < thresholdPeriod; i++) {
                messageMap.pollFirstEntry();
            }
        }
    }

    public void registerConsumer(String consumer, String group) {
        MqGroup mqGroup = null;

        synchronized (groupMap) {
            mqGroup = groupMap.get(group);

            if (mqGroup == null) {
                mqGroup = new MqGroup();
                groupMap.put(group, mqGroup);
            }
        }

        mqGroup.addConsumer(consumer);
    }

    public void unregisterConsumer(String consumer, String group) {
        MqGroup mqGroup = null;

        synchronized (groupMap) {
            mqGroup = groupMap.get(group);

            if (mqGroup == null) {
                return;
            }
        }

        mqGroup.removeConsumer(consumer);
    }

    public void updateOffset(String group, Long targetOffset) {
        MqGroup mqGroup = groupMap.get(group);

        if (mqGroup == null) {
            return;
        }

        mqGroup.setOffset(targetOffset + 1);
    }

    public List<MqMessage> fetchMessages(String group, int size) {
        logger.info("fetch messages : {} {} {}", name, group, size);

        MqGroup mqGroup = null;

        synchronized (groupMap) {
            mqGroup = groupMap.get(group);

            if (mqGroup == null) {
                mqGroup = new MqGroup();
                groupMap.put(group, mqGroup);
            }
        }

        long currentOffset = mqGroup.getOffset();
        logger.info("fetch messages : {}", currentOffset);

        long startOffset = messageMap.firstKey();
        long endOffset = messageMap.lastKey();

        if (currentOffset >= endOffset) {
            return Collections.emptyList();
        }

        if (currentOffset > startOffset) {
            startOffset = currentOffset;
        }

        if ((endOffset - startOffset) > size) {
            endOffset = startOffset + size;
        }

        if (startOffset >= endOffset) {
            return Collections.emptyList();
        }

        Map<Long, MqMessage> subMap = messageMap.subMap(startOffset, true,
                endOffset, true);
        List<MqMessage> list = new ArrayList<MqMessage>();
        list.addAll(subMap.values());

        return list;
    }
}
