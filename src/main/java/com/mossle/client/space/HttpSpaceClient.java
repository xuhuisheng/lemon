package com.mossle.client.space;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpSpaceClient implements SpaceClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpSpaceClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public List<SpaceDTO> findBuildings() {
        try {
            String url = baseUrl + "/space/rs/buildings.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            // conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(false);

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return (List<SpaceDTO>) baseDto.getData();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            throw new RuntimeException(ex);
        }
    }

    public List<SpaceDTO> findLocationsByBuildingCode(String buildingCode) {
        try {
            String url = baseUrl + "/space/rs/locations.do?code="
                    + buildingCode;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            // conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(false);

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return (List<SpaceDTO>) baseDto.getData();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            throw new RuntimeException(ex);
        }
    }

    // ~
    @Value("${client.sendmsg.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
