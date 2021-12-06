package com.mossle.disk.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.client.authz.AuthzClient;
import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRule;
import com.mossle.disk.persistence.domain.DiskSid;
import com.mossle.disk.persistence.manager.DiskAclManager;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskRuleManager;
import com.mossle.disk.persistence.manager.DiskSidManager;
import com.mossle.disk.support.AclDTO;
import com.mossle.disk.support.BatchAclDTO;
import com.mossle.disk.support.DiskSidBuilder;
import com.mossle.disk.support.Result;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskAclInternalService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskAclInternalService.class);
    public static final int MASK_NONE = 0;
    public static final int MASK_ALL = 127;
    public static final int MASK_READ = 1;
    public static final int MASK_PREVIEW = 2;
    public static final int MASK_DOWNLOAD = 4;
    public static final int MASK_COPY = 8;
    public static final int MASK_EDIT = 16;
    public static final int MASK_CREATE = 32;
    public static final int MASK_DELETE = 64;
    private DiskInfoManager diskInfoManager;
    private DiskAclManager diskAclManager;
    private DiskRuleManager diskRuleManager;
    private DiskSidManager diskSidManager;
    private AuthzClient authzClient;
    private UserClient userClient;
    private DiskBaseInternalService diskBaseInternalService;
    private JsonMapper jsonMapper = new JsonMapper();

    // 0011
    /**
     * 获取节点当前的权限列表.
     */
    public List<DiskAcl> findPermissions(Long infoId, String userId) {
        return this.findAclsForInfoId(infoId);
    }

    // 0012
    /**
     * 添加权限.
     */
    public Result<DiskInfo> addPermission(Long infoId, String userId,
            String memberId, int mask) {
        logger.info("addPermission : {} {} {} {}", infoId, userId, memberId,
                mask);

        UserDTO userDto = userClient.findById(userId, "1");

        return this.addAccessEntry(infoId, "user", memberId,
                userDto.getDisplayName(), mask);
    }

    // 0012
    /**
     * 删除权限.
     */
    public Result<DiskInfo> removePermission(Long infoId, String userId,
            Long aclId) {
        // TODO: 应该是自己自己添加的权限才能删除吧
        // TODO: 或者只要有修改权限，就能删除所有权限
        // 目前是只要有read权限，就可以删除所有权限，能量有点儿大，回头细化
        logger.info("removePermission : {} {} {}", infoId, userId, aclId);

        return this.removeAccessEntry(infoId, aclId);
    }

    /**
     * 转移所有者.
     */
    public void transferOwner(Long infoId, String userId, String memberCode) {
        logger.info("transfer owner : {} {} {}", infoId, userId, memberCode);

        if (StringUtils.isBlank(memberCode)) {
            logger.info("require memberCode");

            return;
        }

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("no info : {}", infoId);

            return;
        }

        String creator = diskInfo.getCreator();

        if (memberCode.equals(creator)) {
            logger.info("equals owner : {}", memberCode);

            return;
        }

        diskInfo.setCreator(memberCode);
        diskInfoManager.save(diskInfo);

        UserDTO userDto = userClient.findById(memberCode, "1");
        this.addAccessEntry(infoId, "user", memberCode,
                userDto.getDisplayName(), 127);
    }

    public Result<DiskInfo> addPermissionByMemberCode(Long infoId,
            String userId, String memberCode, String memberType,
            String memberName, String maskText) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        int mask = "read".equals(maskText) ? 1 : 127;
        this.addAccessEntry(infoId, memberType, memberCode, memberName, mask);

        // TODO: 转移所有者
        return Result.success(diskInfo);
    }

    public Result<DiskInfo> updatePermissionByMemberCode(Long infoId,
            String userId, String memberCode, String memberType, String maskText) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        DiskSid diskSid = this.createOrGetSid(memberType, memberCode);
        DiskRule diskRule = this.createOrGetCurrentRule(diskInfo);
        DiskAcl diskAcl = this.findAclByRuleAndSid(diskRule.getId(),
                diskSid.getId());
        long aclId = diskAcl.getId();

        if ("owner".equals(maskText)) {
            transferOwner(infoId, userId, memberCode);
        } else {
            int mask = "read".equals(maskText) ? 1 : 127;
            diskAcl.setMask(mask);
            diskAclManager.save(diskAcl);
            logger.info("mask : {}", diskAcl.getMask());
        }

        return Result.success(diskInfo);
    }

    public Result<DiskInfo> removePermissionByMemberCode(Long infoId,
            String userId, String memberCode) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        DiskSid diskSid = this.createOrGetSid(memberCode);
        DiskRule diskRule = this.createOrGetCurrentRule(diskInfo);
        DiskAcl diskAcl = this.findAclByRuleAndSid(diskRule.getId(),
                diskSid.getId());

        if (diskAcl == null) {
            logger.info("cannot find acl : {} {} {}", infoId, userId,
                    memberCode);

            return Result.failure(404, "no acl");
        }

        long aclId = diskAcl.getId();

        return removePermission(infoId, userId, aclId);
    }

    public Result<DiskInfo> batchSave(String text) throws Exception {
        BatchAclDTO batchAclDto = jsonMapper.fromJson(text, BatchAclDTO.class);
        DiskInfo diskInfo = diskInfoManager.get(batchAclDto.getCode());

        if (diskInfo == null) {
            return Result.failure(404, "no info : " + batchAclDto.getCode());
        }

        DiskRule diskRule = this.createOrGetCurrentRule(diskInfo);

        for (AclDTO aclDto : batchAclDto.getList()) {
            String memberCode = aclDto.getMemberCode();
            String memberType = aclDto.getMemberType();
            String memberName = aclDto.getMemberName();
            String maskText = aclDto.getMask();

            int mask = "read".equals(maskText) ? 1 : 127;

            this.addAccessEntry(batchAclDto.getCode(), memberType, memberCode,
                    memberName, mask);
        }

        return Result.success(diskInfo);
    }

    // ~

    /**
     * 返回帐号对应的sid的id列表.
     */
    public List<Long> findSidIdsByUser(String userId) {
        UserAuthDTO userAuthDto = authzClient.findById(userId, "1");
        List<Long> sids = new ArrayList<Long>();
        sids.add(createOrGetSid("user", userId).getId());

        List<String> permissions = userAuthDto.getPermissions();

        // logger.info("permissions {}", permissions);
        if ((permissions != null)
                && (permissions.contains("*:*:*") || permissions
                        .contains("disk:admin"))) {
            sids.add(createOrGetSid("role", "admin").getId());
        }

        return sids;
    }

    /**
     * 返回帐号对应的sid列表.
     */
    public List<DiskSid> findSidsByUser(String userId) {
        UserAuthDTO userAuthDto = authzClient.findById(userId, "1");
        List<DiskSid> sids = new ArrayList<DiskSid>();
        sids.add(createOrGetSid("user", userId));

        List<String> permissions = userAuthDto.getPermissions();

        // logger.info("permissions {}", permissions);
        if ((permissions != null)
                && (permissions.contains("*:*:*") || permissions
                        .contains("disk:admin"))) {
            sids.add(createOrGetSid("role", "admin"));
        }

        return sids;
    }

    /**
     * 权限列表.
     * 
     * 节点的所有acl
     */
    public List<DiskAcl> findAclsForInfoId(Long infoId) {
        String hql = "select acl from DiskAcl acl left join acl.diskRule.diskInfos diskInfo where diskInfo.id=?";

        // String hql = "select acl from DiskAcl acl left join acl.diskRule.diskInfos diskInfo where diskInfo.id=?0";
        List<DiskAcl> diskAcls = diskAclManager.find(hql, infoId);

        return diskAcls;
    }

    /**
     * 查询diskInfo与userId相关的权限.
     */
    public List<DiskAcl> findAcls(Long diskInfoId, String userId) {
        List<Long> sids = this.findSidIdsByUser(userId);
        logger.info("sids : {}", sids);

        String hql = "select acl from DiskInfo info inner join info.diskRule.diskAcls acl "
                + " where info.id=:infoId " + " and acl.diskSid.id in (:sids) ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("infoId", diskInfoId);
        params.put("sids", sids);

        List<DiskAcl> diskAcls = diskAclManager.find(hql, params);

        return diskAcls;
    }

    /**
     * 判断是否拥有权限.
     */
    public boolean hasPermission(Long diskInfoId, String userId) {
        List<Long> sids = this.findSidIdsByUser(userId);

        String hql = "select diskInfo from DiskInfo diskInfo left join diskInfo.diskRule.diskAcls diskAcl "
                + " where diskInfo.id=:infoId "
                + " and (diskAcl.diskSid.id in (:sids) or diskInfo.creator=:owner) ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("infoId", diskInfoId);
        params.put("sids", sids);
        params.put("owner", userId);

        DiskInfo diskInfo = diskInfoManager.findUnique(hql, params);

        if (diskInfo != null) {
            return true;
        }

        return false;
    }

    /**
     * 判断拥有权限.
     */
    public boolean hasPermission(Long id, String userId, int mask) {
        DiskInfo diskInfo = diskInfoManager.get(id);

        if (diskInfo == null) {
            logger.info("cannot find diskInfo : {}", id);

            return false;
        }

        if (userId == null) {
            logger.info("userId cannot be null");

            return false;
        }

        if (userId.equals(diskInfo.getCreator())) {
            logger.info("owner : {} {} {}", id, diskInfo.getCreator(), userId);

            return true;
        }

        logger.info("not owner : {} {} {}", id, diskInfo.getCreator(), userId);

        List<DiskAcl> diskAcls = this.findAcls(id, userId);

        for (DiskAcl diskAcl : diskAcls) {
            if ((diskAcl.getMask() & mask) != 0) {
                logger.info("hit : {} {} {} {} {} {}", id, userId, mask,
                        diskAcl.getEntityCatalog(), diskAcl.getEntityRef(),
                        diskAcl.getMask());

                return true;
            }
        }

        logger.info("miss : {} {} {}", id, userId, mask);

        return false;
    }

    /**
     * 判断缺少权限.
     */
    public boolean lackPermission(Long id, String userId, int mask) {
        DiskInfo diskInfo = diskInfoManager.get(id);

        if (diskInfo == null) {
            logger.info("cannot find diskInfo : {}", id);

            return true;
        }

        if (userId == null) {
            logger.info("userId cannot be null");

            return true;
        }

        if (userId.equals(diskInfo.getCreator())) {
            logger.info("owner : {} {} {}", id, diskInfo.getCreator(), userId);

            return false;
        }

        logger.info("not owner : {} {} {}", id, diskInfo.getCreator(), userId);

        List<DiskAcl> diskAcls = this.findAcls(id, userId);

        for (DiskAcl diskAcl : diskAcls) {
            if ((diskAcl.getMask() & mask) != 0) {
                logger.info("hit : {} {} {} {} {} {}", id, userId, mask,
                        diskAcl.getEntityCatalog(), diskAcl.getEntityRef(),
                        diskAcl.getMask());

                return false;
            }
        }

        logger.info("miss : {} {} {}", id, userId, mask);

        return true;
    }

    /**
     * 合并权限.
     */
    public int mergeMask(Long diskInfoId, String userId) {
        if (diskInfoId == null) {
            logger.info("diskInfoId cannot be null");

            return MASK_NONE;
        }

        if (userId == null) {
            logger.info("userId cannot be null");

            return MASK_NONE;
        }

        DiskInfo diskInfo = diskInfoManager.get(diskInfoId);

        if (diskInfo == null) {
            logger.info("cannot find diskInfo : {}", diskInfoId);

            return MASK_NONE;
        }

        if (userId.equals(diskInfo.getCreator())) {
            logger.info("owner : {} {} {}", diskInfoId, diskInfo.getCreator(),
                    userId);

            return MASK_ALL;
        }

        List<DiskAcl> diskAcls = this.findAcls(diskInfoId, userId);

        int mask = 0;

        for (DiskAcl diskAcl : diskAcls) {
            mask |= diskAcl.getMask();
        }

        logger.info("mask merged : {}", mask);

        return mask;
    }

    // ~
    /**
     * 添加acl.
     */
    public Result<DiskInfo> addAccessEntry(long infoId, String catalog,
            String value, String name, int mask) {
        Result<DiskInfo> diskInfoResult = this.diskBaseInternalService
                .findActive(infoId);

        if (diskInfoResult.isFailure()) {
            return diskInfoResult;
        }

        DiskInfo diskInfo = diskInfoResult.getData();
        DiskRule diskRule = this.createOrGetCurrentRule(diskInfo);

        DiskSid diskSid = this.createOrGetSid(catalog, value);
        diskSid.setName(name);
        diskSidManager.save(diskSid);

        DiskAcl diskAcl = this.findAclByRuleAndSid(diskRule.getId(),
                diskSid.getId());

        if (diskAcl == null) {
            diskAcl = new DiskAcl();
            diskAcl.setDiskRule(diskRule);
            diskAcl.setDiskSid(diskSid);
            diskAclManager.save(diskAcl);
        }

        if ("owner".equals(diskAcl.getType())) {
            logger.info("skip owner");

            return Result.failure(409, "skip owner");
        }

        diskAcl.setMask(mask);
        diskAclManager.save(diskAcl);

        return Result.success(diskInfo);
    }

    /**
     * 删除acl.
     */
    public Result<DiskInfo> removeAccessEntry(long infoId, long aclId) {
        Result<DiskInfo> diskInfoResult = this.diskBaseInternalService
                .findActive(infoId);

        if (diskInfoResult.isFailure()) {
            return diskInfoResult;
        }

        DiskAcl diskAcl = this.diskAclManager.get(aclId);

        if (diskAcl == null) {
            return Result.failure(404, "no acl " + aclId);
        }

        DiskInfo diskInfo = diskInfoResult.getData();
        DiskRule diskRule = this.createOrGetCurrentRule(diskInfo);

        if (!diskAcl.getDiskRule().getId().equals(diskRule.getId())) {
            logger.info("acl {} not belongs to rule {}", diskAcl.getId(),
                    diskRule.getId());

            return Result.failure(409, "acl not belongs to rule");
        }

        long sidId = diskAcl.getDiskSid().getId();
        // 因为可能从继承rule创建私有rule，所以要使用新的rule和sid重新获取acl
        diskAcl = this.findAclByRuleAndSid(diskRule.getId(), sidId);

        if (diskAcl == null) {
            return Result.failure(404, "no acl " + aclId);
        }

        if ("owner".equals(diskAcl.getType())) {
            logger.info("cannot remove owner acl : {}", diskAcl.getId());

            return Result.failure(409, "cannot remove owner acl");
        }

        diskAclManager.remove(diskAcl);

        return Result.success(diskInfo);
    }

    /**
     * 根据rule和sid查询acl.
     */
    public DiskAcl findAclByRuleAndSid(long ruleId, long sidId) {
        String hql = "from DiskAcl where diskRule.id=? and diskSid.id=?";

        return diskAclManager.findUnique(hql, ruleId, sidId);
    }

    /**
     * 获取当前规则，或创建私有规则.
     */
    public DiskRule createOrGetCurrentRule(DiskInfo diskInfo) {
        DiskRule diskRule = null;

        if ("true".equals(diskInfo.getInherit())) {
            // 如果是继承的权限，要先建立自己的规则再管理权限。
            diskRule = this.createPrivateRule(diskInfo);
        } else {
            // 如果是当前节点私有的权限，可以直接修改
            diskRule = diskInfo.getDiskRule();

            if (diskRule == null) {
                // 这个算数据补偿，应该报错了吧？
                diskRule = this.createPrivateRule(diskInfo);
            }
        }

        return diskRule;
    }

    /**
     * 新规则.
     */
    public DiskRule createRule() {
        DiskRule diskRule = new DiskRule();
        diskRule.setCreateTime(new Date());
        diskRuleManager.save(diskRule);

        return diskRule;
    }

    /**
     * 创建私有权限规则.
     */
    public DiskRule createPrivateRule(DiskInfo diskInfo) {
        DiskRule oldRule = diskInfo.getDiskRule();
        DiskRule diskRule = this.createRule();
        diskInfo.setInherit("false");
        diskInfo.setDiskRule(diskRule);
        diskInfoManager.save(diskInfo);

        if (oldRule != null) {
            // 复制之前的所有权限
            for (DiskAcl diskAcl : oldRule.getDiskAcls()) {
                if (diskAcl == null) {
                    logger.info("skip null acl");

                    continue;
                }

                if ("owner".equals(diskAcl.getType())) {
                    continue;
                }

                if (diskAcl.getDiskSid() == null) {
                    logger.info("skip null sid : {}", diskAcl.getId());

                    continue;
                }

                this.addAclToRuleWithAcl(diskRule, diskAcl);
            }
        }

        // 每个规则都加上所有者
        this.addAclToRule(diskRule, "owner", MASK_ALL, "user",
                diskInfo.getCreator());
        // 每个规则都加上管理员
        this.addAclToRule(diskRule, "", MASK_ALL, "role", "admin");
        // 递归更新子节点rule
        updateChildrenRule(diskInfo);

        return diskRule;
    }

    /**
     * 创建或获取sid
     */
    public DiskSid createOrGetSid(String catalog, String value) {
        String hql = "from DiskSid where catalog=? and value=?";
        DiskSid diskSid = diskSidManager.findUnique(hql, catalog, value);

        if (diskSid == null) {
            diskSid = new DiskSidBuilder().build();
            diskSid.setCatalog(catalog);
            diskSid.setValue(value);

            diskSidManager.save(diskSid);
        }

        return diskSid;
    }

    public DiskSid createOrGetSid(String value) {
        String hql = "from DiskSid where value=?";
        DiskSid diskSid = diskSidManager.findUnique(hql, value);

        if (diskSid == null) {
            diskSid = new DiskSidBuilder().build();
            diskSid.setCatalog("user");
            diskSid.setValue(value);

            diskSidManager.save(diskSid);
        }

        return diskSid;
    }

    /**
     * 向rule添加acl.
     */
    public void addAclToRule(DiskRule diskRule, String type, int mask,
            String catalog, String value) {
        DiskSid diskSid = this.createOrGetSid(catalog, value);
        long sidId = diskSid.getId();
        this.addAclToRuleWithSid(diskRule, type, mask, sidId);
    }

    public void addAclToRuleWithAcl(DiskRule diskRule, DiskAcl diskAcl) {
        this.addAclToRuleWithSid(diskRule, diskAcl.getType(),
                diskAcl.getMask(), diskAcl.getDiskSid().getId());
    }

    public void addAclToRuleWithSid(DiskRule diskRule, String type, int mask,
            long sidId) {
        DiskSid diskSid = diskSidManager.get(sidId);
        DiskAcl diskAcl = new DiskAcl();
        diskAcl.setType(type);
        diskAcl.setMask(mask);
        diskAcl.setDiskSid(diskSid);
        diskAcl.setDiskRule(diskRule);
        diskAclManager.save(diskAcl);
    }

    /**
     * 更新所有子节点权限.
     */
    public void updateChildrenRule(DiskInfo parent) {
        for (DiskInfo child : parent.getDiskInfos()) {
            if (!"true".equals(child.getInherit())) {
                continue;
            }

            child.setDiskRule(parent.getDiskRule());
            diskInfoManager.save(child);
            updateChildrenRule(child);
        }
    }

    /**
     * DiskSid.
     */
    public DiskSid findSid(long id) {
        return diskSidManager.get(id);
    }

    /**
     * DiskAcl.
     */
    public DiskAcl findAcl(long id) {
        return diskAclManager.get(id);
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskAclManager(DiskAclManager diskAclManager) {
        this.diskAclManager = diskAclManager;
    }

    @Resource
    public void setDiskRuleManager(DiskRuleManager diskRuleManager) {
        this.diskRuleManager = diskRuleManager;
    }

    @Resource
    public void setDiskSidManager(DiskSidManager diskSidManager) {
        this.diskSidManager = diskSidManager;
    }

    @Resource
    public void setAuthzClient(AuthzClient authzClient) {
        this.authzClient = authzClient;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setDiskBaseInternalServcie(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }
}
