package com.mossle.internal.whitelist.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mossle.api.whitelist.WhitelistConnector;
import com.mossle.api.whitelist.WhitelistDTO;

import com.mossle.core.http.HttpHandler;
import com.mossle.core.http.HttpHandlerImpl;
import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// http://localhost/rs/whitelist/get?code=CAS
public class HttpWhitelistConnector implements WhitelistConnector {
    private static Logger logger = LoggerFactory
            .getLogger(HttpWhitelistConnector.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private HttpHandler httpHandler = new HttpHandlerImpl();
    private String baseUrl;

    public WhitelistDTO getWhitelist(String code, String tenantId) {
        WhitelistDTO result = new WhitelistDTO();
        List<WhitelistDTO> whitelistDtos = this.getWhitelists(code, tenantId);

        for (WhitelistDTO whitelistDto : whitelistDtos) {
            result.getHosts().addAll(whitelistDto.getHosts());
            result.getIps().addAll(whitelistDto.getIps());
        }

        return result;
    }

    public List<WhitelistDTO> getWhitelists(String code, String tenantId) {
        try {
            String text = httpHandler.readText(baseUrl + "?code=" + code
                    + "&tenantId=" + tenantId);
            Map<String, Object> result = jsonMapper.fromJson(text, Map.class);
            logger.debug("result : {}", result);

            List<Map> list = (List<Map>) result.get("data");

            if (list == null) {
                return Collections.emptyList();
            }

            List<WhitelistDTO> whitelistDtos = new ArrayList<WhitelistDTO>();

            for (Map map : list) {
                WhitelistDTO whitelistDto = new WhitelistDTO();
                whitelistDtos.add(whitelistDto);
                whitelistDto.setName((String) map.get("name"));
                whitelistDto.setDescription((String) map.get("description"));
                whitelistDto.setHosts((List<String>) map.get("host"));
                whitelistDto.setIps((List<String>) map.get("ip"));
            }

            return whitelistDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return Collections.emptyList();
        }
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
