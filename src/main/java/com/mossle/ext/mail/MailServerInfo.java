package com.mossle.ext.mail;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailServerInfo {
    private static Logger logger = LoggerFactory
            .getLogger(MailServerInfo.class);
    public static final String MODE_NORMAL = "normal";
    public static final String MODE_TEST = "test";
    public static final String MODE_SKIP = "skip";
    private String name;
    private String host;
    private String username;
    private String password;
    private boolean smtpAuth;
    private boolean smtpStarttls;
    private boolean defaultServer;
    private String mode;
    private String testMail;
    private String defaultFrom;
    private JavaMailSenderImpl javaMailSender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public boolean isSmtpStarttls() {
        return smtpStarttls;
    }

    public void setSmtpStarttls(boolean smtpStarttls) {
        this.smtpStarttls = smtpStarttls;
    }

    public boolean isDefaultServer() {
        return defaultServer;
    }

    public void setDefaultServer(boolean defaultServer) {
        this.defaultServer = defaultServer;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTestMail() {
        return testMail;
    }

    public void setTestMail(String testMail) {
        this.testMail = testMail;
    }

    public String getDefaultFrom() {
        return defaultFrom;
    }

    public void setDefaultFrom(String defaultFrom) {
        this.defaultFrom = defaultFrom;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.starttls.enable", smtpStarttls);

        return properties;
    }

    public JavaMailSender getJavaMailSender() {
        if (javaMailSender == null) {
            this.updateJavaMailSender();
        }

        return javaMailSender;
    }

    public void updateJavaMailSender() {
        javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);

        if (smtpAuth) {
            javaMailSender.setUsername(username);
            javaMailSender.setPassword(password);
        }

        javaMailSender.setDefaultEncoding("UTF-8");

        javaMailSender.setJavaMailProperties(this.getProperties());
        logger.info("host : {}", host);
        logger.info("username : {}", username);
        logger.info("password : {}", password);
        logger.info("getProperties : {}", getProperties());
    }

    public boolean isSkip() {
        return MODE_SKIP.equals(mode);
    }

    public boolean isTest() {
        return MODE_TEST.equals(mode);
    }
}
