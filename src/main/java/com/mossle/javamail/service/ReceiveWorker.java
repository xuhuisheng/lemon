package com.mossle.javamail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveWorker implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ReceiveWorker.class);
    private JavamailQueue javamailQueue;
    private boolean active = true;

    public ReceiveWorker(JavamailQueue javamailQueue) {
        this.javamailQueue = javamailQueue;
    }

    public void run() {
        while (active) {
            try {
                this.javamailQueue.doProcessReceive();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void stop() {
        active = false;
    }
}
