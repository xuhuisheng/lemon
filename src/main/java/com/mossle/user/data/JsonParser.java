package com.mossle.user.data;

import java.io.InputStream;

import java.util.Map;

import com.mossle.core.mapper.JsonMapper;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonParser {
    private static Logger logger = LoggerFactory.getLogger(JsonParser.class);

    public Map<String, Object> parseMap(String filePath, String encoding)
            throws Exception {
        logger.debug("parse map : {}", filePath);

        InputStream is = JsonParser.class.getClassLoader().getResourceAsStream(
                filePath);
        String text = IOUtils.toString(is, encoding);

        return new JsonMapper().fromJson(text, Map.class);
    }
}
