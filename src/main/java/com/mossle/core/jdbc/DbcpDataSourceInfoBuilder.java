package com.mossle.core.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.mossle.core.util.PropertiesUtils;
import com.mossle.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbcpDataSourceInfoBuilder {
    public static final int VALID_LENGTH = 3;
    private Logger logger = LoggerFactory
            .getLogger(DbcpDataSourceInfoBuilder.class);
    private String defaultPrefix = "db";
    private Properties properties;
    private Map<String, DataSourceInfo> map = new HashMap<String, DataSourceInfo>();

    public DbcpDataSourceInfoBuilder(String defaultPrefix, Properties properties) {
        this.defaultPrefix = defaultPrefix;
        this.properties = properties;
    }

    public Collection<DataSourceInfo> build() {
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

            this.tryToSetProperty(name, property, value);
        }

        return map.values();
    }

    public void tryToSetProperty(String name, String propertyName,
            String propertyValue) {
        DataSourceInfo dataSourceInfo = map.get(name);

        if (dataSourceInfo == null) {
            dataSourceInfo = new DbcpDataSourceInfo();
            dataSourceInfo.setName(name);
            map.put(name, dataSourceInfo);
        }

        PropertiesUtils.tryToSetProperty(dataSourceInfo, propertyName,
                propertyValue);
    }
}
