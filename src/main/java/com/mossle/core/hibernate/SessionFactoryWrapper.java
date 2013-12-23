package com.mossle.core.hibernate;

import java.io.Serializable;

import java.sql.Connection;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.Cache;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;

import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.UpdateTimestampsCache;

import org.hibernate.cfg.Settings;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;

import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.hibernate.exception.spi.SQLExceptionConverter;

import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;

import org.hibernate.internal.NamedQueryRepository;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;

import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

import org.hibernate.proxy.EntityNotFoundDelegate;

import org.hibernate.service.spi.ServiceRegistryImplementor;

import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;

import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

@SuppressWarnings("deprecation")
public class SessionFactoryWrapper implements SessionFactoryImplementor {
    private SessionFactoryImplementor sessionFactoryImplementor;
    private SpringSessionContext springSessionContext;

    public SessionFactoryWrapper() {
        springSessionContext = new SpringSessionContext(this);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        if (sessionFactory instanceof SessionFactoryImplementor) {
            this.sessionFactoryImplementor = (SessionFactoryImplementor) sessionFactory;
        } else {
            throw new IllegalStateException("the type of sessionFactory["
                    + sessionFactory + "] is not SessionFactoryImplementor");
        }
    }

    // ~ ======================================================================
    public SessionFactoryOptions getSessionFactoryOptions() {
        return sessionFactoryImplementor.getSessionFactoryOptions();
    }

    public Session openSession() throws HibernateException {
        return sessionFactoryImplementor.openSession();
    }

    /**
     * 为了解决OpenSessionOnView注册SessionFactory引用的问题，从context中获得当前session. 之前是通过
     * <code>sessionFactory.getCurrentSession()</code>直接获得
     */
    public Session getCurrentSession() throws HibernateException {
        return springSessionContext.currentSession();
    }

    public StatelessSessionBuilder withStatelessOptions() {
        return sessionFactoryImplementor.withStatelessOptions();
    }

    public StatelessSession openStatelessSession() {
        return sessionFactoryImplementor.openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection) {
        return sessionFactoryImplementor.openStatelessSession(connection);
    }

    public ClassMetadata getClassMetadata(Class entityClass) {
        return sessionFactoryImplementor.getClassMetadata(entityClass);
    }

    public ClassMetadata getClassMetadata(String entityName) {
        return sessionFactoryImplementor.getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) {
        return sessionFactoryImplementor.getCollectionMetadata(roleName);
    }

    public Map<String, ClassMetadata> getAllClassMetadata() {
        return sessionFactoryImplementor.getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() {
        return sessionFactoryImplementor.getAllCollectionMetadata();
    }

    /**
     * 此处需要进行封装，如果sessionFactory没有构建成功，就自己新建. 否则应该使用<code>sessionFactory.getStatistics()</code>
     */
    public Statistics getStatistics() {
        return new StatisticsWrapper();
    }

    public void close() throws HibernateException {
        sessionFactoryImplementor.close();
    }

    public boolean isClosed() {
        return sessionFactoryImplementor.isClosed();
    }

    public Cache getCache() {
        return sessionFactoryImplementor.getCache();
    }

    public void evict(Class persistentClass) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evict(Class persistentClass, Serializable id)
            throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evictEntity(String entityName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evictEntity(String entityName, Serializable id)
            throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evictCollection(String roleName) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evictCollection(String roleName, Serializable id)
            throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evictQueries(String cacheRegion) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public void evictQueries() throws HibernateException {
        throw new UnsupportedOperationException();
    }

    public Set getDefinedFilterNames() {
        return sessionFactoryImplementor.getDefinedFilterNames();
    }

    public FilterDefinition getFilterDefinition(String filterName)
            throws HibernateException {
        return sessionFactoryImplementor.getFilterDefinition(filterName);
    }

    public boolean containsFetchProfileDefinition(String name) {
        return sessionFactoryImplementor.containsFetchProfileDefinition(name);
    }

    public TypeHelper getTypeHelper() {
        return sessionFactoryImplementor.getTypeHelper();
    }

    // ~ ======================================================================
    public Reference getReference() throws NamingException {
        return sessionFactoryImplementor.getReference();
    }

    // ~ ======================================================================
    public SessionBuilderImplementor withOptions() {
        return sessionFactoryImplementor.withOptions();
    }

    public TypeResolver getTypeResolver() {
        return sessionFactoryImplementor.getTypeResolver();
    }

    public Properties getProperties() {
        return sessionFactoryImplementor.getProperties();
    }

    public EntityPersister getEntityPersister(String entityName)
            throws MappingException {
        return sessionFactoryImplementor.getEntityPersister(entityName);
    }

    public Map<String, EntityPersister> getEntityPersisters() {
        return sessionFactoryImplementor.getEntityPersisters();
    }

    public CollectionPersister getCollectionPersister(String role)
            throws MappingException {
        return sessionFactoryImplementor.getCollectionPersister(role);
    }

    public Map<String, CollectionPersister> getCollectionPersisters() {
        return sessionFactoryImplementor.getCollectionPersisters();
    }

    public JdbcServices getJdbcServices() {
        return sessionFactoryImplementor.getJdbcServices();
    }

    public Dialect getDialect() {
        return sessionFactoryImplementor.getDialect();
    }

    public Interceptor getInterceptor() {
        return sessionFactoryImplementor.getInterceptor();
    }

    public QueryPlanCache getQueryPlanCache() {
        return sessionFactoryImplementor.getQueryPlanCache();
    }

    public Type[] getReturnTypes(String queryString) throws HibernateException {
        return sessionFactoryImplementor.getReturnTypes(queryString);
    }

    public String[] getReturnAliases(String queryString)
            throws HibernateException {
        return sessionFactoryImplementor.getReturnAliases(queryString);
    }

    public ConnectionProvider getConnectionProvider() {
        return sessionFactoryImplementor.getConnectionProvider();
    }

    public String[] getImplementors(String className) throws MappingException {
        return sessionFactoryImplementor.getImplementors(className);
    }

    public String getImportedClassName(String name) {
        return sessionFactoryImplementor.getImportedClassName(name);
    }

    public QueryCache getQueryCache() {
        return sessionFactoryImplementor.getQueryCache();
    }

    public QueryCache getQueryCache(String regionName)
            throws HibernateException {
        return sessionFactoryImplementor.getQueryCache(regionName);
    }

    public UpdateTimestampsCache getUpdateTimestampsCache() {
        return sessionFactoryImplementor.getUpdateTimestampsCache();
    }

    public StatisticsImplementor getStatisticsImplementor() {
        return sessionFactoryImplementor.getStatisticsImplementor();
    }

    public NamedQueryDefinition getNamedQuery(String queryName) {
        return sessionFactoryImplementor.getNamedQuery(queryName);
    }

    public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
        return sessionFactoryImplementor.getNamedSQLQuery(queryName);
    }

    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return sessionFactoryImplementor.getResultSetMapping(name);
    }

