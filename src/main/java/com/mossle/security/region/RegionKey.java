package com.mossle.security.region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionKey {
    private static Logger logger = LoggerFactory.getLogger(RegionKey.class);
    private String type;
    private String value;

    public RegionKey(String text) {
        int index = text.indexOf('.');

        if (index == -1) {
            type = text;
            value = "*";
        } else {
            type = text.substring(0, index);
            value = text.substring(index + 1);
        }
    }

    public String getType() {
        return type;
    }

    public String getString() {
        return value;
    }

    public Long getLong() {
        try {
            return Long.valueOf(value);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);

            return Long.valueOf(0L);
        }
    }
}
