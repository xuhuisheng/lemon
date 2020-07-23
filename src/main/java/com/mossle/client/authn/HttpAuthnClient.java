package com.mossle.client.authn;

import java.net.HttpURLConnection;
import java.net.URL;

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

public class HttpAuthnClient implements AuthnClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpAuthnClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public String authenticate(String username, String password, String tenantId) {
        try {
            // TODO: urlencode password
            String url = baseUrl + "/user/rs/remote/authenticate.do"
                    + "?username=" + username + "&password=" + password
                    + "&tenantId=" + tenantId;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            String result = (String) baseDto.getData();

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return AccountStatus.FAILURE;
        }
    }

    public DeviceDTO findDevice(String code) {
        return null;
    }

    public void saveDevice(DeviceDTO deviceDto) {
    }

    @Value("${user.authnClient.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
