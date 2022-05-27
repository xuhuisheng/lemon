package com.mossle.client.mq;

import java.util.Collection;
import java.util.List;

public interface MqConsumer {
    List<String> receive(long timeout);

    void subscribe(Collection<String> topics);

    void commitSync();

    void close();

    void setGroup(String group);
}
