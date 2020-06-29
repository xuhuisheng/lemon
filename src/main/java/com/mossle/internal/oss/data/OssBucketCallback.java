package com.mossle.internal.oss.data;

import java.util.Date;
import java.util.List;

import com.mossle.api.auth.CustomPasswordEncoder;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.oss.persistence.domain.OssAccess;
import com.mossle.internal.oss.persistence.domain.OssBucket;
import com.mossle.internal.oss.persistence.domain.OssRegion;
import com.mossle.internal.oss.persistence.manager.OssAccessManager;
import com.mossle.internal.oss.persistence.manager.OssBucketManager;
import com.mossle.internal.oss.persistence.manager.OssRegionManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OssBucketCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(OssBucketCallback.class);
    private OssRegionManager ossRegionManager;
    private OssBucketManager ossBucketManager;
    private OssAccessManager ossAccessManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String userId = list.get(1);
        String region = list.get(2);

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(region)) {
            logger.warn("region cannot be blank {} {}", lineNo, list);

            return;
        }

        name = name.toLowerCase();
        region = region.toLowerCase();

        this.createOrUpdateBucket(name, userId, region, lineNo);
    }

    public void createOrUpdateBucket(String name, String userId, String region,
            int lineNo) {
        OssRegion ossRegion = ossRegionManager.findUniqueBy("name", region);

        if (ossRegion == null) {
            logger.info("cannot find region : {}", region);

            return;
        }

        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", name);

        if (ossBucket != null) {
            return;
        }

        // insert
        ossBucket = new OssBucket();
        ossBucket.setName(name);
        ossBucket.setUserId(userId);
        ossBucket.setOssRegion(ossRegion);
        ossBucket.setStatus("active");
        ossBucketManager.save(ossBucket);

        this.createOrUpdateAccess(name, lineNo);
    }

    public void createOrUpdateAccess(String name, int lineNo) {
        OssBucket ossBucket = ossBucketManager.findUniqueBy("name", name);

        if (ossBucket == null) {
            logger.info("cannot find bucket : {}", name);

            return;
        }

        OssAccess ossAccess = ossAccessManager.findUnique(
                "from OssAccess where ossBucket=?", ossBucket);

        if (ossAccess != null) {
            return;
        }

        ossAccess = new OssAccess();
        ossAccess.setAccessKey("default");
        ossAccess.setSecretKey("default");
        ossAccess.setStatus("active");
        ossAccess.setOssBucket(ossBucket);
        ossAccessManager.save(ossAccess);
    }

    public void setOssRegionManager(OssRegionManager ossRegionManager) {
        this.ossRegionManager = ossRegionManager;
    }

    public void setOssBucketManager(OssBucketManager ossBucketManager) {
        this.ossBucketManager = ossBucketManager;
    }

    public void setOssAccessManager(OssAccessManager ossAccessManager) {
        this.ossAccessManager = ossAccessManager;
    }
}
