package com.mossle.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

public class MultipartHandler {
    private static Logger logger = LoggerFactory
            .getLogger(MultipartHandler.class);
    private MultipartResolver multipartResolver;
    private MultipartHttpServletRequest multipartHttpServletRequest = null;
    private MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
    private MultiValueMap<String, MultipartFile> multiFileMap;

    public MultipartHandler(MultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    public void handle(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            logger.debug("force cast to MultipartHttpServletRequest");

            MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
            this.multiFileMap = req.getMultiFileMap();
            logger.debug("multiFileMap : {}", multiFileMap);
            this.handleMultiValueMap(req);
            logger.debug("multiValueMap : {}", multiValueMap);

            return;
        }

        if (multipartResolver.isMultipart(request)) {
            logger.debug("is multipart : {}",
                    multipartResolver.isMultipart(request));
            this.multipartHttpServletRequest = multipartResolver
                    .resolveMultipart(request);

            logger.debug("multipartHttpServletRequest : {}",
                    multipartHttpServletRequest);
            this.multiFileMap = multipartHttpServletRequest.getMultiFileMap();
            logger.debug("multiFileMap : {}", multiFileMap);
            this.handleMultiValueMap(multipartHttpServletRequest);
            logger.debug("multiValueMap : {}", multiValueMap);
        } else {
            this.handleMultiValueMap(request);
            logger.debug("multiValueMap : {}", multiValueMap);
        }
    }

    public void clear() {
        if (multipartHttpServletRequest == null) {
            return;
        }

        multipartResolver.cleanupMultipart(multipartHttpServletRequest);
    }

    public void handleMultiValueMap(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();

            for (String value : entry.getValue()) {
                multiValueMap.add(key, value);
            }
        }
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        return multiValueMap;
    }

    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return multiFileMap;
    }
}
