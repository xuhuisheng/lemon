package com.mossle.client.log;

import java.net.HttpURLConnection;
import java.net.URL;

import com.mossle.api.audit.AuditDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpAuditClient implements AuditClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpAuditClient.class);
    private String baseUrl;
    private JsonMapper jsonMapper = new JsonMapper();

    public void log(AuditDTO auditDto) {
        try {
            String url = baseUrl + "/audit/rs/log.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = jsonMapper.toJson(auditDto);
            conn.getOutputStream().write(payload.getBytes("utf-8"));

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            logger.info("baseDto : {}", baseDto.getCode());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Value("${client.audit.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
