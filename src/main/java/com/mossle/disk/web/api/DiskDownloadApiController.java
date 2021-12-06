package com.mossle.disk.web.api;

import java.io.InputStream;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "服务端")
@RestController
@RequestMapping("disk/api")
public class DiskDownloadApiController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskDownloadApiController.class);
    private TenantHolder tenantHolder;
    private DiskBaseInternalService diskBaseInternalService;

    /**
     * 下载.
     */
    @Operation(summary = "下载")
    @RequestMapping(value = "download/{code}", method = RequestMethod.GET)
    public void fileDownload(
            @Parameter(description = "节点code") @PathVariable("code") Long code,
            @Parameter(description = "访问帐号") @RequestParam("username") String username,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        try {
            Result<DiskInfo> result = this.diskBaseInternalService
                    .findById(code);

            if (result.isFailure()) {
                return;
            }

            DiskInfo diskInfo = result.getData();
            InputStream is = null;

            try {
                ServletUtils.setFileDownloadHeader(request, response,
                        diskInfo.getName());

                Result<InputStream> downloadResult = this.diskBaseInternalService
                        .findInputStream(code);

                if (downloadResult.isFailure()) {
                    return;
                }

                is = downloadResult.getData();
                IOUtils.copy(is, response.getOutputStream());
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (DiskAclException ex) {
            logger.error(ex.getMessage(), ex);
            response.sendError(403);
        }
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }
}
