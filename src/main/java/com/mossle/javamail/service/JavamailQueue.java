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
public class JavamailQueue {
    private static Logger logger = LoggerFactory.getLogger(JavamailQueue.class);
    private BlockingQueue<JavamailCmd> receiveQueue = new LinkedBlockingQueue<JavamailCmd>();
    private BlockingQueue<JavamailCmd> sendQueue = new LinkedBlockingQueue<JavamailCmd>();
    private Thread receiveThread;
    private Thread sendThread;
    private ReceiveWorker receiveWorker;
    private SendWorker sendWorker;
    private boolean active = true;
    private JavamailService javamailService;

    @PostConstruct
    public void init() {
        receiveWorker = new ReceiveWorker(this);
        receiveThread = new Thread(receiveWorker);
        receiveThread.start();
        sendWorker = new SendWorker(this);
        sendThread = new Thread(sendWorker);
        sendThread.start();
    }

    @PreDestroy
    public void destroy() {
        receiveWorker.stop();
        sendWorker.stop();
    }

    public void doProcessReceive() throws Exception {
        JavamailCmd javamailCmd = receiveQueue.poll(1, TimeUnit.SECONDS);
        logger.debug("process receive : {}", javamailCmd);

        if (javamailCmd == null) {
            return;
        }

        try {
            if ("receive".equals(javamailCmd.getType())) {
                javamailService.receive(javamailCmd.getFrom());
            } else {
                logger.info("unsupport : {}", javamailCmd.getType());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void doProcessSend() throws Exception {
        JavamailCmd javamailCmd = sendQueue.poll(1, TimeUnit.SECONDS);
        logger.debug("process send : {}", javamailCmd);

        if (javamailCmd == null) {
            return;
        }

        try {
            if ("send".equals(javamailCmd.getType())) {
                javamailService.send(javamailCmd.getFrom(),
                        javamailCmd.getTo(), javamailCmd.getCc(),
                        javamailCmd.getBcc(), javamailCmd.getSubject(),
                        javamailCmd.getContent());
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

        receiveQueue.add(javamailCmd);
    }

    public void send(String from, String to, String cc, String bcc,
            String subject, String content) {
        JavamailCmd javamailCmd = new JavamailCmd();
        javamailCmd.setType("send");
        javamailCmd.setFrom(from);
        javamailCmd.setTo(to);
        javamailCmd.setCc(cc);
        javamailCmd.setBcc(bcc);
        javamailCmd.setSubject(subject);
        javamailCmd.setContent(content);

        sendQueue.add(javamailCmd);
    }

    @Resource
    public void setJavamailService(JavamailService javamailService) {
        this.javamailService = javamailService;
    }
}
