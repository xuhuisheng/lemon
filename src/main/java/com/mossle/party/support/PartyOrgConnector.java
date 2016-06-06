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
        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            if (partyStruct.getParentEntity().getPartyType().getType() == PartyConstants.TYPE_POSITION) {
                return partyStruct.getParentEntity().getLevel();
            }
        }

        // 如果没有对应的岗位，就返回-1，就是最低的级别
        return -1;
    }

    /**
     * 根据人员和对应的岗位名称，获得离这个人员最近的岗位的级别.
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
            return null;
        }

        return superior.getRef();
    }

    /**
     * 获得人员对应的最近的岗位下的所有用户.
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
        PartyEntity partyEntity = this.findUpperDepartment(child);

        // 如果存在上级部门
        while (partyEntity != null) {
            logger.debug("partyEntity : {}, {}", partyEntity.getId(),
                    partyEntity.getName());

            // 遍历上级部门的每个叶子
            for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                PartyEntity childPartyEntity = partyStruct.getChildEntity();
                logger.debug("child : {}, {}", childPartyEntity.getId(),
                        childPartyEntity.getName());

                PartyEntity superior = null;

                // 如果叶子是岗位，并且这个职位是管理岗位
                if ((childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_POSITION)
                        && this.isAdmin(partyStruct)) {
                    // 就找这个岗位下面的管理者
                    superior = this.findAdministrator(childPartyEntity);
                } else if ((childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_USER)
                        && this.isAdmin(partyStruct)) {
                    // 如果是人员，并且人员是管理者，也当做是管理者
                    superior = childPartyEntity;
                }

                if (superior != null) {
                    return superior;
                }
            }

            // 递归获取上级部门
            partyEntity = this.findUpperDepartment(partyEntity);
        }

        // 找不到上级领导
        return null;
    }

    public boolean isAdmin(PartyStruct partyStruct) {
        if (partyStruct == null) {
            return false;
        }

        if (partyStruct.getAdmin() == null) {
            return false;
        }

        return partyStruct.getAdmin() == 1;
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
        PartyEntity partyEntity = this.findUpperDepartment(parent);

        while (partyEntity != null) {
            // 如果是组织，部门或公司
            if (partyEntity.getPartyType().getType() == PartyConstants.TYPE_ORG) {
                for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                    PartyEntity child = partyStruct.getChildEntity();

                    // 遍历组织下级岗位
                    if ((child.getPartyType().getType() == PartyConstants.TYPE_POSITION)
                            && child.getName().equals(positionName)) {
                        // 遇到岗位名字一致的，就放到userIds里
                        for (PartyStruct ps : child.getChildStructs()) {
                            userIds.add(ps.getChildEntity().getRef());
                        }
                    }
                }
            } else if ((parent.getPartyType().getType() == PartyConstants.TYPE_POSITION)
                    && parent.getName().equals(positionName)) {
                // 如果parent已经是岗位了，而且名字与期望的positionName一致
                for (PartyStruct partyStruct : parent.getChildStructs()) {
                    PartyEntity child = partyStruct.getChildEntity();

                    // 就把岗位下的人直接附加到userIds里
                    if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
                        userIds.add(child.getRef());
                    }
                }
            }

            if (userIds.isEmpty()) {
                // 如果没找到userIds，递归到更上一级的部门，继续找
                partyEntity = this.findUpperDepartment(partyEntity);
            } else {
                break;
            }
        }

        return userIds;
    }

    /**
     * 获得上级部门.
     */
    public PartyEntity findUpperDepartment(PartyEntity child) {
        if (child == null) {
            logger.info("child is null");

            return null;
        }

        for (PartyStruct partyStruct : child.getParentStructs()) {
            PartyEntity parent = partyStruct.getParentEntity();

            if (parent == null) {
                logger.info("parent is null, child : {}", child.getName());

                continue;
            }

            logger.debug("parent : {}, child : {}", parent.getName(),
                    child.getName());
            logger.debug("admin : [{}]", partyStruct.getAdmin());

            if ((parent.getPartyType().getType() == PartyConstants.TYPE_ORG)
                    && this.isNotAdmin(partyStruct)) {
                logger.debug("upper department : {}, admin : [{}]",
                        parent.getName(), partyStruct.getAdmin());

                return parent;
            } else {
                return this.findUpperDepartment(parent);
            }
        }

        return null;
    }

    // ~ ==================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
