package com.mossle.disk.web.rs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.core.page.Page;

import com.mossle.disk.component.DiskInfoConverter;
import com.mossle.disk.persistence.domain.DiskFavorite;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.manager.DiskFavoriteManager;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskLogManager;
import com.mossle.disk.support.DiskInfoDTO;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "收藏")
@RestController
@RequestMapping("disk/rs/favorite")
public class DiskFavoriteRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFavoriteRestController.class);
    private CurrentUserHolder currentUserHolder;
    private DiskInfoConverter diskInfoConverter;
    @Resource
    private DiskInfoManager diskInfoManager;
    @Resource
    private DiskLogManager diskLogManager;
    @Resource
    private DiskFavoriteManager diskFavoriteManager;

    /**
     * 收藏夹.
     */
    @Operation(summary = "收藏夹列表")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Result list(Page page) throws Exception {
        logger.info("list");

        String userId = currentUserHolder.getUserId();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        String hql = "from DiskFavorite where creator=? order by id desc";
        page = diskFavoriteManager.pagedQuery(hql, pageNo, pageSize, userId);

        List<DiskInfo> diskInfos = new ArrayList<DiskInfo>();
        List<DiskFavorite> diskFavorites = (List<DiskFavorite>) page
                .getResult();

        for (DiskFavorite diskFavorite : diskFavorites) {
            diskInfos.add(diskFavorite.getDiskInfo());
        }

        List<DiskInfoDTO> diskInfoDtos = this.diskInfoConverter.convertList(
                diskInfos, userId);
        page.setResult(diskInfoDtos);

        return Result.success(page);
    }

    /**
     * 添加收藏.
     */
    @Operation(summary = "添加收藏")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Result add(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode)
            throws Exception {
        logger.info("add");

        String userId = currentUserHolder.getUserId();
        String hql = "from DiskFavorite where creator=? and diskInfo.id=?";
        DiskFavorite diskFavorite = diskFavoriteManager.findUnique(hql, userId,
                infoCode);

        if (diskFavorite != null) {
            return Result.failure(405, "already add favorite");
        }

        Date now = new Date();
        DiskInfo diskInfo = diskInfoManager.get(infoCode);
        diskFavorite = new DiskFavorite();
        diskFavorite.setDiskInfo(diskInfo);
        diskFavorite.setCreator(userId);
        diskFavorite.setCreateTime(now);
        diskFavorite.setUpdater(userId);
        diskFavorite.setUpdateTime(now);
        diskFavorite.setStatus("active");
        diskFavoriteManager.save(diskFavorite);

        DiskInfoDTO diskInfoDto = diskInfoConverter
                .convertOne(diskInfo, userId);

        return Result.success(diskInfoDto);
    }

    /**
     * 取消收藏.
     */
    @Operation(summary = "取消收藏")
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public Result remove(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode)
            throws Exception {
        logger.info("remove");

        String userId = currentUserHolder.getUserId();
        String hql = "from DiskFavorite where creator=? and diskInfo.id=?";
        DiskFavorite diskFavorite = diskFavoriteManager.findUnique(hql, userId,
                infoCode);

        if (diskFavorite == null) {
            return Result.failure(404, "not found");
        }

        DiskInfo diskInfo = diskFavorite.getDiskInfo();
        diskFavoriteManager.remove(diskFavorite);

        DiskInfoDTO diskInfoDto = diskInfoConverter
                .convertOne(diskInfo, userId);

        return Result.success(diskInfoDto);
    }

    // ~ ======================================================================
    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setDiskInfoConverter(DiskInfoConverter diskInfoConverter) {
        this.diskInfoConverter = diskInfoConverter;
    }
}
