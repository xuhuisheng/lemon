package com.mossle.core.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceService {
    private Logger logger = LoggerFactory.getLogger(DataSourceService.class);
    private Map<String, DataSourceInfo> dataSourceInfoMap = new HashMap<String, DataSourceInfo>();
    private Map<String, DataSourceWrapper> dataSourceMap = new HashMap<String, DataSourceWrapper>();
    private String defaultName = "default";
    private boolean log4jdbcEnabled;

    // ~ ======================================================================
    public DataSourceWrapper getDataSource() {
        return getDataSource(defaultName);
    }

    public DataSourceWrapper getDataSource(String name) {
        return dataSourceMap.get(name);
    }

    public Collection<DataSourceWrapper> getDataSources() {
        return dataSourceMap.values();
    }

    public boolean isDataSourceExists(String name) {
        return dataSourceMap.containsKey(name);
    }

    public void saveDataSource(String name, DataSourceWrapper dataSource) {
        dataSourceMap.put(name, dataSource);
    }

    public void removeDataSource(String name) {
        dataSourceMap.remove(name);
    }

    public void clearDataSource() {
        dataSourceMap.clear();
    }

    // ~ ======================================================================
    public DataSourceInfo getDataSourceInfo(String name) {
        return dataSourceInfoMap.get(name);
    }

    public Collection<DataSourceInfo> getDataSourceInfos() {
        return dataSourceInfoMap.values();
    }

    public boolean isDataSourceInfoExists(String name) {
        return dataSourceInfoMap.containsKey(name);
    }

    public void saveDataSourceInfo(DataSourceInfo dataSourceInfo) {
        dataSourceInfoMap.put(dataSourceInfo.getName(), dataSourceInfo);
    }

    public void removeDataSourceInfo(String name) {
        dataSourceInfoMap.remove(name);
    }

    public void clearDataSourceInfo() {
        dataSourceInfoMap.clear();
    }

    // ~ ======================================================================
    public void enableLog4jdbc() {
        for (DataSourceWrapper dataSourceWrapper : getDataSources()) {
            dataSourceWrapper.enableLog4jdbc();
        }

        log4jdbcEnabled = true;
    }

    public void disableLog4jdbc() {
        for (DataSourceWrapper dataSourceWrapper : getDataSources()) {
            dataSourceWrapper.disableLog4jdbc();
        }

        log4jdbcEnabled = false;
    }

    public boolean isLog4jdbcEnabled() {
        return log4jdbcEnabled;
    }

    public void setLog4jdbcEnabled(boolean log4jdbcEnabled) {
        this.log4jdbcEnabled = log4jdbcEnabled;
    }

    // ~ ======================================================================
    public void register(DataSourceInfo dataSourceInfo) {
        logger.debug("register : {}", dataSourceInfo);

        String name = dataSourceInfo.getName();
        DataSourceWrapper dataSourceWrapper = dataSourceMap.get(name);

        if (dataSourceWrapper != null) {
            logger.debug("close : {}", dataSourceWrapper);
            dataSourceWrapper.close();
        }

        dataSourceWrapper = new DataSourceWrapper(dataSourceInfo);
        dataSourceWrapper.setLog4jdbcEnabled(log4jdbcEnabled);
        dataSourceWrapper.init();
        dataSourceInfoMap.put(name, dataSourceInfo);
        dataSourceMap.put(name, dataSourceWrapper);
    }
}
