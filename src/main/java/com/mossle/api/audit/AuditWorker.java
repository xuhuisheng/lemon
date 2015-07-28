package com.mossle.api.audit;

import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Date;

public class AuditWorker implements Runnable {
    private String app;
    private String clientIp;
    private String serverIp;
    private String username;
    private String result;
    private String description;

    public void run() {
        String url = "http://otp.mioffice.cn:8000/rs/audit";
        StringBuilder buff = new StringBuilder();
        buff.append("user=").append(username);
        buff.append("&resourceType=");
        buff.append("&resourceId=");
        buff.append("&action=");
        buff.append("&result=").append(result);
        buff.append("&application=").append(app);
        buff.append("&auditTime=").append(
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
                        .format(new Date()));
        buff.append("&client=").append(clientIp);
        buff.append("&server=").append(serverIp);
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

            // System.out.println(conn.getResponseCode());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
