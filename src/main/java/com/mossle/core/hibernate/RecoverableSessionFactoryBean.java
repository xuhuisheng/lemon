package com.mossle.core.hibernate;

import java.io.IOException;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

public class RecoverableSessionFactoryBean implements FactoryBean,
        DisposableBean, InitializingBean {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(RecoverableSessionFactoryBean.class);
    private SessionFactoryWrapper sessionFactoryWrapper;
    private DataSource dataSource;
    private Properties hibernateProperties;
    private String[] packagesToScan;
    private LocalSessionFactoryBean localSessionFactoryBean;

    public void afterPropertiesSet() throws IOException {
        // init SessionFactoryWrapper
        sessionFactoryWrapper = new SessionFactoryWrapper();

        try {
            // init LocalSessionFactoryBean
            localSessionFactoryBean = new LocalSessionFactoryBean();
            localSessionFactoryBean.setDataSource(dataSource);
            localSessionFactoryBean.setHibernateProperties(hibernateProperties);
            localSessionFactoryBean.setPackagesToScan(packagesToScan);

            localSessionFactoryBean.afterPropertiesSet();

            SessionFactory sessionFactory = localSessionFactoryBean.getObject();
            sessionFactoryWrapper.setSessionFactory(sessionFactory);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public void destroy() {
        sessionFactoryWrapper = null;

        if (localSessionFactoryBean.getObject() != null) {
            localSessionFactoryBean.destroy();
        }
    }

    public Object getObject() {
        return sessionFactoryWrapper;
    }

    public Class getObjectType() {
        return SessionFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    // ~ ======================================================================
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    public void setPackagesToScan(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
