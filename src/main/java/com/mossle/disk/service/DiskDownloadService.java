package com.mossle.disk.service;

import java.io.InputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskDownloadService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskDownloadService.class);
    private DiskLogInternalService diskLogInternalService;
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private StoreClient storeClient;

    /**
     * 0210 获取下载使用的输入流.
     */
    public Result<InputStream> findDownloadInputStream(long fileId,
            String userId, String tenantId) throws Exception {
        logger.info("download : {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_DOWNLOAD)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_DOWNLOAD, "download");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_DOWNLOAD, "download");
        }

        Result<DiskInfo> diskInfoResult = this.diskBaseInternalService
                .findActive(fileId);

        if (diskInfoResult.isFailure()) {
            return Result.failure(diskInfoResult.getCode(),
                    diskInfoResult.getMessage());
        }

        DiskInfo diskInfo = diskInfoResult.getData();
        Result<InputStream> result = this.diskBaseInternalService
                .findInputStream(fileId);

        if (result.isFailure()) {
            return result;
        }

        // log
        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_DOWNLOAD_FILE);

        return result;
    }

    /**
     * 0211 预览.
     */
    public Result<InputStream> findPreviewInputStream(Long fileId,
            String userId, String tenantId) throws Exception {
        logger.info("preview : {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_PREVIEW)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_PREVIEW, "preview");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_PREVIEW, "preview");
        }

        Result<DiskInfo> diskInfoResult = diskBaseInternalService
                .findActive(fileId);

        if (diskInfoResult.isFailure()) {
            return Result.failure(diskInfoResult.getCode(),
                    diskInfoResult.getMessage());
        }

        DiskInfo diskInfo = diskInfoResult.getData();
        String modelName = "disk";
        String keyName = diskInfo.getPreviewRef();

        StoreDTO storeDto = storeClient.getStore(modelName, keyName, tenantId);

        if (storeDto == null) {
            logger.info("cannot find file : {} {} {}", modelName, keyName,
                    tenantId);

            return Result.failure(404, "cannot find " + modelName + "/"
                    + keyName);
        }

        DataSource dataSource = storeDto.getDataSource();

        if (dataSource == null) {
            logger.info("cannot find file : {} {} {}", modelName, keyName,
                    tenantId);

            return Result.failure(404, "cannot find " + modelName + "/"
                    + keyName);
        }

        InputStream is = dataSource.getInputStream();
        // log
        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_PREVIEW);

        return Result.success(is);
    }

    // ~
    @Resource
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }

    @Resource
    public void setDiskAclInternalService(
            DiskAclInternalService diskAclInternalService) {
        this.diskAclInternalService = diskAclInternalService;
    }

    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
