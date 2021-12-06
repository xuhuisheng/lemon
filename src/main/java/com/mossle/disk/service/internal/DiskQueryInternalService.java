package com.mossle.disk.service.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRule;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskSpaceManager;
import com.mossle.disk.support.Result;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskQueryInternalService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskQueryInternalService.class);
    private DiskInfoManager diskInfoManager;
    private DiskSpaceManager diskSpaceManager;
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;

    /**
     * 回收站.
     */
    public Page findTrash(String userId, int pageNo, int pageSize) {
        String baseHql = "from DiskInfo file "
                + " left join file.diskRule.diskAcls acl "
                + " where file.status='trash' and file.deleteStatus='root' "
                + " and acl.diskSid.id in (:sids) ";

        String dataHql = "select distinct file " + baseHql
                + " order by file.lastModifiedTime desc";
        String countHql = "select count(distinct file) " + baseHql;

        List<Long> sids = this.diskAclInternalService.findSidIdsByUser(userId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sids", sids);

        int totalCount = diskInfoManager.getCount(countHql, params);
        int start = (pageNo - 1) * pageSize;
        List<DiskInfo> result = diskInfoManager.createQuery(dataHql, params)
                .setFirstResult(start).setMaxResults(pageSize).list();
        Page page = new Page(result, totalCount);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return page;
    }

    /**
     * 0301 搜索.
     */
    public Page search(String query, String startDate, String endDate,
            String userId, Page page) {
        logger.info("search : {} {} {} {}", userId, query, startDate, endDate);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDateValue = null;
        Date endDateValue = null;

        try {
            startDateValue = dateFormat.parse(startDate);
            endDateValue = dateFormat.parse(endDate);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        if (StringUtils.isBlank(query) && (startDateValue == null)
                && (endDateValue == null)) {
            logger.info("query cannot be blank");

            return new Page();
        }

        String baseHql = " from DiskInfo file"
                + " left join file.diskRule.diskAcls acl"
                + " left join file.diskTagInfos tagInfo"
                + " left join tagInfo.diskTag tag"
                + " where file.status='active' "
                + " and (acl.diskSid.id in (:sids) or file.creator=:owner)"
                + " and (file.name like :name or tag.name like :tagName or file.type=:type) ";

        if ((startDateValue != null) && (endDateValue != null)) {
            baseHql += " and file.createTime between :startDate and :endDate";
        }

        String dataHql = "select distinct file " + baseHql
                + " order by file.lastModifiedTime desc";
        String countHql = "select count(distinct file) " + baseHql;

        List<Long> sids = this.diskAclInternalService.findSidIdsByUser(userId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sids", sids);
        params.put("name", "%" + query + "%");
        params.put("tagName", "%" + query + "%");
        params.put("type", query);
        params.put("owner", userId);

        if ((startDateValue != null) && (endDateValue != null)) {
            params.put("startDate", startDateValue);
            params.put("endDate", endDateValue);
        }

        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        // page = diskInfoManager.pagedQuery(hql, pageNo, pageSize, params);
        int totalCount = diskInfoManager.getCount(countHql, params);
        int start = (pageNo - 1) * pageSize;
        List<DiskInfo> result = diskInfoManager.createQuery(dataHql, params)
                .setFirstResult(start).setMaxResults(pageSize).list();
        page = new Page(result, totalCount);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return page;
    }

    public Page searchAdvanced(String query, String sort, String filterValue,
            String searchType, String userId, Page page) {
        logger.info("search : {} {} {} {} {}", userId, query, sort,
                filterValue, searchType);

        // sort
        // 0 上传时间降序
        // 1 上传时间升序
        // 2 大小降序
        // 3 大小升序
        // filterValue
        // 0 全部
        // 1 文档
        // 2 表格
        // 3 文件
        // 4 图片
        // 5 视频
        // searchType
        // 0 全部
        // 1 文件名
        // 2 上传时间
        // 3 上传人
        if (StringUtils.isBlank(query)) {
            logger.info("query cannot be blank");

            return new Page();
        }

        // 默认按照上传时间降序
        String orderBy = null;

        if ("1".equals(sort)) {
            orderBy = " order by file.createTime asc";
        } else if ("2".equals(sort)) {
            orderBy = " order by file.size desc";
        } else if ("3".equals(sort)) {
            orderBy = " order by file.size asc";
        } else {
            orderBy = " order by file.createTime desc";
        }

        String baseHql = " from DiskInfo file "
                + " left join file.diskRule.diskAcls acl "
                + " where file.status='active' "
                + " and (acl.diskSid.id in (:sids) or file.creator=:owner) "
                + " and (file.name like :name or tag.name like :tagName or file.type=:type or file.userId=:username) ";

        // type
        List<String> typeIds = new ArrayList<String>();

        if (filterValue.startsWith("[")) {
            filterValue = filterValue.substring(1);
        }

        if (filterValue.endsWith("]")) {
            filterValue = filterValue.substring(0, filterValue.length() - 1);
        }

        typeIds.addAll(Arrays.asList(filterValue.split(",")));

        // all, in, not
        String typeMode = "all";

        if (typeIds.indexOf("0") != -1) {
            typeMode = "all";
        } else if (typeIds.indexOf("3") != -1) {
            typeMode = "not";
            baseHql += " and file.type not in (:types) ";
        } else {
            typeMode = "in";
            baseHql += " and file.type in (:types) ";
        }

        List<String> docTypes = new ArrayList<String>();
        docTypes.add("doc");
        docTypes.add("docx");

        List<String> xlsTypes = new ArrayList<String>();
        docTypes.add("xls");
        docTypes.add("xlsx");

        List<String> imageTypes = new ArrayList<String>();
        docTypes.add("png");
        docTypes.add("jpg");
        docTypes.add("gif");
        docTypes.add("bmp");

        List<String> videoTypes = new ArrayList<String>();
        docTypes.add("avi");
        docTypes.add("mp4");

        List<String> knownTypes = new ArrayList<String>();
        knownTypes.addAll(docTypes);
        knownTypes.addAll(xlsTypes);
        knownTypes.addAll(imageTypes);
        knownTypes.addAll(videoTypes);

        List<String> types = new ArrayList<String>();

        if ("in".equals(typeMode)) {
            if (typeIds.indexOf("1") != -1) {
                types.addAll(docTypes);
            }

            if (typeIds.indexOf("2") != -1) {
                types.addAll(xlsTypes);
            }

            if (typeIds.indexOf("4") != -1) {
                types.addAll(imageTypes);
            }

            if (typeIds.indexOf("5") != -1) {
                types.addAll(videoTypes);
            }
        }

        if ("not".equals(typeMode)) {
            types.addAll(knownTypes);

            if (typeIds.indexOf("1") != -1) {
                types.removeAll(docTypes);
            }

            if (typeIds.indexOf("2") != -1) {
                types.removeAll(xlsTypes);
            }

            if (typeIds.indexOf("4") != -1) {
                types.removeAll(imageTypes);
            }

            if (typeIds.indexOf("5") != -1) {
                types.removeAll(videoTypes);
            }
        }

        // query
        if ("0".equals(searchType)) {
            baseHql += " and (file.name like :name or file.userId=:username) ";
        } else if ("1".equals(searchType)) {
            baseHql += " and file.name like :name ";
        } else if ("2".equals(searchType)) {
            baseHql += " and file.createTime between :startTime and :endTime ";
        } else if ("3".equals(searchType)) {
            baseHql += " and file.userId=:username ";
        }

        String dataHql = "select distinct file " + baseHql + orderBy;
        String countHql = "select count(distinct file) " + baseHql;

        List<Long> sids = this.diskAclInternalService.findSidIdsByUser(userId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sids", sids);
        params.put("owner", userId);

        if (!types.isEmpty()) {
            params.put("types", types);
        }

        // query
        if ("0".equals(searchType)) {
            params.put("name", "%" + query + "%");
            params.put("username", query);
        } else if ("1".equals(searchType)) {
            params.put("name", "%" + query + "%");
        } else if ("2".equals(searchType)) {
            String[] array = query.split("-");
            long start = Long.parseLong(array[0]);
            long end = Long.parseLong(array[1]);
            params.put("startTime", new Date(start));
            params.put("endTime", new Date(end));
        } else if ("3".equals(searchType)) {
            params.put("username", query);
        }

        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        int totalCount = diskInfoManager.getCount(countHql, params);
        int start = (pageNo - 1) * pageSize;
        List<DiskInfo> result = diskInfoManager.createQuery(dataHql, params)
                .setFirstResult(start).setMaxResults(pageSize).list();
        page = new Page(result, totalCount);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return page;
    }

    /**
     * 0113 根据文件夹查询子节点.
     */
    public Page findChildren(Long folderId, int pageNo, int pageSize,
            String orderBy, String order, String userId) {
        logger.info("findChildren {} {}", folderId, userId);

        // 默认只要有了权限，都能read，目前想不出只能修改不能读的场景
        String baseHql = " from DiskInfo child left join child.diskRule.diskAcls acl "
                + " where child.diskInfo.id=:folderId and child.status='active' "
                + " and (acl.diskSid.id in (:sids) or child.creator=:owner) ";
        String dataSql = "select distinct child " + baseHql
                + " order by child.dirType asc ";
        String countSql = "select count(distinct child) " + baseHql;

        if (StringUtils.isNotBlank(orderBy)) {
            dataSql += ("," + orderBy);

            if (StringUtils.isNotBlank(order)) {
                dataSql += (" " + order);
            }
        } else {
            dataSql += ", child.createTime desc";
        }

        List<Long> sids = this.diskAclInternalService.findSidIdsByUser(userId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("folderId", folderId);
        params.put("sids", sids);
        params.put("owner", userId);

        // return diskInfoManager.pagedQuery(hql, pageNo, pageSize, params);
        int totalCount = diskInfoManager.getCount(countSql, params);
        int start = (pageNo - 1) * pageSize;
        List<DiskInfo> result = diskInfoManager.createQuery(dataSql, params)
                .setFirstResult(start).setMaxResults(pageSize).list();
        Page page = new Page(result, totalCount);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return page;
    }

    /**
     * 根据上级文件夹，搜索子文件夹，不包含文件.
     */
    public List<DiskInfo> findChildrenFolders(Long folderId, String userId) {
        logger.info("findChildrenFolders {} {}", folderId, userId);

        // 默认只要有了权限，都能read，目前想不出只能修改不能读的场景
        String baseHql = "from DiskInfo child left join child.diskRule.diskAcls acl "
                + " where child.diskInfo.id=:folderId and child.status='active' "
                + " and (acl.diskSid.id in (:sids) or child.creator=:owner) "
                + " and child.dirType=0 ";
        String dataSql = "select distinct child " + baseHql
                + " order by child.createTime desc ";
        String countSql = "select count(distinct child) " + baseHql;

        List<Long> sids = this.diskAclInternalService.findSidIdsByUser(userId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("folderId", folderId);
        params.put("sids", sids);
        params.put("owner", userId);

        // int totalCount = diskInfoManager.getCount(countSql, params);
        // List<DiskInfo> result = diskInfoManager.createQuery(dataSql, params)
        // .setFirstResult(start).setMaxResults(pageSize).list();
        List<DiskInfo> result = diskInfoManager.find(dataSql, params);

        return result;
    }

    /**
     * 根据space获取根文件夹.
     */
    public DiskInfo findRootFolderBySpace(Long spaceId) {
        DiskSpace diskSpace = diskSpaceManager.get(spaceId);

        if (diskSpace == null) {
            logger.info("cannot find space : {}", spaceId);

            return null;
        }

        String hql = "from DiskInfo where diskSpace=? and diskInfo=null";

        // String hql = "from DiskInfo where diskSpace=?0 and diskInfo=null";
        DiskInfo diskInfo = diskInfoManager.findUnique(hql, diskSpace);

        if (diskInfo == null) {
            logger.info("cannot find root : {}", diskSpace.getId());
        }

        return diskInfo;
    }

    /**
     * 特殊场景下，只有一个默认文档库，就选中这个文档库，获取默认根目录.
     */
    public DiskInfo findDefaultRepoSpaceRoot() {
        String defaultRepoSpaceName = "default";
        String hql = "from DiskSpace where catalog='group' and type='repo' and status='active' and name=?";

        // String hql = "from DiskSpace where catalog='group' and type='repo' and status='active' and name=?0";
        DiskSpace diskSpace = this.diskSpaceManager.findUnique(hql,
                defaultRepoSpaceName);

        return this.findRootFolderBySpace(diskSpace.getId());
    }

    /**
     * 获取用户空间根文件夹.
     */
    public DiskInfo findUserSpaceRootFolder(String userId) {
        DiskSpace diskSpace = this.findUserSpace(userId);

        return this.findRootFolderBySpace(diskSpace.getId());
    }

    /**
     * 共享空间列表.
     */
    public Page findGroupSpaces(String spaceName, String userId, Page page) {
        logger.info("findGroupSpaces {}", spaceName);

        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        if (StringUtils.isBlank(spaceName)) {
            String baseHql = " from DiskSpace space left join space.diskRule.diskAcls acl "
                    + " where space.status='active' and space.catalog!='user' "
                    + " and (acl.diskSid.id in (:sids) or space.creator=:owner) ";
            String dataSql = "select distinct space " + baseHql
                    + " order by space.id ";
            String countSql = "select count(distinct space) " + baseHql;
            List<Long> sids = this.diskAclInternalService
                    .findSidIdsByUser(userId);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("sids", sids);
            params.put("owner", userId);

            int totalCount = diskSpaceManager.getCount(countSql, params);
            int start = (pageNo - 1) * pageSize;
            List<DiskSpace> result = diskSpaceManager
                    .createQuery(dataSql, params).setFirstResult(start)
                    .setMaxResults(pageSize).list();
            page = new Page(result, totalCount);
            page.setPageNo(pageNo);
            page.setPageSize(pageSize);
        } else {
            String baseHql = " from DiskSpace space left join space.diskRule.diskAcls acl "
                    + " where space.status='active' and space.catalog!='user' "
                    + " and (acl.diskSid.id in (:sids) or space.creator=:owner) "
                    + " and space.name like :spaceName ";
            String dataSql = "select distinct space " + baseHql
                    + " order by space.id ";
            String countSql = "select count(distinct space) " + baseHql;
            List<Long> sids = this.diskAclInternalService
                    .findSidIdsByUser(userId);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("sids", sids);
            params.put("owner", userId);
            params.put("spaceName", spaceName);

            // return diskInfoManager.pagedQuery(hql, pageNo, pageSize, params);
            int totalCount = diskSpaceManager.getCount(countSql, params);
            int start = (pageNo - 1) * pageSize;
            List<DiskSpace> result = diskSpaceManager
                    .createQuery(dataSql, params).setFirstResult(start)
                    .setMaxResults(pageSize).list();
            page = new Page(result, totalCount);
            page.setPageNo(pageNo);
            page.setPageSize(pageSize);
        }

        return page;
    }

    /**
     * 共享空间列表.
     */
    public List<DiskSpace> findGroupSpaces(String userId) {
        logger.info("findGroupSpaces");

        String baseHql = " from DiskSpace space left join space.diskRule.diskAcls acl "
                + " where space.status='active' and space.catalog!='user' "
                + " and (acl.diskSid.id in (:sids) or space.creator=:owner) ";
        String dataSql = "select distinct space " + baseHql
                + " order by space.id ";
        List<Long> sids = this.diskAclInternalService.findSidIdsByUser(userId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sids", sids);
        params.put("owner", userId);

        List<DiskSpace> result = diskSpaceManager.find(dataSql, params);

        return result;
    }

    /**
     * 根据用户id返回或创建这个用户的个人文档.
     */
    public DiskSpace findUserSpace(String userId) {
        logger.info("find user space : {}", userId);

        return this.createUserSpace(userId);
    }

    /**
     * 创建个人空间.
     * 
     * c=user, t=user, 个人空间 c=group, t=group, 群组空间 c=group, t=repo, 文档库
     */
    public DiskSpace createUserSpace(String userId) {
        logger.info("create user space : {}", userId);

        String hql = "from DiskSpace where catalog='user' and type='user' and creator=?";

        // String hql = "from DiskSpace where catalog='user' and type='user' and creator=?0";
        DiskSpace diskSpace = this.diskSpaceManager.findUnique(hql, userId);

        if (diskSpace != null) {
            return diskSpace;
        }

        // DiskRule diskRule = new DiskRule();
        // diskRuleManager.save(diskRule);
        diskSpace = new DiskSpace();
        diskSpace.setName("我的空间");
        diskSpace.setCatalog("user");
        diskSpace.setType("user");
        diskSpace.setCreator(userId);
        diskSpace.setCreateTime(new Date());
        // diskSpace.setDiskRule(diskRule);
        diskSpace.setStatus("active");
        this.diskSpaceManager.save(diskSpace);

        // 默认根目录
        Result<DiskInfo> result = this.diskBaseInternalService.createRoot(
                "我的空间", diskSpace.getId());

        if (result.isFailure()) {
            logger.info("create root folder failure");
            throw new IllegalStateException("create root folder failure");
        }

        DiskInfo diskInfo = result.getData();
        DiskRule diskRule = this.diskAclInternalService
                .createPrivateRule(diskInfo);
        diskInfo.setDiskRule(diskRule);
        this.diskBaseInternalService.save(diskInfo);

        diskSpace.setDiskRule(diskRule);
        diskSpaceManager.save(diskSpace);

        return diskSpace;
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskSpaceManager(DiskSpaceManager diskSpaceManager) {
        this.diskSpaceManager = diskSpaceManager;
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
}
