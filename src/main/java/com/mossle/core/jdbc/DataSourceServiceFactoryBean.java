package com.mossle.core.jdbc;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Properties;

import com.mossle.core.util.BeanUtils;
import com.mossle.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 构造DataSourceService.
 * 
 * @author Lingo
 */
public class DataSourceServiceFactoryBean implements FactoryBean,
        InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory
            .getLogger(DataSourceServiceFactoryBean.class);
    private Properties properties;
    private String defaultPrefix = "db";
    private DataSourceService dataSourceService;

    // ~ ======================================================================
    /**
     * 构建过程.
     * 
     * 1.创建DataSourceService 2.通过properties设置DataSourceService初始化参数 3.通过properties创建每个DataSourceInfo
     * 4.把DataSourceInfo注册到dataSourceService中，并进行初始化
     */
    public void afterPropertiesSet() {
        dataSourceService = new DataSourceService();
        dataSourceService.setLog4jdbcEnabled(this.checkLog4jdbcEnabled());

        Collection<DataSourceInfo> dataSourceInfos = new DbcpDataSourceInfoBuilder(
                defaultPrefix, properties).build();

        for (DataSourceInfo dataSourceInfo : dataSourceInfos) {
            dataSourceService.register(dataSourceInfo);
        }
    }

    public void destroy() {
        if (dataSourceService == null) {
            return;
        }

        for (DataSourceWrapper dataSourceWrapper : dataSourceService
                .getDataSources()) {
            dataSourceWrapper.close();
        }
    }

    // ~ ======================================================================
    public Object getObject() {
        return dataSourceService;
    }

    public Class getObjectType() {
        return DataSourceService.class;
    }

    public boolean isSingleton() {
        return true;
    }

    // ~ ======================================================================
    private boolean checkLog4jdbcEnabled() {
        return Boolean.parseBoolean(properties.getProperty("log4jdbc.enable"));
    }

    // ~ ======================================================================
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setDefaultPrefix(String defaultPrefix) {
        if (StringUtils.isEmpty(defaultPrefix)) {
            logger.info("defaultPrefix cannot be null or empty");

            return;
        }

        this.defaultPrefix = defaultPrefix;
    }

    public String export() {
        StringBuilder buff = new StringBuilder();

        for (DataSourceInfo dataSourceInfo : dataSourceService
                .getDataSourceInfos()) {
            String prefix = defaultPrefix + "." + dataSourceInfo.getName()
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
