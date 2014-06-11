package com.mossle.ext.mail;

import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import com.mossle.core.mail.HostGenerator;
import com.mossle.core.mail.HostGeneratorImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.InputStreamSource;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.util.Assert;

public class MailHelper {
    private static Logger logger = LoggerFactory.getLogger(MailHelper.class);
    private HostGenerator hostGenerator = new HostGeneratorImpl();
    private Map<String, MailServerInfo> mailServerInfoMap = new HashMap<String, MailServerInfo>();
    private MailServerInfo defaultMailServerInfo;

    public void send(String from, String to, String subject, String content) {
        this.send(from, to, subject, content, this.getDefaultMailServerInfo());
    }

    public void send(String from, String to, String subject, String content,
            String mailServerInfoName) {
        this.send(from, to, subject, content,
                mailServerInfoMap.get(mailServerInfoName));
    }

    public void send(String from, String to, String subject, String content,
            MailServerInfo mailServerInfo) {
        MailDTO mailDto = new MailDTO();
        mailDto.setFrom(from);
        mailDto.setTo(to);
        mailDto.setSubject(subject);
        mailDto.setContent(content);
        this.send(mailDto, mailServerInfo);
    }

    public void send(MailDTO mailDto) {
        this.send(mailDto, this.getDefaultMailServerInfo());
    }

    public void send(MailDTO mailDto, MailServerInfo mailServerInfo) {
        String from = mailDto.getFrom();
        String to = mailDto.getTo();
        String subject = mailDto.getSubject();
        String content = mailDto.getContent();

        if (from == null) {
            from = mailServerInfo.getDefaultFrom();
            mailDto.setFrom(from);
        }

        if (mailServerInfo.isSkip()) {
            logger.info("send mail from {} to {}", from, to);

            logger.info("subject : {}, content : {}", subject, content);
        } else if (mailServerInfo.isTest()) {
            this.sendTestMail(mailDto, mailServerInfo);
        } else {
            this.sendRealMail(mailDto, mailServerInfo);
        }
    }

    protected void sendTestMail(MailDTO mailDto, MailServerInfo mailServerInfo) {
        String from = mailDto.getFrom();
        String to = mailDto.getTo();
        String subject = mailDto.getSubject();
        String content = mailDto.getContent();
        String address = "";

        try {
            address = hostGenerator.generateLocalAddress();
        } catch (UnknownHostException ex) {
            logger.error(ex.getMessage(), ex);
        }

        String decoratedContent = "address : " + address + "\nfrom : " + from
                + "\nto : " + to + "\n";

        decoratedContent += ("subject : " + subject + "\ncontent : " + content);

        String decoratedSubject = "[test]" + subject;
        String decoratedFrom = address;
        String testMail = mailServerInfo.getTestMail();
        logger.info("send mail from {} to {}", decoratedFrom, testMail);

        logger.info("subject : {}, content : {}", decoratedSubject,
                decoratedContent);
        mailDto.setFrom(decoratedFrom);
        mailDto.setTo(testMail);
        mailDto.setSubject(decoratedSubject);
        mailDto.setContent(decoratedContent);
        this.sendRealMail(mailDto, mailServerInfo);
    }

    protected void sendRealMail(MailDTO mailDto, MailServerInfo mailServerInfo) {
        String from = mailDto.getFrom();
        String to = mailDto.getTo();
        String subject = mailDto.getSubject();
        String content = mailDto.getContent();

        try {
            JavaMailSender javaMailSender = mailServerInfo.getJavaMailSender();
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(content, true);

            if (mailDto.getCc() != null) {
                helper.setCc(mailDto.getCc());
            }

            if (mailDto.getBcc() != null) {
                helper.setBcc(mailDto.getBcc());
            }

            for (Map.Entry<String, InputStreamSource> entry : mailDto
                    .getAttachments().entrySet()) {
                helper.addAttachment(entry.getKey(), entry.getValue());
            }

            javaMailSender.send(msg);
            logger.debug("send mail from {} to {}", from, to);
        } catch (Exception e) {
            logger.error("send mail error", e);
        }
    }

    public MailServerInfo getDefaultMailServerInfo() {
        return defaultMailServerInfo;
    }

    public void setDefaultMailServerInfo(MailServerInfo defaultMailServerInfo) {
        Assert.notNull(defaultMailServerInfo);
        this.defaultMailServerInfo = defaultMailServerInfo;
        mailServerInfoMap.put(defaultMailServerInfo.getName(),
                defaultMailServerInfo);
    }
}
