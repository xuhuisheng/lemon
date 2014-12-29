package com.mossle.internal.store.service;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.ext.store.ByteArrayDataSource;
import com.mossle.ext.store.StoreHelper;
import com.mossle.ext.store.StoreResult;

import com.mossle.internal.store.domain.StoreInfo;
import com.mossle.internal.store.manager.StoreInfoManager;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreService {
    private StoreInfoManager storeInfoManager;
    private StoreHelper storeHelper;

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

    @Resource
    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    @Resource
    public void setStoreInfoManager(StoreInfoManager storeInfoManager) {
        this.storeInfoManager = storeInfoManager;
    }
}
