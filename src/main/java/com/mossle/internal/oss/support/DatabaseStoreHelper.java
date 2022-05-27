package com.mossle.internal.oss.support;

import java.io.InputStream;

import java.sql.Blob;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import javax.sql.DataSource;

import com.mossle.core.store.ByteArrayDataSource;
import com.mossle.core.store.StoreHelper;
import com.mossle.core.store.StoreResult;

import com.mossle.internal.oss.persistence.domain.OssData;
import com.mossle.internal.oss.persistence.manager.OssDataManager;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.datasource.DataSourceUtils;

public class DatabaseStoreHelper implements StoreHelper {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseStoreHelper.class);
    private OssDataManager ossDataManager;
    private DataSource dataSource;

    public StoreResult getStore(String model, String key) throws Exception {
        if (key == null) {
            logger.info("key cannot be null");

            return null;
        }

        if (key.indexOf("../") != -1) {
            StoreResult storeResult = new StoreResult();
            storeResult.setModel(model);
            storeResult.setKey(key);

            return storeResult;
        }

        String code = model + "/" + key;
        OssData ossData = ossDataManager.findUniqueBy("code", code);

        if (ossData == null) {
            logger.info("cannot find : {}", code);

            return null;
        }

        Blob data = ossData.getData();

        if (data == null) {
            logger.info("cannot find : {}", code);

            return null;
        }

        InputStream is = data.getBinaryStream();
        byte[] bytes = IOUtils.toByteArray(is);

        StoreResult storeResult = new StoreResult();
        storeResult.setModel(model);
        storeResult.setKey(key);
        storeResult.setDataSource(new ByteArrayDataSource(bytes));

        return storeResult;
    }

    public void removeStore(String model, String key) throws Exception {
        if (key.indexOf("../") != -1) {
            return;
        }

        String code = model + "/" + key;
        OssData ossData = ossDataManager.findUniqueBy("code", code);

        if (ossData == null) {
            logger.info("cannot find : {}", code);

            return;
        }

        ossDataManager.remove(ossData);
    }

    public StoreResult saveStore(String model,
            javax.activation.DataSource inputDataSource) throws Exception {
        String prefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String suffix = this.getSuffix(inputDataSource.getName());
        String path = prefix + "/" + UUID.randomUUID() + suffix;

        String code = model + "/" + path;

        byte[] bytes = IOUtils.toByteArray(inputDataSource.getInputStream());
        Blob blob = DataSourceUtils.getConnection(dataSource).createBlob();
        blob.setBytes(1, bytes);

        OssData ossData = new OssData();
        ossData.setCode(code);
        ossData.setData(blob);
        ossDataManager.save(ossData);

        StoreResult storeResult = new StoreResult();
        storeResult.setModel(model);
        storeResult.setKey(path);
        storeResult.setDataSource(new ByteArrayDataSource(bytes));

        return storeResult;
    }

    public StoreResult saveStore(String model, String key,
            javax.activation.DataSource inputDataSource) throws Exception {
        String path = key;
        String code = model + "/" + key;

        byte[] bytes = IOUtils.toByteArray(inputDataSource.getInputStream());
        Blob blob = DataSourceUtils.getConnection(dataSource).createBlob();
        blob.setBytes(1, bytes);

        OssData ossData = new OssData();
        ossData.setCode(code);
        ossData.setData(blob);
        ossDataManager.save(ossData);

        StoreResult storeResult = new StoreResult();
        storeResult.setModel(model);
        storeResult.setKey(path);
        storeResult.setDataSource(new ByteArrayDataSource(bytes));

        return storeResult;
    }

    public void mkdir(String path) {
    }

    public String getSuffix(String name) {
        int lastIndex = name.lastIndexOf(".");

        if (lastIndex != -1) {
            return name.substring(lastIndex);
        } else {
            return "";
        }
    }

    @Resource
    public void setOssDataManager(OssDataManager ossDataManager) {
        this.ossDataManager = ossDataManager;
    }

    @Resource
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
