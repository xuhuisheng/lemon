package com.mossle.ext.message;

public interface Subscribable<T> {
    void handleMessage(T message);

    String getTopic();
}
