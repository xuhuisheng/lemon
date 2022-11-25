package com.mossle.bpm.data;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.core.mapper.JsonMapper;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonParser {
    private static Logger logger = LoggerFactory.getLogger(JsonParser.class);
    private List<String[]> list = new ArrayList<String[]>();

    public Map<String, Object> parseMap(String filePath, String encoding)
            throws Exception {
        InputStream is = JsonParser.class.getClassLoader().getResourceAsStream(
                filePath);
        String text = IOUtils.toString(is, encoding);

        return new JsonMapper().fromJson(text, Map.class);
    }

    public List<Map<String, Object>> parseList(String filePath, String encoding)
            throws Exception {
        InputStream is = JsonParser.class.getClassLoader().getResourceAsStream(
                filePath);
        String text = IOUtils.toString(is, encoding);

        return new JsonMapper().fromJson(text, List.class);
    }
}
