package com.mossle.client.open;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import com.mossle.api.user.AccountStatus;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.spi.device.DeviceDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpOpenClient implements OpenClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpOpenClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public OpenAppDTO getApp(String clientId) {
        try {
            // TODO: urlencode password
            String url = baseUrl + "/open/rs/remote/getApp.do" + "?clientId="
                    + clientId;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Object result = baseDto.getData();
            OpenAppDTO openAppDto = jsonMapper.fromJson(
                    jsonMapper.toJson(result), OpenAppDTO.class);

            return openAppDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public List<OpenAppDTO> getAll() {
        try {
            String url = baseUrl + "/open/rs/remote/getAll.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Object result = baseDto.getData();
            List<OpenAppDTO> openAppDtos = jsonMapper.fromJson(
                    jsonMapper.toJson(result), new TypeReference<OpenAppDTO>() {
                    });

            return openAppDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public List<OpenAppDTO> findUserApps(String userId) {
        try {
            String url = baseUrl + "/open/rs/remote/findUserApps.do?userId"
                    + userId;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Object result = baseDto.getData();
            List<OpenAppDTO> openAppDtos = jsonMapper.fromJson(
                    jsonMapper.toJson(result), new TypeReference<OpenAppDTO>() {
                    });

            return openAppDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public List<OpenAppDTO> findGroupApps(String groupCode) {
        try {
            String url = baseUrl + "/open/rs/remote/findGroupApps.do?groupCode"
                    + groupCode;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Object result = baseDto.getData();
            List<OpenAppDTO> openAppDtos = jsonMapper.fromJson(
                    jsonMapper.toJson(result), new TypeReference<OpenAppDTO>() {
                    });

            return openAppDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public SysDTO findSys(String code) {
        try {
            String url = baseUrl + "/open/rs/remote/findSys.do?code" + code;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Object result = baseDto.getData();
            SysDTO sysDto = jsonMapper.fromJson(jsonMapper.toJson(result),
                    SysDTO.class);

            return sysDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    // ~
    @Value("${open.openClient.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
