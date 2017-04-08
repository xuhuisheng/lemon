package com.mossle.party.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;

import com.mossle.party.PartyConstants;
import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.manager.PartyEntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组织机构接口.
 */
public class PartyOrgConnector implements OrgConnector {
    private static Logger logger = LoggerFactory
            .getLogger(PartyOrgConnector.class);
    private PartyEntityManager partyEntityManager;

    /**
     * 根据userId获得对应的PartyEntity.
     */
    public PartyEntity findUser(String userId) {
        // 找到userId对应的partyEntity
        String hql = "from PartyEntity where partyType.type=? and ref=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                PartyConstants.TYPE_USER, userId);

        return partyEntity;
    }

    /**
     * 获得人员对应的岗位的级别.
     */
    public int getJobLevelByUserId(String userId) {
        // 找到userId对应的partyEntity
        PartyEntity partyEntity = this.findUser(userId);

        if (partyEntity == null) {
            logger.info("cannot find user : {}", userId);

            return -1;
        }

        // 如果直接上级是岗位，就返回岗位级别
        // for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
        // if (partyStruct.getParentEntity().getPartyType().getType() == PartyConstants.TYPE_POSITION) {
        // return partyStruct.getParentEntity().getLevel();
        // }
        // }
        for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
            if ("user-position".equals(partyStruct.getPartyStructType()
                    .getType())) {
                return partyStruct.getChildEntity().getLevel();
            }
        }

