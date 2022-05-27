package com.mossle.client.notification;

import java.net.HttpURLConnection;
import java.net.URL;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpNotificationClient implements NotificationClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpNotificationClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public BaseDTO send(String requestId, String catalog, String to,
            String templateCode, String data, int priority, String config) {
        try {
            String url = baseUrl + "/notification/rs/send.do";
            logger.info("url : {}", url);

            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            // conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // String payload = jsonMapper.toJson(calendarEventDto);
            String requestBody = "requestId=" + requestId + "&catalog="
                    + catalog + "&to=" + to + "&templateCode=" + templateCode
                    + "&data=" + data + "&priority=" + priority + "&config="
                    + config;
            logger.info("requestBody : {}", requestBody);
            conn.getOutputStream().write(requestBody.getBytes("utf-8"));

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            logger.info("response : {}", text);

            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return baseDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return new BaseDTO();
        }
    }

    // ~
    @Value("${client.notification.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
