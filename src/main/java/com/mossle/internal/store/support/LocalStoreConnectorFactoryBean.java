package com.mossle.internal.store.support;

import javax.activation.DataSource;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;

import com.mossle.ext.store.FileStoreHelper;
import com.mossle.ext.store.StoreHelper;
import com.mossle.ext.store.StoreResult;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class LocalStoreConnectorFactoryBean implements FactoryBean {
    private String baseDir;
    private StoreConnector storeConnector;

    @PostConstruct
    public void afterPropertiesSet() {
        FileStoreHelper fileStoreHelper = new FileStoreHelper();
        fileStoreHelper.setBaseDir(baseDir);

        LocalStoreConnector localStoreConnector = new LocalStoreConnector();
        localStoreConnector.setStoreHelper(fileStoreHelper);
        this.storeConnector = localStoreConnector;
    }

    public Object getObject() {
        return storeConnector;
    }

    public Class getObjectType() {
        return StoreConnector.class;
    }

    public boolean isSingleton() {
        return true;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
