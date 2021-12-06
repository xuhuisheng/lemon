package com.mossle.disk.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRequest;
import com.mossle.disk.persistence.domain.DiskSid;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskRequestManager;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.support.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskShareService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskShareService.class);
    private DiskBaseInternalService diskBaseInternalService;
    private DiskAclInternalService diskAclInternalService;
    private UserClient userClient;
    private DiskRequestManager diskRequestManager;
    private DiskInfoManager diskInfoManager;

    /**
     * 创建申请.
     */
    public DiskRequest create(long infoCode, String userId, int mask,
            String description) {
        logger.info("create {} {} {} {}", infoCode, userId, mask, description);

        Result<DiskInfo> result = diskBaseInternalService.findActive(infoCode);

        if (result.isFailure()) {
            logger.info("cannot find info : {}", infoCode);

            return null;
        }

        DiskInfo diskInfo = result.getData();
        String owner = diskInfo.getCreator();
        Date now = new Date();
        DiskRequest diskRequest = new DiskRequest();
        diskRequest.setDiskInfo(diskInfo);
        diskRequest.setUserId(owner);
        diskRequest.setMask(mask);
        diskRequest.setDescription(description);
        diskRequest.setCreator(userId);
        diskRequest.setCreateTime(now);
        diskRequest.setUpdater(userId);
        diskRequest.setUpdateTime(now);
        diskRequest.setStatus("active");
        diskRequestManager.save(diskRequest);

        return diskRequest;
    }

    /**
     * 通过申请.
     */
    public DiskRequest approve(long requestCode, String userId, String result) {
        DiskRequest diskRequest = diskRequestManager.get(requestCode);

        if (!"active".equals(diskRequest.getStatus())) {
            return diskRequest;
        }

        diskRequest.setUpdateTime(new Date());
        diskRequest.setStatus("approved");
        diskRequest.setResult(result);
        diskRequestManager.save(diskRequest);

        UserDTO userDto = userClient.findById(diskRequest.getCreator(), "1");
        this.diskAclInternalService.addAccessEntry(diskRequest.getDiskInfo()
                .getId(), "user", userDto.getId(), userDto.getDisplayName(),
                diskRequest.getMask());

        return diskRequest;
    }

    /**
     * 拒绝申请.
     */
    public DiskRequest reject(long requestCode, String userId, String result) {
        DiskRequest diskRequest = diskRequestManager.get(requestCode);

        if (!"active".equals(diskRequest.getStatus())) {
            return diskRequest;
        }

        diskRequest.setUpdateTime(new Date());
        diskRequest.setStatus("rejected");
        diskRequest.setResult(result);
        diskRequestManager.save(diskRequest);

        return diskRequest;
    }

    /**
     * 申请详情.
     */
    public DiskRequest findById(long requestCode, String userId) {
        DiskRequest diskRequest = diskRequestManager.get(requestCode);

        return diskRequest;
    }

    /**
     * 我的申请列表.
     */
    public Page findMyRequests(String userId) {
        String hql = "from DiskRequest where creator=? order by id desc";
        Page page = diskRequestManager.pagedQuery(hql, 1, 100, userId);

        return page;
    }

    /**
     * 待办申请.
     */
    public Page findUserRequests(String userId) {
        String hql = "from DiskRequest where userId=? order by id desc";
        Page page = diskRequestManager.pagedQuery(hql, 1, 100, userId);

        return page;
    }

    /**
     * 检查是否拥有当前节点权限.
     */
    public boolean check(long infoCode, String userId, int mask) {
        return this.diskAclInternalService
                .hasPermission(infoCode, userId, mask);
    }

    /**
     * 分享给我的节点.
     * 
     * 我有权限，但是所有者不是我
     */
    public Page shareToMeList(String userId, int pageNo, int pageSize) {
        String baseHql = " from DiskShare share"
                + " left join share.diskMembers member"
                + " where member.description in (:refs) ";

        String dataHql = "select distinct share.diskInfo " + baseHql
                + " order by share.shareTime desc";
        String countHql = "select count(distinct share.diskInfo) " + baseHql;

        List<DiskSid> sids = this.diskAclInternalService.findSidsByUser(userId);
        List<String> refs = new ArrayList<String>();

        for (DiskSid sid : sids) {
            refs.add(sid.getCatalog() + ":" + sid.getValue());
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("refs", refs);

        // page = diskInfoManager.pagedQuery(hql, pageNo, pageSize, params);
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
     * 我分享的节点.
     * 
     * 所有者是我，权限不只有我自己
     */
    public Page mySharedList(String userId, int pageNo, int pageSize) {
        String hql = "from DiskShare where creator=? order by shareTime desc";
        Page page = diskInfoManager.pagedQuery(hql, pageNo, pageSize, userId);

        return page;
    }

    // ~
    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }

    @Resource
    public void setDiskAclInternalService(
            DiskAclInternalService diskAclInternalService) {
        this.diskAclInternalService = diskAclInternalService;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setDiskRequestManager(DiskRequestManager diskRequestManager) {
        this.diskRequestManager = diskRequestManager;
    }

    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }
}
