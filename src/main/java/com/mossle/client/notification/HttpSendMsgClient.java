package com.mossle.client.notification;

import java.net.HttpURLConnection;
import java.net.URL;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpSendMsgClient implements SendMsgClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpSendMsgClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public BaseDTO sendMsg(String from, String to, String content) {
        try {
            String url = baseUrl + "/sendmsg/rs/sendmsg.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            // conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // String payload = jsonMapper.toJson(calendarEventDto);
            String payload = "from=" + from + "&to=" + to + "&content="
                    + content;
            conn.getOutputStream().write(payload.getBytes("utf-8"));

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return baseDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return new BaseDTO();
        }
    }

    // ~
    @Value("${client.sendmsg.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
