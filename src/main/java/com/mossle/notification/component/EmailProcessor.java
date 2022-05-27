package com.mossle.notification.component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Properties;

import com.mossle.core.mail.MailDTO;
import com.mossle.core.mail.MailHelper;
import com.mossle.core.mail.MailServerInfo;

import com.mossle.notification.persistence.domain.NotificationMessage;
import com.mossle.notification.persistence.domain.NotificationProvider;

import org.springframework.stereotype.Component;

@Component
public class EmailProcessor {
    public void process(NotificationMessage notificationMessage,
            NotificationProvider notificationProvider) {
        try {
            String from = notificationMessage.getApp();
            String to = notificationMessage.getDestination();
            String content = notificationMessage.getContent();
            String username = notificationProvider.getUsername();
            String password = notificationProvider.getPassword();

            MailServerInfo mailServerInfo = this.buildMailServerInfo(content,
                    username, password);

            MailDTO mailDto = new MailDTO();
            mailDto.setFrom("bot@mossle.com");
            mailDto.setTo(to);
            mailDto.setSubject("subject");
            mailDto.setContent(content);

            MailDTO resultMailDto = new MailHelper().send(mailDto,
                    mailServerInfo);
            System.out.println(resultMailDto.isSuccess());
            System.out.println(resultMailDto.getException());

            notificationMessage.setStatus("success");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public MailServerInfo buildMailServerInfo(String providerConfig,
            String username, String password) throws IOException {
        // String providerConfig = "host=mail.mossle.com\n" + "port=465\n"
        // + "smtpAuth=true\n" + "smtpStarttls=false\n" + "smtpSsl=true\n"
        // + "username=bot@mossle.com\n" + "password=bot2mossle";
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(providerConfig.getBytes()));

        String host = properties.getProperty("host");
        int port = Integer.parseInt(properties.getProperty("port"));
        boolean smtpAuth = Boolean.valueOf(properties.getProperty("smtpAuth"));
        boolean smtpStarttls = Boolean.valueOf(properties
                .getProperty("smtpStarttls"));
        boolean smtpSsl = Boolean.valueOf(properties.getProperty("smtpSsl"));
        MailServerInfo mailServerInfo = new MailServerInfo();
        mailServerInfo.setHost(host);
        mailServerInfo.setPort(port);
        mailServerInfo.setSmtpAuth(smtpAuth);
        mailServerInfo.setSmtpStarttls(smtpStarttls);
        mailServerInfo.setSmtpSsl(smtpSsl);
        mailServerInfo.setUsername(username);
        mailServerInfo.setPassword(password);
        mailServerInfo.setDefaultFrom("bot@mossle.com");
        mailServerInfo.setMode("active");
        mailServerInfo.setTestMail("bot@mossle.com");

        return mailServerInfo;
    }
}
