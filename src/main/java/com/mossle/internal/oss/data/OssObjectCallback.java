package com.mossle.internal.oss.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.oss.persistence.domain.OssBucket;
import com.mossle.internal.oss.persistence.domain.OssObject;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssObjectManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OssObjectCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(OssObjectCallback.class);
    private OssBucketManager ossBucketManager;
    private OssObjectManager ossObjectManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("process : {} {}", list, lineNo);
        logger.debug("default tenant id : {}", defaultTenantId);

        String name = list.get(0);
        String userId = list.get(1);
        String bucket = list.get(2);

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(bucket)) {
            logger.warn("bucket cannot be blank {} {}", lineNo, list);

            return;
        }

        name = name.toLowerCase();
        bucket = bucket.toLowerCase();

        this.createOrUpdateObject(name, userId, bucket, lineNo);
    }

    public void createOrUpdateObject(String name, String userId, String bucket,
            int lineNo) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", bucket);

        if (ossBucket == null) {
            logger.info("cannot find bucket : {}", bucket);

            return;
        }

        OssObject ossObject = ossObjectManager.findUniqueBy("name", name);

        if (ossObject != null) {
            return;
        }

        // insert
        ossObject = new OssObject();
        ossObject.setName(name);
        ossObject.setPath(name);
        ossObject.setUserId(userId);
        ossObject.setOssBucket(ossBucket);
        ossObject.setStatus("active");
        ossObjectManager.save(ossObject);
    }

    // ~
    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public void setOssBucketManager(OssBucketManager ossBucketManager) {
        this.ossBucketManager = ossBucketManager;
    }

    public void setOssObjectManager(OssObjectManager ossObjectManager) {
        this.ossObjectManager = ossObjectManager;
    }
}
