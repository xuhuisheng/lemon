package com.mossle.core.jdbc;

import java.sql.SQLException;

import com.mossle.core.mapper.BeanMapper;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;

import org.apache.commons.dbcp.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dataSource wrapper.
 * 
 * @author Lingo
 */
public class DataSourceWrapper extends DataSourceProxy {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(DataSourceWrapper.class);
    private BasicDataSource basicDataSource;
    private Log4jdbcProxyDataSource log4jdbcProxyDataSource;
    private boolean log4jdbcEnabled;
    private Throwable throwable;
    private DataSourceInfo dataSourceInfo;

    public DataSourceWrapper(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }

    public void init() {
        this.basicDataSource = createBasicDataSource();

        dataSourceInfo.validate();
        new BeanMapper().copy(dataSourceInfo, basicDataSource);

        try {
            new DataSourceChecker().check(basicDataSource);
        } catch (SQLException ex) {
            logger.warn(ex.getMessage(), ex);
            throwable = ex;
        }

        this.log4jdbcProxyDataSource = new Log4jdbcProxyDataSource(
                this.basicDataSource);

        if (this.log4jdbcEnabled) {
            logger.debug("enable log4jdbc");
            this.setTargetDataSource(this.log4jdbcProxyDataSource);
        } else {
            logger.debug("disable log4jdbc");
            this.setTargetDataSource(this.basicDataSource);
        }
    }

    // ~ ======================================================================
    public void enableLog4jdbc() {
        if (!this.log4jdbcEnabled) {
            this.log4jdbcEnabled = true;
            this.setTargetDataSource(this.log4jdbcProxyDataSource);
            logger.info("enable log4jdbc");
        } else {
            logger.info("log4jdbc already enabled");
        }
    }

    public void disableLog4jdbc() {
        if (this.log4jdbcEnabled) {
            this.log4jdbcEnabled = false;
            this.setTargetDataSource(this.basicDataSource);
            logger.info("disable log4jdbc");
        } else {
            logger.info("log4jdbc already disabled");
        }
    }

    public boolean isLog4jdbcEnabled() {
        return log4jdbcEnabled;
    }

    public void setLog4jdbcEnabled(boolean log4jdbcEnabled) {
        this.log4jdbcEnabled = log4jdbcEnabled;
    }

    protected BasicDataSource createBasicDataSource() {
        return new BasicDataSource();
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    // ~ ======================================================================
    public void close() {
        if (basicDataSource != null) {
            try {
                basicDataSource.close();
                basicDataSource = null;
                log4jdbcProxyDataSource = null;
            } catch (SQLException ex) {
                logger.info("close db error", ex);
            }
        }

        throwable = null;
    }

    public void restart() {
        this.close();

        this.init();
    }
}
