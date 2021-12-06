package com.mossle.disk.web.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "服务端")
@RestController
@RequestMapping("disk/api/info")
public class DiskInfoApiController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskInfoApiController.class);
    private DiskBaseInternalService diskBaseInternalService;

    /**
     * 详情.
     */
    @Operation(summary = "详情")
    @RequestMapping(value = "detail/{code}", method = RequestMethod.GET)
    public Result detail(
            @Parameter(description = "节点code") @PathVariable("code") long code,
            @Parameter(description = "访问账号") @RequestParam("username") String username) {
        logger.info("detail : {}", code);

        try {
            Result<DiskInfo> result = this.diskBaseInternalService
                    .findActive(code);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", diskInfo.getId());
            map.put("name", diskInfo.getName());
            map.put("type", diskInfo.getType());
            map.put("size", diskInfo.getFileSize());
            map.put("creator", diskInfo.getCreator());
            map.put("creatorTime", diskInfo.getCreateTime());
            map.put("creatorName", diskInfo.getCreator());

            return Result.success(map);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    // ~ ======================================================================
    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }
}
