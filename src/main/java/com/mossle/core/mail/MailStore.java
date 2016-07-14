package com.mossle.core.mail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailStore {
    private static Logger logger = LoggerFactory.getLogger(MailStore.class);
    private BlockingQueue<MailDTO> queue = new LinkedBlockingQueue<MailDTO>();

    @PostConstruct
    public void start() {
    }

    @PreDestroy
    public void stop() {
    }

    public void sendMail(MailDTO mailDto) {
        try {
            queue.put(mailDto);
        } catch (InterruptedException ex) {
            logger.info(ex.getMessage(), ex);
        }
    }

    public MailDTO takeMailDto() throws InterruptedException {
        return queue.take();
    }
}
