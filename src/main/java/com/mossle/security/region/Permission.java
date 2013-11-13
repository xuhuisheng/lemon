package com.mossle.security.region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 三种可能. 如果只包含一部分：默认region=system，把仅有的一部分当做resource，最后opertion默认为* 如果包含了两部分：第一部分为region，第二部分为resource，最后operation为*
 * 如果包含三部分，第一部分为region，第二部分为resource，第三部分为operation
 */
public class Permission {
    public static final int MAX_PART_COUNT = 3;
    private static Logger logger = LoggerFactory.getLogger(Permission.class);
    private String region;
    private String resource;
    private String operation;

    public Permission(String text) {
        String[] array = text.split(":");

        if (array.length == 1) {
            logger.debug("there must 2 or 3 parts in text : [{}]", text);
            this.region = RegionConstants.SYSTEM_REGION;
            this.resource = array[0];
            this.operation = "*";

            return;
        }

        region = array[0];
        resource = array[1];

        if (array.length == MAX_PART_COUNT) {
            operation = array[2];
        } else {
            operation = "*";
        }
    }

    public String getRegion() {
        return region;
    }

    public String getResource() {
        return resource;
    }

    public String getOperation() {
        return operation;
    }
}
