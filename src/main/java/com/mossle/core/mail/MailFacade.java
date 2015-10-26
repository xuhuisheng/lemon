package com.mossle.core.mail;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailFacade {
    private static Logger logger = LoggerFactory.getLogger(MailFacade.class);
    private MailStore mailStore = new MailStore();
    private MailConsumer mailConsumer = new MailConsumer();
    private MailHelper mailHelper;

    @PostConstruct
    public void init() {
        mailStore.start();
        mailConsumer.setMailStore(mailStore);
        mailConsumer.setMailHelper(mailHelper);
        mailConsumer.start();
    }

    @PreDestroy
    public void close() {
        mailConsumer.stop();
        mailStore.stop();
    }

    public void sendMail(String to, String subject, String content) {
        this.sendMail(null, to, subject, content);
    }

    public void sendMail(String from, String to, String subject, String content) {
        MailDTO mailDto = new MailDTO();
        mailDto.setFrom(from);
        mailDto.setTo(to);
        mailDto.setSubject(subject);
        mailDto.setContent(content);

        mailStore.sendMail(mailDto);
    }

    public void sendMail(MailDTO mailDto) {
        mailStore.sendMail(mailDto);
    }

    public void setMailStore(MailStore mailStore) {
        this.mailStore = mailStore;
    }

    public void setMailConsumer(MailConsumer mailConsumer) {
        this.mailConsumer = mailConsumer;
    }

    public void setMailHelper(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }
}
