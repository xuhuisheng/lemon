package com.mossle.client.authz;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import com.mossle.api.sys.SysCategoryDTO;
import com.mossle.api.sys.SysInfoDTO;
import com.mossle.api.userauth.ResourceDTO;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpAuthzResourceClient implements AuthzResourceClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpAuthzResourceClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public List<ResourceDTO> findResource(String sysCode) {
        try {
            String url = baseUrl + "/auth/rs/remote/resource/findResources.do"
                    + "?sysCode=" + sysCode;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            String json = jsonMapper.toJson(baseDto.getData());
            List<ResourceDTO> list = (List<ResourceDTO>) jsonMapper.fromJson(
                    json, new TypeReference<List<ResourceDTO>>() {
                    });

            return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    @Value("${auth.authzClient.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
