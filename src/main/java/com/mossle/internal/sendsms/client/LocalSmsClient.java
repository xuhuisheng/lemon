package com.mossle.internal.sendsms.client;

import java.io.*;

import java.net.*;

import java.util.*;

import javax.annotation.Resource;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.internal.sendsms.service.SendsmsDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

public class LocalSmsClient implements SmsClient {
    private static Logger logger = LoggerFactory
            .getLogger(LocalSmsClient.class);
    private String baseUrl;
    private String configCode;
    private JsonMapper jsonMapper = new JsonMapper();
    private SendsmsDataService sendsmsDataService;

    public void sendSms(String mobile, String message) throws Exception {
        sendsmsDataService.saveSendsmsQueue(mobile, message, configCode);
    }

    @Value("${sms.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${sms.configCode}")
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    @Resource
    public void setSendsmsDataService(SendsmsDataService sendsmsDataService) {
        this.sendsmsDataService = sendsmsDataService;
    }
}
