package com.mossle.core.mail;

import java.net.UnknownHostException;

import java.util.Arrays;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 简单的邮件发送服务.
 * 
 * TODO: 支持附件
 * 
 * @author Lingo
 */
public class MailService {
    /** logger. */
    private static Logger logger = LoggerFactory.getLogger(MailService.class);

    /** java mail sender. */
    private JavaMailSender javaMailSender;

    /** host generator. */
    private HostGenerator hostGenerator = new HostGeneratorImpl();

    // ~ ======================================================================

    /** default from. */
    private String defaultFrom;

    /** default to. */
    private String[] defaultToArray;

    /** default cc. */
    private String[] defaultCcArray;

    /** default bcc. */
    private String[] defaultBccArray;

    /** default subject. */
    private String defaultSubject;

    /** default content. */
    private String defaultContent;

    // ~ ======================================================================
    /** use html. */
    private boolean useHtml = false;

    /** encoding. */
    private String encoding = "UTF-8";

    // ~ ======================================================================
    /**
     * 发送模式.
     * 
     * 如果为false，表示正常发送 如果为true，表示把邮件都发给指定的测试邮箱
     */
    private boolean testMode = false;

    /**
     * 测试模式下发送到的测试邮箱.
     * 
     * demo.mossle@gmail.com;xyz20003@gmail.com也自动处理成了数组
     */
    private String testMail = "demo.mossle@gmail.com";

    // ~ ======================================================================
    /**
     * send message.
     */
    public void send() {
        send(defaultFrom, defaultToArray, defaultSubject, defaultContent);
    }

    /**
     * send message with to.
     */
    public void send(String to) {
        send(defaultFrom, convertArray(to), defaultSubject, defaultContent);
    }

    /**
     * send message with to, subject, content.
     */
    public void send(String to, String subject, String content) {
        send(defaultFrom, convertArray(to), subject, content);
    }

    /**
     * send message with from, to, subject, content.
     */
    public void send(String from, String to, String subject, String content) {
        send(from, convertArray(to), subject, content);
    }

    /**
     * send message with from, to, subject, content.
     */
    public void send(String from, String[] to, String subject, String content) {
        send(from, to, defaultCcArray, defaultBccArray, subject, content);
    }

    /**
     * send message with from, to, cc, bcc, subject, content.
     */
    public void send(String from, String[] to, String[] cc, String[] bcc,
            String subject, String content) {
        if (testMode) {
            this.sendTestMail(from, to, cc, bcc, subject, content);
        } else {
            this.sendRealMail(from, to, cc, bcc, subject, content);
        }
    }

    /**
     * 发送测试用的邮件.
     */
    protected void sendTestMail(String from, String[] to, String[] cc,
            String[] bcc, String subject, String content) {
        String address = "";

        try {
            address = hostGenerator.generateLocalAddress();
        } catch (UnknownHostException ex) {
            logger.error("", ex);
        }

        String decoratedContent = "address : " + address + "\nfrom : " + from
                + "\nto : " + Arrays.asList(to) + "\n";

        if (cc != null) {
            decoratedContent = "cc : " + Arrays.asList(cc) + "\n";
        }

        if (bcc != null) {
            decoratedContent = "bcc : " + Arrays.asList(bcc) + "\n";
        }

        decoratedContent += ("subject : " + subject + "\ncontent : " + content);

        String decoratedSubject = "[test]" + subject;
        String decoratedFrom = address;

        logger.info("send mail from {} to {}", decoratedFrom, testMail);

        logger.info("subject : {}, content : {}", decoratedSubject,
                decoratedContent);
        this.sendRealMail(decoratedFrom, convertArray(testMail), null, null,
                decoratedSubject, decoratedContent);
    }

    /**
     * 实际发送邮件.
     */
    protected void sendRealMail(String from, String[] to, String[] cc,
            String[] bcc, String subject, String content) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true,
                    encoding);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(content, useHtml);

            if (cc != null) {
                helper.setCc(cc);
            }

            if (bcc != null) {
                helper.setBcc(bcc);
            }

            javaMailSender.send(msg);
            logger.debug("send mail from {} to {}", from, to);
        } catch (Exception e) {
            logger.error("send mail error", e);
        }
    }

    // ~ ======================================================================
    public String[] convertArray(String str) {
        if (str == null) {
            return null;
        }

        if (str.indexOf(';') != -1) {
            return str.split(";");
        } else {
            return new String[] { str };
        }
    }

    // ~ ======================================================================
    /**
     * set mail sender.
     */
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void setHostGenerator(HostGenerator hostGenerator) {
        this.hostGenerator = hostGenerator;
    }

    /**
     * set default from.
     */
    public void setDefaultFrom(String defaultFrom) {
        this.defaultFrom = defaultFrom;
    }

    /**
     * set default to.
     */
    public void setDefaultTo(String defaultTo) {
        this.defaultToArray = convertArray(defaultTo);
    }

    /**
     * set default to.
     */
    public void setDefaultTo(String[] defaultTo) {
        if (defaultTo == null) {
            this.defaultToArray = null;
        } else {
            this.defaultToArray = new String[defaultTo.length];
            System.arraycopy(defaultTo, 0, this.defaultToArray, 0,
                    defaultTo.length);
        }
    }

    /**
     * set default cc.
     */
    public void setDefaultCc(String defaultCc) {
        this.defaultCcArray = convertArray(defaultCc);
    }

    /**
     * set default cc.
     */
    public void setDefaultCc(String[] defaultCc) {
        if (defaultCc == null) {
            this.defaultCcArray = null;
        } else {
            this.defaultCcArray = new String[defaultCc.length];
            System.arraycopy(defaultCc, 0, this.defaultCcArray, 0,
                    defaultCc.length);
        }
    }

    /**
     * set default bcc.
     */
    public void setDefaultBcc(String defaultBcc) {
        this.defaultBccArray = convertArray(defaultBcc);
    }

    /**
     * set default bcc.
     */
    public void setDefaultBcc(String[] defaultBcc) {
        if (defaultBcc == null) {
            this.defaultBccArray = null;
        } else {
            this.defaultBccArray = new String[defaultBcc.length];
            System.arraycopy(defaultBcc, 0, this.defaultBccArray, 0,
                    defaultBcc.length);
        }
    }

    /**
     * set default subject.
     */
    public void setDefaultSubject(String defaultSubject) {
        this.defaultSubject = defaultSubject;
    }

    /**
     * set default content.
     */
    public void setDefaultContent(String defaultContent) {
        this.defaultContent = defaultContent;
    }

    // ~ ======================================================================
    /** @return use html. */
    public boolean isUseHtml() {
        return useHtml;
    }

    /**
     * @param set
     *            useHtml boolean.
     */
    public void setUseHtml(boolean useHtml) {
        this.useHtml = useHtml;
    }

    /** @return encoding. */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding
     *            String.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    // ~ ======================================================================
    /** return test mode. */
    public boolean isTestMode() {
        return testMode;
    }

    /** set test mode. */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getTestMail() {
        return testMail;
    }

    /** set test mail. */
    public void setTestMail(String testMail) {
        this.testMail = testMail;
    }
}
