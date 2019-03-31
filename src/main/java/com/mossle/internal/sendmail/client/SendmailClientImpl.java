package com.mossle.internal.sendmail.client;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.client.notification.SendmailClient;

import com.mossle.core.util.BaseDTO;

import com.mossle.spi.rpc.RestClientBuilder;
import com.mossle.spi.rpc.RpcAuthHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class SendmailClientImpl implements SendmailClient {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailClientImpl.class);
    private String baseUrl = "http://localhost:8080/mossle-web-msg";
    private String accessKey = "lemon";
    private String accessSecret = "lemon@2018";
    private RestSendmailClient restSendmailClient;
    private RpcAuthHelper rpcAuthHelper;

    @PostConstruct
    public void init() {
        this.restSendmailClient = new RestClientBuilder().setBaseUrl(baseUrl)
                .setAccessKey(accessKey).setAccessSecret(accessSecret)
                .setRpcAuthHelper(rpcAuthHelper)
                .build(RestSendmailClient.class);
    }

    public BaseDTO sendMail(String to, String templateCode,
            Map<String, Object> data) {
        restSendmailClient.sendMail(to, templateCode, data, null);

        return null;
    }

    @Resource
    public void setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;
    }

    @Value("${client.sendmail.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${client.sendmail.accessKey}")
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @Value("${client.sendmail.accessSecret}")
    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
}
