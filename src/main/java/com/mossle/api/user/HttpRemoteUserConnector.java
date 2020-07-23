package com.mossle.api.user;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpRemoteUserConnector implements RemoteUserConnector {
    private static Logger logger = LoggerFactory
            .getLogger(HttpRemoteUserConnector.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public UserDTO findById(String userId, String userRepoRef) {
        try {
            String url = baseUrl + "/user/rs/remote/findById.do?id=" + userId
                    + "&userRepoRef=" + userRepoRef;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            String json = jsonMapper.toJson(baseDto.getData());
            UserDTO userDto = (UserDTO) jsonMapper
                    .fromJson(json, UserDTO.class);

            return userDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        try {
            String url = baseUrl
                    + "/user/rs/remote/findByUsername.do?username=" + username
                    + "&userRepoRef=" + userRepoRef;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            String json = jsonMapper.toJson(baseDto.getData());
            UserDTO userDto = (UserDTO) jsonMapper
                    .fromJson(json, UserDTO.class);

            return userDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public boolean authenticate(String username, String password) {
        try {
            String url = baseUrl + "/user/rs/remote/authenticate.do?username="
                    + username + "&password=" + password;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Boolean result = (Boolean) baseDto.getData();

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return false;
        }
    }

    public List<UserDTO> search(String query) {
        try {
            String url = baseUrl + "/user/rs/remote/search.do?query=" + query;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);
            Object result = baseDto.getData();
            String jsonText = jsonMapper.toJson(result);
            List<UserDTO> userDtos = jsonMapper.fromJson(jsonText,
                    new TypeReference<List<UserDTO>>() {
                    });

            return userDtos;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return Collections.emptyList();
        }
    }

    // ~
    @Value("${user.remoteUserConnector.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