        // 如果没有对应的岗位，就返回-1，就是最低的级别
        return -1;
    }

    /**
     * 根据人员和对应的岗位名称，获得离这个人员最近的岗位的级别.
     * 
     * TODO: 这里目前肯定有问题，以后记得研究 2016-07-06
     */
    public int getJobLevelByInitiatorAndPosition(String userId,
            String positionName) {
        // 获得岗位对应的partyEntity
        String hql = "from PartyEntity where partyType.type=? and name=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                PartyConstants.TYPE_POSITION, positionName);

        // 直接返回级别
        return partyEntity.getLevel();
    }

    /**
     * 获得上级领导.
     */
    public String getSuperiorId(String userId) {
        logger.debug("user id : {}", userId);

        PartyEntity partyEntity = this.findUser(userId);
        logger.debug("party entity : {}, {}", partyEntity.getId(),
                partyEntity.getName());

        PartyEntity superior = this.findSuperior(partyEntity);

        if (superior == null) {
            logger.info("cannot find superiour : {} {}", partyEntity.getName(),
                    partyEntity.getId());

            return null;
        }

        return superior.getRef();
    }

    /**
     * 获得人员对应的最近的岗位下的所有用户.
     * 
     * TODO: 这里目前肯定有问题，以后记得研究 2016-07-06
     */
    public List<String> getPositionUserIds(String userId, String positionName) {
        PartyEntity partyEntity = this.findUser(userId);

        return this.findPositionUserIds(partyEntity, positionName);
    }

    /**
     * 获取这个人的所有的直接部门或者公司.
     */
    public List<OrgDTO> getOrgsByUserId(String userId) {
        PartyEntity partyEntity = this.findUser(userId);

        if (partyEntity == null) {
            return Collections.emptyList();
        }

        List<OrgDTO> orgDtos = new ArrayList<OrgDTO>();

        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            PartyEntity parent = partyStruct.getParentEntity();

            if (parent.getPartyType().getType() == PartyConstants.TYPE_ORG) {
                OrgDTO orgDto = new OrgDTO();
                orgDto.setId(Long.toString(parent.getId()));
                orgDto.setName(parent.getName());
                orgDto.setTypeName(parent.getPartyType().getName());
                orgDto.setType(parent.getPartyType().getType());
                orgDto.setRef(parent.getRef());
                orgDtos.add(orgDto);
            }
        }

        return orgDtos;
    }

    // ~ ==================================================
    /**
     * 获得直接上级.
     */
    public PartyEntity findSuperior(PartyEntity child) {
        // 得到上级部门
        PartyEntity partyEntity = this.findUpperDepartment(child, true);

        // 如果存在上级部门
        while (partyEntity != null) {
            logger.debug("partyEntity : {}, {}", partyEntity.getId(),
                    partyEntity.getName());

            // 遍历上级部门的每个叶子
            for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                if (!"manage"
                        .equals(partyStruct.getPartyStructType().getType())) {
                    continue;
                }

                // 遍历管理关系
                PartyEntity childPartyEntity = partyStruct.getChildEntity();
                logger.debug("child : {}, {}", childPartyEntity.getId(),
                        childPartyEntity.getName());

                if (childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_USER) {
                    // 如果是人员，直接返回
                    return childPartyEntity;
                } else if (childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_POSITION) {
                    // 如果是岗位，继续查找部门下所有岗位对应的人员，返回
                    List<PartyEntity> users = this.findByPosition(partyEntity,
                            childPartyEntity.getName());

                    if (!users.isEmpty()) {
                        return users.get(0);
                    }
                }
            }

            // 递归获取上级部门
            partyEntity = this.findUpperDepartment(partyEntity, true);
        }

        // 找不到上级领导
        return null;
    }

    /**
     * 在本部门下，查找对应职位的人员.
     */
    public List<PartyEntity> findByPosition(PartyEntity partyEntity,
            String positionName) {
        List<PartyEntity> partyEntities = new ArrayList<PartyEntity>();

        for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
            if (!"struct".equals(partyStruct.getPartyStructType().getType())) {
                continue;
            }

            PartyEntity childPartyEntity = partyStruct.getChildEntity();
            logger.debug("child : {}, {}", childPartyEntity.getId(),
                    childPartyEntity.getName());

            if (childPartyEntity.getPartyType().getType() != PartyConstants.TYPE_USER) {
                continue;
            }

            if (this.hasPosition(childPartyEntity, positionName)) {
                partyEntities.add(childPartyEntity);
            }
        }

        return partyEntities;
    }

    /**
     * 判断用户是否包含对应岗位.
     */
    public boolean hasPosition(PartyEntity partyEntity, String positionName) {
        for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
            if (!"user-position".equals(partyStruct.getPartyStructType()
                    .getType())) {
                continue;
            }

            PartyEntity childPartyEntity = partyStruct.getChildEntity();
            logger.debug("child : {}, {}", childPartyEntity.getId(),
                    childPartyEntity.getName());

            if (childPartyEntity.getName().equals(positionName)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAdmin(PartyStruct partyStruct) {
        if (partyStruct == null) {
            return false;
        }

        // if (partyStruct.getAdmin() == null) {
        // return false;
        // }
        // return partyStruct.getAdmin() == 1;
        PartyEntity department = partyStruct.getParentEntity();
        PartyEntity user = partyStruct.getChildEntity();

        logger.info("department : {} {}", department.getName(),
                department.getId());

        // 遍历上级部门的每个叶子
        for (PartyStruct childPartyStruct : department.getChildStructs()) {
            if (!"manage".equals(childPartyStruct.getPartyStructType()
                    .getType())) {
                continue;
            }

            // 遍历管理关系
            PartyEntity childPartyEntity = childPartyStruct.getChildEntity();
            logger.debug("child : {}, {}", childPartyEntity.getId(),
                    childPartyEntity.getName());

            if (childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_USER) {
                // 如果是人员，直接返回
                if (childPartyEntity.getId().equals(user.getId())) {
                    return true;
                }
            } else if (childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_POSITION) {
                // 如果是岗位，继续查找部门下所有岗位对应的人员，返回
                List<PartyEntity> users = this.findByPosition(department,
                        childPartyEntity.getName());

                for (PartyEntity userPartyEntity : users) {
                    if (userPartyEntity.getId().equals(user.getId())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isNotAdmin(PartyStruct partyStruct) {
        return !this.isAdmin(partyStruct);
    }

    /**
     * 获取岗位的管理者.
     */
    public PartyEntity findAdministrator(PartyEntity parent) {
        for (PartyStruct partyStruct : parent.getChildStructs()) {
            PartyEntity partyEntity = null;
            PartyEntity child = partyStruct.getChildEntity();
            logger.debug("child : {}, {}", child.getId(), child.getName());

            // 完全不考虑岗位下面有其他组织或者岗位的情况
            // 认为岗位下直接就是人员
            if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
                // 首先岗位必须是管理岗位
                // 如果岗位下是一个人，这个人就是部门的管理者
                // 在想，如果不是管理者，应该也可以是上级吧？比如管理岗位下面的所有人都应该是管理者
                return child;
            }
        }

        return null;
    }

    /**
     * 找到离parent最近的岗位下的人员.
     */
    public List<String> findPositionUserIds(PartyEntity parent,
            String positionName) {
        List<String> userIds = new ArrayList<String>();

        // 获得上级部门
        PartyEntity partyEntity = this.findUpperDepartment(parent, false);

        while (partyEntity != null) {
            // 如果是组织，部门或公司
            if (partyEntity.getPartyType().getType() == PartyConstants.TYPE_ORG) {
                for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                    PartyEntity child = partyStruct.getChildEntity();

                    // 遍历组织下属所有员工
                    if (child.getPartyType().getType() != PartyConstants.TYPE_USER) {
                        continue;
                    }

                    // 如果员工拥有对应的岗位，就放到userIds里
                    for (PartyStruct ps : child.getChildStructs()) {
                        // 只搜索人员岗位关系
                        if (ps.getPartyStructType().getId() != 5) {
                            continue;
                        }

                        if (ps.getChildEntity().getName().equals(positionName)) {
                            // 拥有对应的岗位，就放到userIds里
                            userIds.add(child.getRef());
                        }
                    }
                }
            }

            /*
             * else if ((parent.getPartyType().getType() == PartyConstants.TYPE_POSITION) &&
             * parent.getName().equals(positionName)) { // 如果parent已经是岗位了，而且名字与期望的positionName一致 for (PartyStruct
             * partyStruct : parent.getChildStructs()) { PartyEntity child = partyStruct.getChildEntity();
             * 
             * // 就把岗位下的人直接附加到userIds里 if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
             * userIds.add(child.getRef()); } } }
             */
            if (userIds.isEmpty()) {
                // 如果没找到userIds，递归到更上一级的部门，继续找
                partyEntity = this.findUpperDepartment(partyEntity, false);
            } else {
                break;
            }
        }

        return userIds;
    }

    /**
     * 获得上级部门.
     */
    public PartyEntity findUpperDepartment(PartyEntity child,
            boolean skipAdminDepartment) {
        if (child == null) {
            logger.info("child is null");

            return null;
        }

        for (PartyStruct partyStruct : child.getParentStructs()) {
            PartyEntity parent = partyStruct.getParentEntity();

            if (parent == null) {
                logger.info("parent is null, child : {} {}", child.getName(),
                        child.getId());

                continue;
            }

            logger.debug("parent : {}, child : {}", parent.getName(),
                    child.getName());
            logger.debug("admin : [{}]", partyStruct.getAdmin());

            if (parent.getPartyType().getType() == PartyConstants.TYPE_ORG) {
                if (skipAdminDepartment && this.isAdmin(partyStruct)) {
                    return this
                            .findUpperDepartment(parent, skipAdminDepartment);
                } else {
                    // 不是当前部门负责人才会返回这个部门实体，否则返回再上一级部门
                    logger.debug("upper department : {}, admin : [{}]",
                            parent.getName(), partyStruct.getAdmin());

                    return parent;
                }
            }
        }

        logger.info("cannot find parent department : {} {}", child.getName(),
                child.getId());

        return null;
    }

    // ~ ==================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
