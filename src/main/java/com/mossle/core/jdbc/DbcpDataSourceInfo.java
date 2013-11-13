package com.mossle.core.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbcpDataSourceInfo implements DataSourceInfo {
    private static Logger logger = LoggerFactory
            .getLogger(DbcpDataSourceInfo.class);
    public static final int DEFAULT_MAX_ACTIVE = 8;
    public static final int DEFAULT_MAX_IDLE = 8;
    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
    public static final int DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLS = 1000 * 60 * 30;
    public static final int DEFAULT_REMOVE_ABANDONED_TIMEOUT = 300;
    private String name;
    private String username;
    private String password;
    private String url;
    private String driverClassName;
    private boolean defaultAutoCommit = true;
    private boolean defaultReadOnly = false;
    private int defaultTransactionIsolation = -1;
    private String defaultCatalog;
    private int initialSize = 0;
    private int maxActive = DEFAULT_MAX_ACTIVE;
    private int maxIdle = DEFAULT_MAX_IDLE;
    private int minIdle = 0;
    private int maxWait = -1;
    private String validationQuery;
    private boolean testOnBorrow = true;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private int timeBetweenEvictionRunsMillis = -1;
    private int numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    private int minEvictableIdleTimeMills = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLS;
    private String connectionInitSqls;
    private boolean poolPreparedStatements = false;
    private int maxOpenPreparedStatements = -1;
    private boolean accessToUnderlyingConnectionAllowed = false;
    private boolean removeAbandoned = false;
    private int removeAbandonedTimeout = DEFAULT_REMOVE_ABANDONED_TIMEOUT;
    private boolean logAbandoned = false;

    // ~ ======================================================================
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void validate() {
        if (driverClassName == null) {
            logger.warn("{} : driverClassName shouldnot be null", name);
        }

        if (url == null) {
            logger.warn("{} : url shouldnot be null", name);
        }

        if (username == null) {
            logger.warn("{} : username shouldnot be null", name);
        }

        if (password == null) {
            logger.warn("{} : password shouldnot be null", name);
        }
    }

    // ~ ======================================================================
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public boolean isDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(
            int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public int getMinEvictableIdleTimeMills() {
        return minEvictableIdleTimeMills;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMills) {
        this.minEvictableIdleTimeMills = minEvictableIdleTimeMills;
    }

    public String getConnectionInitSqls() {
        return connectionInitSqls;
    }

    public void setConnectionInitSqls(String connectionInitSqls) {
        this.connectionInitSqls = connectionInitSqls;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public void setmaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public void setAccessToUnderlyingConnectionAllowed(
            boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }
}
