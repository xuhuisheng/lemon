package com.mossle.internal.mail.client;

import java.io.*;

import java.net.*;

import java.util.*;

import javax.annotation.Resource;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.internal.mail.service.MailDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

public class LocalMailClient implements MailClient {
    private static Logger logger = LoggerFactory
            .getLogger(LocalMailClient.class);
    private String configCode;
    private MailDataService mailDataService;
    private JsonMapper jsonMapper = new JsonMapper();

    public void sendMail(String to, String templateCode,
            Map<String, Object> parameters) throws Exception {
        mailDataService.sendTemplate(to, jsonMapper.toJson(parameters),
                templateCode, configCode);
    }

    @Value("${mail.configCode}")
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    @Resource
    public void setMailDataService(MailDataService mailDataService) {
        this.mailDataService = mailDataService;
    }
}
