package com.mossle.api.user;

import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountLogWorker implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLogWorker.class);
    private String url;
    private String application;
    private String client;
    private String server;
    private String username;
    private String result;
    private String reason;
    private String description;

    public void run() {
        StringBuilder buff = new StringBuilder();
        buff.append("username=").append(username);
        buff.append("&result=").append(result);
        buff.append("&reason=").append(reason);
        buff.append("&application=").append(application);
        buff.append("&logTime=").append(
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
                        .format(new Date()));
        buff.append("&client=").append(client);
        buff.append("&server=").append(server);
        buff.append("&description=").append(description);

        try {
            // System.out.println(buff);
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(buff.toString().getBytes());
            // conn.getOutputStream().flush();
            conn.getResponseCode();

            logger.debug("response code : {}", conn.getResponseCode());
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
