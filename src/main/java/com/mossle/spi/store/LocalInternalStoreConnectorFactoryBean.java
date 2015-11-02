package com.mossle.spi.store;

import javax.annotation.PostConstruct;

import com.mossle.core.store.FileStoreHelper;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

public class LocalInternalStoreConnectorFactoryBean implements FactoryBean {
    private String baseDir;
    private InternalStoreConnector internalStoreConnector;

    @PostConstruct
    public void afterPropertiesSet() {
        FileStoreHelper fileStoreHelper = new FileStoreHelper();
        fileStoreHelper.setBaseDir(baseDir);

        LocalInternalStoreConnector localInternalStoreConnector = new LocalInternalStoreConnector();
        localInternalStoreConnector.setStoreHelper(fileStoreHelper);
        this.internalStoreConnector = localInternalStoreConnector;
    }

    public Object getObject() {
        return internalStoreConnector;
    }

    public Class getObjectType() {
        return InternalStoreConnector.class;
    }

    public boolean isSingleton() {
        return true;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
