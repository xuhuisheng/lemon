package com.mossle.core.dbmigrate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mossle.core.util.PropertiesUtils;
import com.mossle.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseMigrateInfoBuilder {
    public static final int VALID_LENGTH = 3;
    public static final String DEFAULT_PREFIX = "dbmigrate";
    private Logger logger = LoggerFactory
            .getLogger(DatabaseMigrateInfoBuilder.class);
    private String defaultPrefix = DEFAULT_PREFIX;
    private Properties properties;
    private Map<String, DatabaseMigrateInfo> map = new HashMap<String, DatabaseMigrateInfo>();

    public DatabaseMigrateInfoBuilder(Properties properties) {
        this(DEFAULT_PREFIX, properties);
    }

    public DatabaseMigrateInfoBuilder(String defaultPrefix,
            Properties properties) {
        this.defaultPrefix = defaultPrefix;
        this.properties = properties;
    }

    public Collection<DatabaseMigrateInfo> build() {
        logger.debug("defaultPrefix : {}", defaultPrefix);

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (StringUtils.isEmpty(key)) {
                logger.debug("skip empty key");

                continue;
            }

            String[] array = key.split("\\.");

            if (array.length != VALID_LENGTH) {
                logger.debug("skip invalid key : length({}), {}", array.length,
                        key);

                continue;
            }

            String prefix = array[0];
            String name = array[1];
            String property = array[2];

            if (!defaultPrefix.equals(prefix)) {
                logger.debug("prefix not match. skip : {}", prefix);

                continue;
            }

            logger.trace("key : {}, value : {}", key, value);

            this.tryToSetProperty(name, property, value);
        }

        List<DatabaseMigrateInfo> list = new ArrayList<DatabaseMigrateInfo>(
                map.values());
        Collections.sort(list);

        return list;
    }

    public void tryToSetProperty(String name, String propertyName,
            String propertyValue) {
        DatabaseMigrateInfo databaseMigrateInfo = map.get(name);

        if (databaseMigrateInfo == null) {
            databaseMigrateInfo = new DatabaseMigrateInfo();
            databaseMigrateInfo.setName(name);
            map.put(name, databaseMigrateInfo);
        }

        PropertiesUtils.tryToSetProperty(databaseMigrateInfo, propertyName,
                propertyValue);
    }
}
