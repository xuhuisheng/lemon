package com.mossle.disk.service.internal;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskDownload;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskLog;
import com.mossle.disk.persistence.domain.DiskRecent;
import com.mossle.disk.persistence.manager.DiskDownloadManager;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskLogManager;
import com.mossle.disk.persistence.manager.DiskRecentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskLogInternalService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskLogInternalService.class);
    public static final String CATALOG_UPLOAD = "upload";
    public static final String CATALOG_PREVIEW = "preview";
    public static final String CATALOG_REMOVE_FILE = "removeFile";
    public static final String CATALOG_EDIT_TAGS = "editTags";
    public static final String CATALOG_DOWNLOAD_FILE = "download";
    public static final String CATALOG_SEARCH = "search";
    public static final String CATALOG_CREATE_FOLDER = "createFolder";
    public static final String CATALOG_REMOVE_FOLDER = "removeFolder";
    public static final String CATALOG_MOVE = "move";
    public static final String CATALOG_COPY = "copy";
    public static final String CATALOG_LINK = "link";
    private DiskLogManager diskLogManager;
    private DiskRecentManager diskRecentManager;
    private DiskDownloadManager diskDownloadManager;
    private DiskInfoManager diskInfoManager;

    /**
     * 记录日志.
     * 
     * upload, preview, createFile, createFolder, removeFolder, downloadFile, removeFile, editTag, searchFiles
     */
    public DiskLog recordLog(DiskInfo diskInfo, String creator, String catalog) {
        logger.debug("record log");

        DiskLog diskLog = new DiskLog();
        diskLog.setName(diskInfo.getName());
        diskLog.setCatalog(catalog);
        diskLog.setNewValue(diskInfo.getName());
        diskLog.setCreator(creator);
        diskLog.setCreateTime(new Date());
        diskLog.setRefType("diskInfo");
        diskLog.setRefValue(Long.toString(diskInfo.getId()));

        if ("folder".equals(diskInfo.getType())) {
            // diskLog.setType(diskInfo.getType());
            diskLog.setType("folder");

            if (diskInfo.getDiskInfo() != null) {
                diskLog.setParentId(diskInfo.getDiskInfo().getId());
            } else {
                diskLog.setParentId(0L);
            }
        } else {
            diskLog.setType("file");
            diskLog.setParentId(diskInfo.getDiskInfo().getId());
        }

        diskLog.setSourceId(diskInfo.getId());
        diskLog.setSpaceId(diskInfo.getDiskSpace().getId());
        diskLogManager.save(diskLog);

        return diskLog;
    }

    /**
     * 记录修改标签.
     */
    public DiskLog recordLogEditTag(DiskInfo diskInfo, String creator,
            String catalog, String tags) {
        DiskLog diskLog = this.recordLog(diskInfo, creator, catalog);
        diskLog.setNewValue(tags);
        diskLogManager.save(diskLog);

        return diskLog;
    }

    /**
     * 记录搜索.
     */
    public DiskLog recordLogSearch(String query, String creator, String catalog) {
        DiskLog diskLog = new DiskLog();
        diskLog.setCatalog(catalog);
        diskLog.setNewValue("");
        diskLog.setCreator(creator);
        diskLog.setCreateTime(new Date());
        diskLog.setRefType("search");
        diskLog.setRefValue(query);
        diskLogManager.save(diskLog);

        return diskLog;
    }

    /**
     * 日志列表.
     */
    public List<DiskLog> findLogs(DiskInfo diskInfo) {
        String hql = "from DiskLog where diskInfo=? order by id desc";

        // String hql = "from DiskLog where diskInfo=?0 order by id desc";
        return diskLogManager.find(hql, diskInfo);
    }

    /**
     * 日志列表.
     */
    public Page findLogs(Page page) {
        page = diskLogManager.pagedQuery("from DiskLog log order by id desc",
                page.getPageNo(), page.getPageSize());

        return page;
    }

    /**
     * 日志列表.
     */
    public Page findParentLogs(Long parentId, Page page) {
        String hql = "from DiskLog log where log.parentId=? order by log.id desc";
        // String hql = "from DiskLog log where log.parentId=?0 order by log.id desc";
        page = diskLogManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), parentId);

        return page;
    }

    /**
     * 日志列表.
     */
    public Page findSourceLogs(Long sourceId, Page page) {
        String hql = "from DiskLog log where log.sourceId=? order by log.id desc";
        // String hql = "from DiskLog log where log.sourceId=?0 order by log.id desc";
        page = diskLogManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), sourceId);

        return page;
    }

    /**
     * 记录打开文件夹或文件事件.
     */
    public void recordOpen(long infoId, String userId) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if ("folder".equals(diskInfo.getType())) {
            return;
        }

        Date now = new Date();
        DiskRecent diskRecent = new DiskRecent();
        diskRecent.setName(diskInfo.getName());
        diskRecent.setType(diskInfo.getType());
        diskRecent.setDiskInfo(diskInfo);
        diskRecent.setCreator(userId);
        diskRecent.setCreateTime(now);
        diskRecent.setUpdater(userId);
        diskRecent.setUpdateTime(now);
        diskRecent.setStatus("active");
        diskRecent.setTenantId("1");
        diskRecentManager.save(diskRecent);
    }

    /**
     * 记录下载开始.
     * 
     * TODO: info may dumplicate
     */
    public void recordDownloadStart(long infoId, String userId) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        String name = diskInfo.getName();
        String type = diskInfo.getType();
        Date now = new Date();
        DiskDownload diskDownload = new DiskDownload();
        diskDownload.setDiskInfo(diskInfo);
        diskDownload.setName(name);
        diskDownload.setType(type);
        diskDownload.setCreator(userId);
        diskDownload.setCreateTime(now);
        diskDownload.setUpdater(userId);
        diskDownload.setUpdateTime(now);
        diskDownload.setStatus("active");
        diskDownload.setTenantId("1");
        diskDownload.setStartTime(now);
        diskDownloadManager.save(diskDownload);
    }

    /**
     * 记录下载结束.
     * 
     * TODO: info may dumplicate
     */
    public void recordDownloadEnd(long infoId, String userId) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        DiskDownload diskDownload = diskDownloadManager.findUniqueBy(
                "diskInfo", diskInfo);
        Date now = new Date();
        diskDownload.setEndTime(now);
        diskDownloadManager.save(diskDownload);
    }

    /**
     * 记录下载日志.
     * 
     * 使用浏览器和手机原生下载，无法获取完成时间，所以只保存一个日志
     */
    public void recordDownload(long infoId, String userId) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        String name = diskInfo.getName();
        String type = diskInfo.getType();
        Date now = new Date();
        DiskDownload diskDownload = new DiskDownload();
        diskDownload.setDiskInfo(diskInfo);
        diskDownload.setName(name);
        diskDownload.setType(type);
        diskDownload.setCreator(userId);
        diskDownload.setCreateTime(now);
        diskDownload.setUpdater(userId);
        diskDownload.setUpdateTime(now);
        diskDownload.setStatus("active");
        diskDownload.setTenantId("1");
        diskDownload.setStartTime(now);
        diskDownload.setEndTime(now);
        diskDownloadManager.save(diskDownload);
    }

    /**
     * 根据空间获取点击用户数.
     */
    public int findUserCountByUserId(String userId) {
        String hql = "select count(distinct log.creator) from DiskLog log, DiskInfo info where log.sourceId=info.id and log.catalog='preview' and info.creator=?";

        return diskLogManager.getCount(hql, userId);
    }

    /**
     * 根据空间获取点击数.
     */
    public int findClickCountByUserId(String userId) {
        String hql = "select count(log.id) from DiskLog log, DiskInfo info where log.sourceId=info.id and log.catalog='preview' and info.creator=?";

        return diskLogManager.getCount(hql, userId);
    }

    /**
     * 根据节点获取下载数.
     */
    public int findDownloadCountByUserId(String userId) {
        String hql = "select count(log.id) from DiskLog log, DiskInfo info where log.sourceId=info.id and log.catalog='download' and info.creator=?";

        return diskLogManager.getCount(hql, userId);
    }

    /**
     * 根据节点获取点击用户数.
     */
    public int findUserCountByInfoId(long infoId) {
        String hql = "select count(distinct log.creator) from DiskLog log "
                + " where log.sourceId=? and log.catalog='preview'";

        return diskLogManager.getCount(hql, infoId);
    }

    /**
     * 根据节点获取点击数.
     */
    public int findClickCountByInfoId(long infoId) {
        String hql = "select count(log.id) from DiskLog log "
                + " where log.sourceId=? and log.catalog='preview'";

        return diskLogManager.getCount(hql, infoId);
    }

    /**
     * 根据空间获取下载数.
     */
    public int findDownloadCountByInfoId(long infoId) {
        String hql = "select count(log.id) from DiskLog log "
                + " where log.sourceId=? and log.catalog='download'";

        return diskLogManager.getCount(hql, infoId);
    }

    // ~
    @Resource
    public void setDiskLogManager(DiskLogManager diskLogManager) {
        this.diskLogManager = diskLogManager;
    }

    @Resource
    public void setDiskRecentManager(DiskRecentManager diskRecentManager) {
        this.diskRecentManager = diskRecentManager;
    }

    @Resource
    public void setDiskDownloadManager(DiskDownloadManager diskDownloadManager) {
        this.diskDownloadManager = diskDownloadManager;
    }

    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }
}
