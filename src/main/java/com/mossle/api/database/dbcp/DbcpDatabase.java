package com.mossle.api.database.dbcp;

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mossle.api.database.Database;
import com.mossle.api.database.support.DataSourceInfo;
import com.mossle.api.database.support.DataSourceWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbcpDatabase extends AbstractDatabase {
    private Logger logger = LoggerFactory.getLogger(DbcpDatabase.class);
    private Collection<DataSourceInfo> dataSourceInfos;
    private Map<String, DataSourceInfo> dataSourceInfoMap = new HashMap<String, DataSourceInfo>();
    private Map<String, DataSourceWrapper> dataSourceMap = new HashMap<String, DataSourceWrapper>();
    private boolean log4jdbcEnabled;

    public DbcpDatabase(Collection<DataSourceInfo> dataSourceInfos) {
        this.dataSourceInfos = dataSourceInfos;
        this.refreshDataSources();
    }

    @Override
    public DataSource getDataSource(String name) {
        return dataSourceMap.get(name);
    }

    @Override
    public void close() throws IOException {
        for (DataSourceWrapper dataSourceWrapper : getDataSources()) {
            dataSourceWrapper.close();
        }
    }

    public void refreshDataSources() {
        for (DataSourceInfo dataSourceInfo : dataSourceInfos) {
            this.register(dataSourceInfo);
        }
    }

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

    public Collection<DataSourceInfo> getDataSourceInfos() {
        return dataSourceInfos;
    }

    public Collection<DataSourceWrapper> getDataSources() {
        return dataSourceMap.values();
    }
}
