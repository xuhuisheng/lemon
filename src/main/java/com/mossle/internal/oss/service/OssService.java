package com.mossle.internal.oss.service;

import java.io.InputStream;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.store.StoreHelper;
import com.mossle.core.store.StoreResult;

import com.mossle.internal.oss.persistence.domain.OssBucket;
import com.mossle.internal.oss.persistence.domain.OssObject;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssObjectManager;
import com.mossle.internal.oss.support.OssDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class OssService {
    private static Logger logger = LoggerFactory.getLogger(OssService.class);
    private StoreHelper storeHelper;
    private OssBucketManager ossBucketManager;
    private OssObjectManager ossObjectManager;

    public OssDTO putObject(InputStream is, String bucketName, String objectName) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucketName);

        if (ossBucket == null) {
            logger.info("cannot find bucket : {}", bucketName);

            return null;
        }

        OssObject ossObject = ossObjectManager.findUnique(
                "from OssObject where ossBucket=? and name=?", ossBucket,
                objectName);

        try {
            StoreResult storeResult = storeHelper.saveStore(bucketName,
                    new InputStreamDataSource(objectName, is));

            if (ossObject != null) {
                ossObject.setName(objectName);
                ossObject.setPath(storeResult.getKey());
                ossObject.setCreateTime(new Date());
            } else {
                ossObject = new OssObject();
                ossObject.setOssBucket(ossBucket);
                ossObject.setName(objectName);
                ossObject.setPath(storeResult.getKey());
                ossObject.setCreateTime(new Date());
            }

            ossObjectManager.save(ossObject);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }

        OssDTO ossDto = new OssDTO();
        ossDto.setBucketName(bucketName);
        ossDto.setObjectName(objectName);

        return ossDto;
    }

    public OssDTO postObject(InputStream is, String bucketName) {
        String objectName = UUID.randomUUID().toString();

        return this.putObject(is, bucketName, objectName);
    }

    public OssDTO getObject(String bucketName, String objectName) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucketName);

        if (ossBucket == null) {
            logger.info("cannot find bucket : {}", bucketName);

            return null;
        }

        OssObject ossObject = ossObjectManager.findUnique(
                "from OssObject where ossBucket=? and name=?", ossBucket,
                objectName);

        if (ossObject == null) {
            logger.info("cannot find object : {} {}", bucketName, objectName);

            return null;
        }

        OssDTO ossDto = new OssDTO();
        ossDto.setBucketName(bucketName);
        ossDto.setObjectName(objectName);

        try {
            String key = ossObject.getPath();
            StoreResult storeResult = storeHelper.getStore(bucketName, key);

            if (storeResult == null) {
                logger.info("cannot find store : {} {}", bucketName, key);

                return null;
            }

            ossDto.setInputStream(storeResult.getDataSource().getInputStream());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }

        return ossDto;
    }

    public OssDTO deleteObject(String bucketName, String objectName) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucketName);

        if (ossBucket == null) {
            logger.info("cannot find bucket : {}", bucketName);

            return null;
        }

        OssObject ossObject = ossObjectManager.findUnique(
                "from OssObject where ossBucket=? and name=?", ossBucket,
                objectName);

        if (ossObject == null) {
            logger.info("cannot find object : {} {}", bucketName, objectName);

            return null;
        }

        try {
            String key = ossObject.getPath();
            storeHelper.removeStore(bucketName, key);

            ossObjectManager.remove(ossObject);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }

        OssDTO ossDto = new OssDTO();
        ossDto.setBucketName(bucketName);
        ossDto.setObjectName(objectName);

        return ossDto;
    }

    public OssDTO doesObjectExist(String bucketName, String objectName) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucketName);

        if (ossBucket == null) {
            logger.info("cannot find bucket : {}", bucketName);

            return null;
        }

        OssObject ossObject = ossObjectManager.findUnique(
                "from OssObject where ossBucket=? and name=?", ossBucket,
                objectName);

        if (ossObject == null) {
            logger.info("cannot find object : {} {}", bucketName, objectName);

            return null;
        }

        OssDTO ossDto = new OssDTO();
        ossDto.setBucketName(bucketName);
        ossDto.setObjectName(objectName);

        return ossDto;
    }

    @Resource
    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }

    @Resource
    public void setOssBucketManager(OssBucketManager ossBucketManager) {
        this.ossBucketManager = ossBucketManager;
    }

    @Resource
    public void setOssObjectManager(OssObjectManager ossObjectManager) {
        this.ossObjectManager = ossObjectManager;
    }
}
