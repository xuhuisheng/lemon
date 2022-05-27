package com.mossle.internal.oss.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.oss.persistence.manager.OssAccessManager;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssObjectManager;
import com.mossle.internal.oss.persistence.manager.OssRegionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class OssDeployer implements ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(OssDeployer.class);
    private OssRegionManager ossRegionManager;
    private OssBucketManager ossBucketManager;
    private OssAccessManager ossAccessManager;
    private OssObjectManager ossObjectManager;
    private String dataFilePath = "data/oss-region.csv";
    private String dataFileEncoding = "GB2312";
    private String bucketDataFilePath = "data/oss-bucket.csv";
    private String bucketDataFileEncoding = "GB2312";
    private String objectDataFilePath = "data/oss-object.csv";
    private String objectDataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private AvatarInitiator avatarInitiator;
    private String baseDir;
    private ApplicationContext applicationContext;
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip");

            return;
        }

        logger.debug("default tenant id : {}", defaultTenantId);

        OssRegionCallback ossRegionCallback = new OssRegionCallback();
        ossRegionCallback.setDefaultTenantId(defaultTenantId);
        ossRegionCallback.setOssRegionManager(ossRegionManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                ossRegionCallback);

        OssBucketCallback ossBucketCallback = new OssBucketCallback();
        ossBucketCallback.setDefaultTenantId(defaultTenantId);
        ossBucketCallback.setOssRegionManager(ossRegionManager);
        ossBucketCallback.setOssBucketManager(ossBucketManager);
        ossBucketCallback.setOssAccessManager(ossAccessManager);
        new CsvProcessor().process(bucketDataFilePath, bucketDataFileEncoding,
                ossBucketCallback);

        OssObjectCallback ossObjectCallback = new OssObjectCallback();
        ossObjectCallback.setDefaultTenantId(defaultTenantId);
        ossObjectCallback.setOssObjectManager(ossObjectManager);
        ossObjectCallback.setOssBucketManager(ossBucketManager);
        new CsvProcessor().process(objectDataFilePath, objectDataFileEncoding,
                ossObjectCallback);

        avatarInitiator = new AvatarInitiator();
        avatarInitiator.setApplicationContext(applicationContext);
        avatarInitiator.setBaseDir(baseDir);
        avatarInitiator.init();
    }

    // ~
    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    @Resource
    public void setOssRegionManager(OssRegionManager ossRegionManager) {
        this.ossRegionManager = ossRegionManager;
    }

    @Resource
    public void setOssBucketManager(OssBucketManager ossBucketManager) {
        this.ossBucketManager = ossBucketManager;
    }

    @Resource
    public void setOssAccessManaeger(OssAccessManager ossAccessManager) {
        this.ossAccessManager = ossAccessManager;
    }

    @Resource
    public void setOssObjectManager(OssObjectManager ossObjectManager) {
        this.ossObjectManager = ossObjectManager;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
