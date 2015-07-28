package com.mossle.internal.store.service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.ext.store.ByteArrayDataSource;
import com.mossle.ext.store.FileStoreHelper;
import com.mossle.ext.store.StoreHelper;
import com.mossle.ext.store.StoreResult;

import com.mossle.internal.store.domain.StoreInfo;
import com.mossle.internal.store.manager.StoreInfoManager;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreService {
    private StoreInfoManager storeInfoManager;
    private StoreHelper storeHelper;
    private String baseDir;

    @PostConstruct
    public void afterPropertiesSet() {
        FileStoreHelper fileStoreHelper = new FileStoreHelper();
        fileStoreHelper.setBaseDir(baseDir);
        this.storeHelper = fileStoreHelper;
    }

    public StoreResult saveStore(String model, String key, String fileName,
            String contentType, byte[] bytes) throws Exception {
        StoreResult storeResult = storeHelper.saveStore(model, key,
                new ByteArrayDataSource(fileName, bytes));
        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setName(fileName);
        storeInfo.setModel(model);
        storeInfo.setPath(storeResult.getKey());
        storeInfo.setCreateTime(new Date());
        storeInfoManager.save(storeInfo);

        return storeResult;
    }

    public StoreResult saveStore(String model, String fileName,
            String contentType, byte[] bytes) throws Exception {
        StoreResult storeResult = storeHelper.saveStore(model,
                new ByteArrayDataSource(fileName, bytes));
        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setName(fileName);
        storeInfo.setModel(model);
        storeInfo.setPath(storeResult.getKey());
        storeInfo.setCreateTime(new Date());
        storeInfoManager.save(storeInfo);

        return storeResult;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    @Resource
    public void setStoreInfoManager(StoreInfoManager storeInfoManager) {
        this.storeInfoManager = storeInfoManager;
    }
}
