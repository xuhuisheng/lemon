package com.mossle.internal.oss.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.oss.persistence.domain.OssAccess;
import com.mossle.internal.oss.persistence.domain.OssBucket;
import com.mossle.internal.oss.persistence.domain.OssRegion;
import com.mossle.internal.oss.persistence.manager.OssAccessManager;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssRegionManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OssDeployer {
    private static Logger logger = LoggerFactory.getLogger(OssDeployer.class);
    private OssRegionManager ossRegionManager;
    private OssBucketManager ossBucketManager;
    private OssAccessManager ossAccessManager;
    private String dataFilePath = "data/oss-region.csv";
    private String dataFileEncoding = "GB2312";
    private String bucketDataFilePath = "data/oss-bucket.csv";
    private String bucketDataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip");

            return;
        }

        OssRegionCallback ossRegionCallback = new OssRegionCallback();
        ossRegionCallback.setOssRegionManager(ossRegionManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                ossRegionCallback);

        OssBucketCallback ossBucketCallback = new OssBucketCallback();
        ossBucketCallback.setOssRegionManager(ossRegionManager);
        ossBucketCallback.setOssBucketManager(ossBucketManager);
        ossBucketCallback.setOssAccessManager(ossAccessManager);
        new CsvProcessor().process(bucketDataFilePath, bucketDataFileEncoding,
                ossBucketCallback);
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
}
