package com.mossle.party.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.party.PartyConstants;
import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.manager.PartyEntityManager;

import com.mossle.spi.party.PartyConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyConnectorImpl implements PartyConnector {
    private static Logger logger = LoggerFactory
            .getLogger(PartyConnectorImpl.class);
    private PartyEntityManager partyEntityManager;

    public String findIdByRef(String ref) {
        PartyEntity partyEntity = this.partyEntityManager.findUniqueBy("ref",
                ref);

        if (partyEntity == null) {
            return null;
        }

        Long id = partyEntity.getId();

        return Long.toString(id);
    }

    public String findIdByPositionName(String name) {
        String hql = "from PartyEntity where name=? and partyType.type="
                + PartyConstants.TYPE_POSITION;
        PartyEntity partyEntity = partyEntityManager.findUnique(hql, name);

        if (partyEntity == null) {
            return null;
        }

        Long id = partyEntity.getId();

        return Long.toString(id);
    }

    /**
     * 根据用户ID查询用户所在公司或者所属库
     */
    public String findCompanyNameByUserId(String userId) {
        // 找到userId对应的partyEntity
        String hql = "from PartyEntity where partyType.type=? and ref=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                PartyConstants.TYPE_USER, userId);

        if (partyEntity == null) {
            logger.info("cannot find user : {}", userId);

            return null;
        }

        PartyEntity companyPartyEntity = this.findCompany(partyEntity);

        if (companyPartyEntity == null) {
            logger.info("cannot find company : {}", userId);

            return null;
        }

        return companyPartyEntity.getName();
    }

    public PartyEntity findCompany(PartyEntity partyEntity) {
        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            PartyEntity parentEntity = partyStruct.getParentEntity();

            if ("公司".equals(parentEntity.getPartyType().getName())) {
                return parentEntity;
            }

            PartyEntity companyEntity = this.findCompany(parentEntity);

            if (companyEntity != null) {
                return companyEntity;
            }
        }

        return null;
    }

    public String findIdByNearestPositionName(String userId, String positionName) {
        PartyEntity child = this.findUser(userId);

        // 得到上级部门
        PartyEntity partyEntity = this.findUpperDepartment(child);

        // 如果存在上级
        while (partyEntity != null) {
            logger.info("check : {}", partyEntity.getName());

            Set<Long> visitedIds = new HashSet<Long>();
            String partyId = this.findPositionIdByParent(partyEntity,
                    positionName, visitedIds);

            if (partyId != null) {
                return partyId;
            }

            // 如果没找到partyId，递归到更上一级的部门，继续找
            partyEntity = this.findUpperDepartment(partyEntity);
        }

        return null;
    }

    /**
     * 根据userId获得对应的PartyEntity.
     *
     * @param userId String
     * @return PartyEntity
     */
    public PartyEntity findUser(String userId) {
        // 找到userId对应的partyEntity
        String hql = "from PartyEntity where partyType.type=? and ref=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                PartyConstants.TYPE_USER, userId);

        return partyEntity;
    }

    /**
     * 获得上级部门.
     *
     * @param child PartyEntity
     * @return PartyEntity
     */
    public PartyEntity findUpperDepartment(PartyEntity child) {
        if (child == null) {
            logger.info("child is null");

            return null;
        }

        for (PartyStruct partyStruct : this.sortPartyStructs(child
                .getParentStructs())) {
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

    /**
     * 递归查找部门，子部门下的所有岗位.
     *
     * @param partyEntity PartyEntity
     * @param positionName String
     * @param visitedIds Set
     * @return String
     */
    public String findPositionIdByParent(PartyEntity partyEntity,
            String positionName, Set<Long> visitedIds) {
        if (partyEntity == null) {
            return null;
        }

        if (visitedIds.contains(partyEntity.getId())) {
            logger.info("already visit : {}, just skip", partyEntity.getId());

            return null;
        }

        visitedIds.add(partyEntity.getId());

        // 如果是组织，部门或公司
        if (partyEntity.getPartyType().getType() == PartyConstants.TYPE_ORG) {
            for (PartyStruct partyStruct : this.sortPartyStructs(partyEntity
                    .getChildStructs())) {
                PartyEntity child = partyStruct.getChildEntity();
                logger.info("department : {} {}", partyEntity.getName(),
                        child.getName());

                // 遍历组织下级岗位
                if ((child.getPartyType().getType() == PartyConstants.TYPE_POSITION)
                        && child.getName().equals(positionName)) {
                    logger.info("return {}", child.getId());

                    // 遇到岗位名字一致的，就返回
                    return Long.toString(child.getId());
                }
            }
        } else if ((partyEntity.getPartyType().getType() == PartyConstants.TYPE_POSITION)
                && partyEntity.getName().equals(positionName)) {
            // 如果parent已经是岗位了，而且名字与期望的positionName一致
            logger.info("position : {}", partyEntity.getName());
            logger.info("return {}", partyEntity.getId());

            // 直接返回岗位
            return Long.toString(partyEntity.getId());
        }

        // 如果本部门内没找到岗位，继续搜索下级部门
        for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
            String partyId = this.findPositionIdByParent(
                    partyStruct.getChildEntity(), positionName,
                    new HashSet<Long>(visitedIds));

            if (partyId != null) {
                return partyId;
            }
        }

        return null;
    }

    /**
     * 排序.
     *
     * @param partyStructSet Set
     * @return List
     */
    public List<PartyStruct> sortPartyStructs(Set<PartyStruct> partyStructSet) {
        List<PartyStruct> partyStructList = new ArrayList<PartyStruct>(
                partyStructSet);
        Collections.sort(partyStructList, new PartyStructComparator());

        return partyStructList;
    }

    /**
     * 是否负责人.
     *
     * @param partyStruct PartyStruct
     * @return boolean
     */
    public boolean isAdmin(PartyStruct partyStruct) {
        if (partyStruct == null) {
            return false;
        }

        if (partyStruct.getAdmin() == null) {
            return false;
        }

        return partyStruct.getAdmin() == 1;
    }

    /**
     * 是否员工.
     *
     * @param partyStruct PartyStruct
     * @return boolean
     */
    public boolean isNotAdmin(PartyStruct partyStruct) {
        return !this.isAdmin(partyStruct);
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
