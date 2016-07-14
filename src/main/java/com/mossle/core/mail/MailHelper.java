package com.mossle.core.mail;

import java.net.UnknownHostException;

import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.mossle.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.InputStreamSource;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.util.Assert;

public class MailHelper {
    private static Logger logger = LoggerFactory.getLogger(MailHelper.class);
    private HostGenerator hostGenerator = new HostGeneratorImpl();
    private MailServerInfoCache mailServerInfoCache = new MemoryMailServerInfoCache();

    public MailDTO send(String from, String to, String subject, String content) {
        return this.send(from, to, subject, content,
                this.getDefaultMailServerInfo());
    }

    public MailDTO send(String from, String to, String subject, String content,
            String mailServerInfoName) {
        return this.send(from, to, subject, content,
                mailServerInfoCache.getMailServerInfo(mailServerInfoName));
    }

    public MailDTO send(String from, String to, String subject, String content,
            MailServerInfo mailServerInfo) {
        MailDTO mailDto = new MailDTO();
        mailDto.setFrom(from);
        mailDto.setTo(to);
        mailDto.setSubject(subject);
        mailDto.setContent(content);

        MailDTO resultMailDto = this.send(mailDto, mailServerInfo);

        return resultMailDto;
    }

    public MailDTO send(MailDTO mailDto) {
        logger.debug("send : {}", mailDto);

        return this.send(mailDto, this.getDefaultMailServerInfo());
    }

    public MailDTO send(MailDTO mailDto, MailServerInfo mailServerInfo) {
        try {
            if (mailServerInfo == null) {
                mailServerInfo = this.getDefaultMailServerInfo();
            }

            String from = mailDto.getFrom();
            String to = mailDto.getTo();
            String subject = mailDto.getSubject();
            String content = mailDto.getContent();

            if (StringUtils.isBlank(from)) {
                from = mailServerInfo.getDefaultFrom();
                mailDto.setFrom(from);
            }

            logger.debug("{} : {}", from, to);

            if (mailServerInfo.isSkip()) {
                logger.info("send mail from {} to {}", from, to);

                if (StringUtils.isNotBlank(mailDto.getCc())) {
                    logger.info("cc : " + mailDto.getCc() + "\n");
                }

                if (StringUtils.isNotBlank(mailDto.getBcc())) {
                    logger.info("bcc : " + mailDto.getBcc() + "\n");
                }

                logger.info("subject : {}, content : {}", subject, content);
                mailDto.setSuccess(true);

                return mailDto;
            } else if (mailServerInfo.isTest()) {
                return this.sendTestMail(mailDto, mailServerInfo);
            } else {
                return this.sendRealMail(mailDto, mailServerInfo);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    protected MailDTO sendTestMail(MailDTO mailDto,
            MailServerInfo mailServerInfo) {
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

        if (StringUtils.isNotBlank(mailDto.getCc())) {
            decoratedContent += ("cc : " + mailDto.getCc() + "\n");
            mailDto.setCc(null);
        }

        if (StringUtils.isNotBlank(mailDto.getBcc())) {
            decoratedContent += ("bcc : " + mailDto.getBcc() + "\n");
            mailDto.setBcc(null);
        }

        decoratedContent += ("subject : " + subject + "\ncontent : " + content);

        String decoratedSubject = "[test]" + subject;
        String decoratedFrom = from;
        String testMail = mailServerInfo.getTestMail();
        logger.info("send mail from {} to {}", decoratedFrom, testMail);

        logger.info("subject : {}, content : {}", decoratedSubject,
                decoratedContent);
        mailDto.setFrom(decoratedFrom);
        mailDto.setTo(testMail);
        mailDto.setSubject(decoratedSubject);
        mailDto.setContent(decoratedContent);

        return this.sendRealMail(mailDto, mailServerInfo);
    }

    protected MailDTO sendRealMail(MailDTO mailDto,
            MailServerInfo mailServerInfo) {
        String from = mailDto.getFrom();
        String to = mailDto.getTo();
        String cc = mailDto.getCc();
        String bcc = mailDto.getBcc();
        String subject = mailDto.getSubject();
        String content = mailDto.getContent();

        to = to.replaceAll("\n", ",").replaceAll(";", ",");

        if (StringUtils.isBlank(cc)) {
            cc = null;
        } else {
            cc = cc.replaceAll("\n", ",").replaceAll(";", ",");
        }

        if (StringUtils.isBlank(bcc)) {
            bcc = null;
        } else {
            bcc = bcc.replaceAll("\n", ",").replaceAll(";", ",");
        }

        logger.debug("from : {}, to : {}", from, to);
        logger.debug("cc : {}, bcc : {}", cc, bcc);
        logger.debug("subject : {}", subject);

        try {
            JavaMailSender javaMailSender = mailServerInfo.getJavaMailSender();
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setTo(InternetAddress.parse(to));
            helper.setText(content, true);

            if (StringUtils.isNotBlank(cc)) {
                helper.setCc(InternetAddress.parse(cc));
            }

            if (StringUtils.isNotBlank(bcc)) {
                helper.setBcc(InternetAddress.parse(bcc));
            }

            for (Map.Entry<String, InputStreamSource> entry : mailDto
                    .getInlines().entrySet()) {
                helper.addInline(entry.getKey(), entry.getValue(), "image/png");
            }

            for (Map.Entry<String, InputStreamSource> entry : mailDto
                    .getAttachments().entrySet()) {
                helper.addAttachment(entry.getKey(), entry.getValue());
            }

            javaMailSender.send(msg);
            logger.debug("send mail from {} to {}", from, to);
            mailDto.setSuccess(true);
        } catch (Exception e) {
            logger.error("send mail error", e);
            mailDto.setSuccess(false);

            if (e.getCause() != null) {
                mailDto.setException(e.getCause());
            } else {
                mailDto.setException(e);
            }
        }

        return mailDto;
    }

    public MailServerInfo getDefaultMailServerInfo() {
        return mailServerInfoCache.getDefaultMailServerInfo();
    }

    public void setDefaultMailServerInfo(MailServerInfo defaultMailServerInfo) {
        Assert.notNull(defaultMailServerInfo);
        mailServerInfoCache.setDefaultMailServerInfo(defaultMailServerInfo);
    }
}
