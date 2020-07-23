package com.mossle.client.mdm;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import com.mossle.api.sys.SysCategoryDTO;
import com.mossle.api.sys.SysInfoDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpSysClient implements SysClient {
    private static Logger logger = LoggerFactory.getLogger(HttpSysClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public List<SysCategoryDTO> findAll() {
        try {
            String url = baseUrl + "/sys/rs/remote/findAll.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            String json = jsonMapper.toJson(baseDto.getData());
            List<SysCategoryDTO> sysCategoryDtos = (List<SysCategoryDTO>) jsonMapper
                    .fromJson(json, new TypeReference<List<SysCategoryDTO>>() {
                    });

            return sysCategoryDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public List<SysInfoDTO> findFavorites() {
        try {
            String url = baseUrl + "/sys/rs/remote/findFavorites.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            String json = jsonMapper.toJson(baseDto.getData());
            List<SysInfoDTO> sysInfoDtos = (List<SysInfoDTO>) jsonMapper
                    .fromJson(json, new TypeReference<List<SysInfoDTO>>() {
                    });

            return sysInfoDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    @Value("${sys.sysClient.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
