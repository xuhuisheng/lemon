package com.mossle.core.subscribe;

public interface Subscribable<T> {
    void handleMessage(T message);

    boolean isTopic();

    String getName();
}
