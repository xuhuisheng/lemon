package com.mossle.api.database.dbcp;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.mossle.api.database.Database;
import com.mossle.api.database.DatabaseBuilder;
import com.mossle.api.database.support.DataSourceInfo;

import com.mossle.core.util.BeanUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbcpDatabaseBuilder extends DatabaseBuilder<DbcpDatabaseBuilder> {
    private static Logger logger = LoggerFactory
            .getLogger(DbcpDatabaseBuilder.class);
    private DbcpDatabase dbcpDatabase;

    @Override
    public Database build() {
        initPropeties();

        Collection<DataSourceInfo> dataSourceInfos = new DbcpDataSourceInfoBuilder(
                getDefaultPrefix(), getProperties()).build();

        dbcpDatabase = new DbcpDatabase(dataSourceInfos);

        return dbcpDatabase;
    }

    // ~
    public void initPropeties() {
        if (getProperties() != null) {
            return;
        }

        Properties properties = new Properties();
        this.setProperties(properties);

        try {
            properties.load(DbcpDatabaseBuilder.class.getClassLoader()
                    .getResourceAsStream("application.properties"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private boolean checkLog4jdbcEnabled() {
        return Boolean.parseBoolean(getProperties().getProperty(
                "log4jdbc.enable"));
    }

    public String export() {
        StringBuilder buff = new StringBuilder();

        for (DataSourceInfo dataSourceInfo : dbcpDatabase.getDataSourceInfos()) {
            String prefix = getDefaultPrefix() + "." + dataSourceInfo.getName()
                    + ".";

            for (Method method : dataSourceInfo.getClass().getDeclaredMethods()) {
                String methodName = method.getName();

                if (methodName.startsWith("get") || methodName.startsWith("is")) {
                    String fieldName = BeanUtils.getFieldName(methodName);
                    Object fieldValue = BeanUtils.safeInvokeMethod(
                            dataSourceInfo, method);
                    buff.append(prefix).append(fieldName).append("=")
                            .append(fieldValue).append("\n");
                }
            }
        }

        return buff.toString();
    }
}