    public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
        return sessionFactoryImplementor.getIdentifierGenerator(rootEntityName);
    }

    public Region getSecondLevelCacheRegion(String regionName) {
        return sessionFactoryImplementor.getSecondLevelCacheRegion(regionName);
    }

    public Region getNaturalIdCacheRegion(String regionName) {
        return sessionFactoryImplementor.getNaturalIdCacheRegion(regionName);
    }

    public Map getAllSecondLevelCacheRegions() {
        return sessionFactoryImplementor.getAllSecondLevelCacheRegions();
    }

    public SQLExceptionConverter getSQLExceptionConverter() {
        return sessionFactoryImplementor.getSQLExceptionConverter();
    }

    public SqlExceptionHelper getSQLExceptionHelper() {
        return sessionFactoryImplementor.getSQLExceptionHelper();
    }

    public Settings getSettings() {
        return sessionFactoryImplementor.getSettings();
    }

    public Session openTemporarySession() throws HibernateException {
        return sessionFactoryImplementor.openTemporarySession();
    }

    public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
        return sessionFactoryImplementor
                .getCollectionRolesByEntityParticipant(entityName);
    }

    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return sessionFactoryImplementor.getEntityNotFoundDelegate();
    }

    public SQLFunctionRegistry getSqlFunctionRegistry() {
        return sessionFactoryImplementor.getSqlFunctionRegistry();
    }

    public FetchProfile getFetchProfile(String name) {
        return sessionFactoryImplementor.getFetchProfile(name);
    }

    public ServiceRegistryImplementor getServiceRegistry() {
        return sessionFactoryImplementor.getServiceRegistry();
    }

    public void addObserver(SessionFactoryObserver observer) {
        sessionFactoryImplementor.addObserver(observer);
    }

    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
        return sessionFactoryImplementor.getCustomEntityDirtinessStrategy();
    }

    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return sessionFactoryImplementor.getCurrentTenantIdentifierResolver();
    }

    // ~ ======================================================================
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return sessionFactoryImplementor.getIdentifierGeneratorFactory();
    }

    public Type getIdentifierType(String className) throws MappingException {
        return sessionFactoryImplementor.getIdentifierType(className);
    }

    public String getIdentifierPropertyName(String className)
            throws MappingException {
        return sessionFactoryImplementor.getIdentifierPropertyName(className);
    }

    public Type getReferencedPropertyType(String className, String propertyName)
            throws MappingException {
        return sessionFactoryImplementor.getReferencedPropertyType(className,
                propertyName);
    }

    public NamedQueryRepository getNamedQueryRepository() {
        return sessionFactoryImplementor.getNamedQueryRepository();
    }

    public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
        return sessionFactoryImplementor.iterateEntityNameResolvers();
    }

    public void registerNamedSQLQueryDefinition(String name,
            NamedSQLQueryDefinition definition) {
        sessionFactoryImplementor.registerNamedSQLQueryDefinition(name,
                definition);
    }

    public void registerNamedQueryDefinition(String name,
            NamedQueryDefinition definition) {
        sessionFactoryImplementor
                .registerNamedQueryDefinition(name, definition);
    }
}
