package com.mossle.internal.store;

import java.io.*;

import java.text.SimpleDateFormat;

import java.util.*;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.internal.StoreDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.IoUtils;

import com.mossle.ext.store.StoreHelper;
import com.mossle.ext.store.StoreResult;

import com.mossle.internal.store.domain.StoreInfo;
import com.mossle.internal.store.manager.StoreInfoManager;
import com.mossle.internal.store.service.StoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.InputStreamResource;

import org.springframework.stereotype.Component;

import org.springframework.util.FileCopyUtils;

public class StoreConnectorImpl implements
        com.mossle.api.internal.StoreConnector {
    private Logger logger = LoggerFactory.getLogger(StoreConnectorImpl.class);
    private StoreHelper storeHelper;
    private StoreService storeService;
    private StoreInfoManager storeInfoManager;

    public StoreDTO saveStore(String model, DataSource dataSource)
            throws Exception {
        int len = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        InputStream is = dataSource.getInputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        is.close();

        byte[] bytes = baos2.toByteArray();

        StoreResult storeResult = storeService.saveStore(model,
                dataSource.getName(), dataSource.getContentType(), bytes);

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(storeResult.getModel());
        storeDto.setKey(storeResult.getKey());
        storeDto.setDataSource(storeResult.getDataSource());

        return storeDto;
    }

    public StoreDTO saveStore(String model, String key, DataSource dataSource)
            throws Exception {
        int len = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        InputStream is = dataSource.getInputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        is.close();

        byte[] bytes = baos2.toByteArray();

        StoreResult storeResult = storeService.saveStore(model, key,
                dataSource.getName(), dataSource.getContentType(), bytes);

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(storeResult.getModel());
        storeDto.setKey(storeResult.getKey());
        storeDto.setDataSource(storeResult.getDataSource());

        return storeDto;
    }

    public StoreDTO getStore(String model, String key) throws Exception {
        StoreResult storeResult = storeHelper.getStore(model, key);

        if (storeResult == null) {
            return null;
        }

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(storeResult.getModel());
        storeDto.setKey(storeResult.getKey());
        storeDto.setDataSource(storeResult.getDataSource());

        StoreInfo storeInfo = storeInfoManager.findUniqueBy("path", key);

        if (storeInfo == null) {
            storeDto.setDisplayName(key);
        } else {
            storeDto.setDisplayName(storeInfo.getName());
        }

        return storeDto;
    }

    public void removeStore(String model, String key) throws Exception {
        storeHelper.removeStore(model, key);
    }

    @Resource
    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    @Resource
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    @Resource
    public void setStoreInfoManager(StoreInfoManager storeInfoManager) {
        this.storeInfoManager = storeInfoManager;
    }
}
