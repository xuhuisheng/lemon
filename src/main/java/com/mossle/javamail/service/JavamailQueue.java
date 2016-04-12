package com.mossle.javamail.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.javamail.support.JavamailCmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class JavamailQueue implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(JavamailQueue.class);
    private BlockingQueue<JavamailCmd> queue = new LinkedBlockingQueue<JavamailCmd>();
    private Thread thread;
    private boolean active = true;
    private JavamailService javamailService;

    @PostConstruct
    public void init() {
        thread = new Thread(this);
        thread.start();
    }

    @PreDestroy
    public void destroy() {
        active = false;
    }

    public void run() {
        while (active) {
            try {
                this.doProcess();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void doProcess() throws Exception {
        JavamailCmd javamailCmd = queue.poll(1, TimeUnit.SECONDS);
        logger.debug("process : {}", javamailCmd);

        if (javamailCmd == null) {
            return;
        }

        try {
            if ("send".equals(javamailCmd.getType())) {
                javamailService.send(javamailCmd.getFrom(),
                        javamailCmd.getTo(), javamailCmd.getSubject(),
                        javamailCmd.getContent());
            } else if ("receive".equals(javamailCmd.getType())) {
                javamailService.receive(javamailCmd.getFrom());
            } else {
                logger.info("unsupport : {}", javamailCmd.getType());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void receive(String userId) {
        JavamailCmd javamailCmd = new JavamailCmd();
        javamailCmd.setType("receive");
        javamailCmd.setFrom(userId);

        queue.add(javamailCmd);
    }

    public void send(String from, String to, String subject, String content) {
        JavamailCmd javamailCmd = new JavamailCmd();
        javamailCmd.setType("send");
        javamailCmd.setFrom(from);
        javamailCmd.setTo(to);
        javamailCmd.setSubject(subject);
        javamailCmd.setContent(content);

        queue.add(javamailCmd);
    }

    @Resource
    public void setJavamailService(JavamailService javamailService) {
        this.javamailService = javamailService;
    }
}
