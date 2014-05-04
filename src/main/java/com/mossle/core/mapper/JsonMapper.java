package com.mossle.core.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * json转换.
 */
public class JsonMapper {
    /** logger. */
    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    /** jackson. */
    private ObjectMapper mapper;

    /** constructor. */
    public JsonMapper() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);

            return null;
        }
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if ((jsonString == null) || "".equals(jsonString.trim())) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);

            return null;
        }
    }

    public <T> T fromJson(String jsonString, TypeReference typeReference) {
        if ((jsonString == null) || "".equals(jsonString.trim())) {
            return null;
        }

        try {
            return (T) mapper.readValue(jsonString, typeReference);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);

            return null;
        }
    }

    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }
}
