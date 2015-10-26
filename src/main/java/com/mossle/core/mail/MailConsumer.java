package com.mossle.core.mail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(MailConsumer.class);
    private MailStore mailStore;
    private MailHelper mailHelper;
    private boolean active;
    private Thread thread;
    private ExecutorService executorService;
    private int threshold = 10;

    public void start() {
        active = true;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        executorService = Executors.newFixedThreadPool(10);
    }

    public void stop() {
        active = false;
        thread = null;
        executorService.shutdown();
    }

    public void run() {
        while (active) {
            for (int i = 0; i < threshold; i++) {
                doConsume();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage(), ex);
            }
        }
    }

    public void doConsume() {
        try {
            MailDTO mailDto = mailStore.takeMailDto();
            logger.debug("consume : {}", mailDto);

            MailWorker mailWorker = new MailWorker(mailDto, mailHelper);
            executorService.submit(mailWorker);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    public void setMailStore(MailStore mailStore) {
        this.mailStore = mailStore;
    }

    public void setMailHelper(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
