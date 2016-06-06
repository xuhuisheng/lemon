package com.mossle.javamail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendWorker implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(SendWorker.class);
    private JavamailQueue javamailQueue;
    private boolean active = true;

    public SendWorker(JavamailQueue javamailQueue) {
        this.javamailQueue = javamailQueue;
    }

    public void run() {
        while (active) {
            try {
                this.javamailQueue.doProcessSend();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void stop() {
        active = false;
    }
}
