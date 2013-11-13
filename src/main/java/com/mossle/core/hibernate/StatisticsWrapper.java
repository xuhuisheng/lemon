package com.mossle.core.hibernate;

import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.NaturalIdCacheStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

public class StatisticsWrapper implements Statistics {
    public void clear() {
    }

    public EntityStatistics getEntityStatistics(String entityName) {
        return null;
    }

    public CollectionStatistics getCollectionStatistics(String role) {
        return null;
    }

    public SecondLevelCacheStatistics getSecondLevelCacheStatistics(
            String regionName) {
        return null;
    }

    public NaturalIdCacheStatistics getNaturalIdCacheStatistics(
            String regionName) {
        return null;
    }

    public QueryStatistics getQueryStatistics(String queryString) {
        return null;
    }

    public long getEntityDeleteCount() {
        return 0;
    }

    public long getEntityInsertCount() {
        return 0;
    }

    public long getEntityLoadCount() {
        return 0;
    }

    public long getEntityFetchCount() {
        return 0;
    }

    public long getEntityUpdateCount() {
        return 0;
    }

    public long getQueryExecutionCount() {
        return 0;
    }

    public long getQueryExecutionMaxTime() {
        return 0;
    }

    public String getQueryExecutionMaxTimeQueryString() {
        return null;
    }

    public long getQueryCacheHitCount() {
        return 0;
    }

    public long getQueryCacheMissCount() {
        return 0;
    }

    public long getQueryCachePutCount() {
        return 0;
    }

    public long getNaturalIdQueryExecutionCount() {
        return 0;
    }

    public long getNaturalIdQueryExecutionMaxTime() {
        return 0;
    }

    public String getNaturalIdQueryExecutionMaxTimeRegion() {
        return null;
    }

    public long getNaturalIdCacheHitCount() {
        return 0;
    }

    public long getNaturalIdCacheMissCount() {
        return 0;
    }

    public long getNaturalIdCachePutCount() {
        return 0;
    }

    public long getUpdateTimestampsCacheHitCount() {
        return 0;
    }

    public long getUpdateTimestampsCacheMissCount() {
        return 0;
    }

    public long getUpdateTimestampsCachePutCount() {
        return 0;
    }

    public long getFlushCount() {
        return 0;
    }

    public long getConnectCount() {
        return 0;
    }

    public long getSecondLevelCacheHitCount() {
        return 0;
    }

    public long getSecondLevelCacheMissCount() {
        return 0;
    }

    public long getSecondLevelCachePutCount() {
        return 0;
    }

    public long getSessionCloseCount() {
        return 0;
    }

    public long getSessionOpenCount() {
        return 0;
    }

    public long getCollectionLoadCount() {
        return 0;
    }

    public long getCollectionFetchCount() {
        return 0;
    }

    public long getCollectionUpdateCount() {
        return 0;
    }

    public long getCollectionRemoveCount() {
        return 0;
    }

    public long getCollectionRecreateCount() {
        return 0;
    }

    public long getStartTime() {
        return 0;
    }

    public void logSummary() {
    }

    public boolean isStatisticsEnabled() {
        return false;
    }

    public void setStatisticsEnabled(boolean b) {
    }

    public String[] getQueries() {
        return null;
    }

    public String[] getEntityNames() {
        return null;
    }

    public String[] getCollectionRoleNames() {
        return null;
    }

    public String[] getSecondLevelCacheRegionNames() {
        return null;
    }

    public long getSuccessfulTransactionCount() {
        return 0;
    }

    public long getTransactionCount() {
        return 0;
    }

    public long getPrepareStatementCount() {
        return 0;
    }

    public long getCloseStatementCount() {
        return 0;
    }

    public long getOptimisticFailureCount() {
        return 0;
    }
}
