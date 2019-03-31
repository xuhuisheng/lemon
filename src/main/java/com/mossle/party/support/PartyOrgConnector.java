package com.mossle.party.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;

import com.mossle.party.PartyConstants;
import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.manager.PartyEntityManager;

import org.apache.commons.lang3.StringUtils;

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
     * 获得人员对应的岗位的级别.
     *
     * @param userId String
     * @return int
     */
    public int getJobLevelByUserId(String userId) {
        // 找到userId对应的partyEntity
        PartyEntity partyEntity = this.findUser(userId);

        if (partyEntity == null) {
            logger.info("cannot find user : {}", userId);

            return -1;
        }

        // 如果直接上级是岗位，就返回岗位级别
        for (PartyStruct partyStruct : this.sortPartyStructs(partyEntity
                .getParentStructs())) {
            if (partyStruct.getParentEntity().getPartyType().getType() == PartyConstants.TYPE_POSITION) {
                return partyStruct.getParentEntity().getLevel();
            }
        }

        // 如果没有对应的岗位，就返回-1，就是最低的级别
        return -1;
    }

    /**
     * 根据人员和对应的岗位名称，获得离这个人员最近的岗位的级别.
     *
     * @param userId String
     * @param positionName String
     * @return int
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
     *
     * @param userId String
     * @return String
     */
    public String getSuperiorId(String userId) {
        logger.debug("user id : {}", userId);

        PartyEntity partyEntity = this.findUser(userId);

        if (partyEntity == null) {
            logger.info("cannot find partyEntity : {}", userId);

            return null;
        }

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
     * @param userId String
     * @param positionName String
     * @return List
     */
    public List<String> getPositionUserIds(String userId, String positionName) {
        logger.info("getPositionUserIds : userId : {}, positionName : {}",
                userId, positionName);

        PartyEntity partyEntity = this.findUser(userId);

        List<String> list = this.findPositionUserIds(partyEntity, positionName);
        Set<String> set = new HashSet<String>(list);

        return new ArrayList<String>(set);
    }

    /**
     * 获取这个人的所有的直接部门或者公司.
     *
     * @param userId String
     * @return List
     */
    public List<OrgDTO> getOrgsByUserId(String userId) {
        PartyEntity partyEntity = this.findUser(userId);

        if (partyEntity == null) {
            logger.info("cannot find party by userId : {}", userId);
            return Collections.emptyList();
        }

        List<OrgDTO> orgDtos = this.getParentOrgsByPartyEntity(partyEntity);

        return orgDtos;
    }

    public List<OrgDTO> getParentOrgsByPartyEntity(PartyEntity partyEntity) {
        boolean isPartTime = false; // 是否有主职，默认没有

        if (partyEntity == null) {
            return Collections.emptyList();
        }

        List<OrgDTO> orgDtos = new ArrayList<OrgDTO>();

        for (PartyStruct partyStruct : this.sortPartyStructs(partyEntity
                .getParentStructs())) {
            // sven dazer
            if ((partyStruct.getPartTime() != null)
                    && (partyStruct.getPartTime() == 1)) { // 判断用户有没有在部门下面担任主职
                isPartTime = true; // 有主职
            }

            PartyEntity parent = partyStruct.getParentEntity();

            if (parent.getPartyType().getType() != PartyConstants.TYPE_ORG) {
                continue;
            }

            if (parent.getName().endsWith("~")) {
                continue;
            }

            OrgDTO orgDto = new OrgDTO();
            orgDto.setId(Long.toString(parent.getId()));
            orgDto.setName(parent.getName());
            orgDto.setTypeName(parent.getPartyType().getName());
            orgDto.setType(parent.getPartyType().getType());
            orgDto.setRef(parent.getRef());
            orgDtos.add(orgDto);
        }

        if (!orgDtos.isEmpty()) {
            return orgDtos;
        }

        for (PartyStruct partyStruct : this.sortPartyStructs(partyEntity
                .getParentStructs())) {
            PartyEntity parent = partyStruct.getParentEntity();

            // sven dazer
            if ((partyStruct.getPartTime() == null)
                    || (partyStruct.getPartTime() == 1)) { // 判断这个人是不是主职，是主职就进去找他的所在的部门. sven
                orgDtos.addAll(this.getParentOrgsByPartyEntity(parent));
            }

            // sven dazer
            if (!isPartTime) { // 如果没有就去查所有的兼职
                orgDtos.addAll(this.getParentOrgsByPartyEntity(parent));
            }
        }

        return orgDtos;
    }

    // ~ ==================================================
    /**
     * 通过汇报线获取直接领导.
     *
     * @param child PartyEntity
     * @return PartyEntity
     */
    public PartyEntity findSuperior(PartyEntity child) {
        for (PartyStruct partyStruct : child.getParentStructs()) {
            if (!"report".equals(partyStruct.getPartyStructType().getType())) {
                continue;
            }

            return partyStruct.getParentEntity();
        }

        logger.info("cannot find superior : {} {} {}", child.getId(),
                child.getCode(), child.getName());

        // 找不到上级领导
        return null;
    }

    /**
     * 获得直接上级. 暂时保留之前按照部门负责人获取汇报线的功能.
     *
     * @param child PartyEntity
     * @return PartyEntity
     */
    public PartyEntity findSuperior2(PartyEntity child) {
        // 得到上级部门
        PartyEntity partyEntity = this.findUpperDepartment(child);

        // 找不到上级部门，直接返回null
        if (partyEntity == null) {
            logger.info("cannot find upper department : {}", child.getId());

            return null;
        }

        // 如果是部门的负责人
        // 如果是员工

        // 如果存在上级部门
        while (partyEntity != null) {
            logger.debug("partyEntity : {}, {}", partyEntity.getId(),
                    partyEntity.getName());

            if (this.isManagerOfDepartment(child, partyEntity)) {
                // 本部门负责人
                List<String> managers = this.findNearestManagers(partyEntity,
                        child);

                if (!managers.isEmpty()) {
                    return findUser(managers.get(0));
                }
            } else {
                // 员工
                List<String> managers = this.findNearestManagers(partyEntity,
                        null);

                if (!managers.isEmpty()) {
                    return findUser(managers.get(0));
                }
            }

            // 递归获取上级部门
            PartyEntity childDepartment = partyEntity;
            partyEntity = this.findUpperDepartment(partyEntity);

            // 找不到上级部门，直接返回null
            if (partyEntity == null) {
                logger.info("cannot find upper department : {}",
                        childDepartment.getId());

                return null;
            }
        }

        logger.info("cannot find upper department : {}", partyEntity.getId());

        // 找不到上级领导
        return null;
    }

    public boolean isManagerOfDepartment(PartyEntity person,
            PartyEntity department) {
        ManagerPool managerPool = this.findManagersOfDepartment(department);

        return managerPool.containsUserId(person.getRef());
    }

    public ManagerPool findManagersOfDepartment(PartyEntity department) {
        ManagerPool managerPool = new ManagerPool();

        // 遍历上级部门的每个叶子
        for (PartyStruct partyStruct : department.getChildStructs()) {
            // 如果不是负责人，直接跳过
            if (!"manage".equals(partyStruct.getPartyStructType().getType())) {
                continue;
            }

            // 遍历管理关系
            PartyEntity childPartyEntity = partyStruct.getChildEntity();
            logger.debug("child : {}, {}", childPartyEntity.getId(),
                    childPartyEntity.getName());

            if (childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_USER) {
                // 人员
                managerPool.addUserId(childPartyEntity.getRef(),
                        partyStruct.getPriority());
            } else if (childPartyEntity.getPartyType().getType() == PartyConstants.TYPE_POSITION) {
                // 如果是岗位，继续查找部门下所有岗位对应的人员，返回
                List<PartyEntity> users = this.findByPosition(department,
                        childPartyEntity.getName());

                for (PartyEntity user : users) {
                    managerPool.addUserId(user.getRef(),
                            partyStruct.getPriority());
                }
            }
        }

        return managerPool;
    }

    public List<String> findNearestManagers(PartyEntity department,
            PartyEntity person) {
        ManagerPool managerPool = this.findManagersOfDepartment(department);

        if (!managerPool.isExists()) {
            return Collections.emptyList();
        }

        if ((person == null) || (!managerPool.containsUserId(person.getRef()))) {
            // 员工
            return managerPool.findNearestManagers(null);
        } else {
            // 负责人
            return managerPool.findNearestManagers(person.getRef());
        }
    }

    /**
     * 在本部门下，查找对应职位的人员.
     *
     * @param partyEntity PartyEntity
     * @param positionName String
     * @return List
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
     *
     * @param partyEntity PartyEntity
     * @param positionName String
     * @return boolean
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

    /**
     * 获取岗位的管理者.
     *
     * @param parent PartyEntity
     * @return PartyEntity
     */
    public PartyEntity findAdministrator(PartyEntity parent) {
        for (PartyStruct partyStruct : this.sortPartyStructs(parent
                .getChildStructs())) {
            PartyEntity partyEntity = null;
            PartyEntity child = partyStruct.getChildEntity();
            logger.info("child : {}, {}", child.getId(), child.getName());

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
     * 寻找距离parent最近的岗位下的人员，默认不包含兄弟部门.
     *
     * @param parent PartyEntity
     * @param positionName String
     * @return List
     */
    public List<String> findPositionUserIds(PartyEntity parent,
            String positionName) {
        return this.findPositionUserIds(parent, positionName, false);
    }

    /**
     * 找到离parent最近的岗位下的人员.
     *
     * @param parent PartyEntity
     * @param positionName String
     * @param includeNeighboor boolean
     * @return List
     */
    public List<String> findPositionUserIds(PartyEntity parent,
            String positionName, boolean includeNeighboor) {
        List<String> userIds = new ArrayList<String>();

        // 获得上级部门
        PartyEntity partyEntity = this.findUpperDepartment(parent);

        while (partyEntity != null) {
            Set<Long> visitedIds = new HashSet<Long>();
            userIds = this.findPositionUserIdsByParent(partyEntity,
                    positionName, visitedIds);

            if (userIds.isEmpty()) {
                // 如果没找到userIds，递归到更上一级的部门，继续找
                partyEntity = this.findUpperDepartment(partyEntity);
            } else {
                break;
            }
        }

        /*
         * //dazer Sven 部门岗位查找最近的岗位有问题，后面修改了把这段去掉。 if(userIds.isEmpty()){//如果没有找到就直接找岗位。
         * userIds=this.findUserByPositionName(positionName); }
         */
        return userIds;
    }

    /**
     * 递归查找部门，子部门下的所有岗位.
     *
     * @param partyEntity PartyEntity
     * @param positionName String
     * @param visitedIds Set
     * @return List
     */
    public List<String> findPositionUserIdsByParent(PartyEntity partyEntity,
            String positionName, Set<Long> visitedIds) {
        if (partyEntity == null) {
            return Collections.emptyList();
        }

        if (visitedIds.contains(partyEntity.getId())) {
            logger.info("already visit : {}, just skip", partyEntity.getId());

            return Collections.emptyList();
        }

        visitedIds.add(partyEntity.getId());

        List<String> userIds = new ArrayList<String>();

        // 如果是组织，部门或公司
        if (partyEntity.getPartyType().getType() == PartyConstants.TYPE_ORG) {
            for (PartyStruct partyStruct : this.sortPartyStructs(partyEntity
                    .getChildStructs())) {
                PartyEntity child = partyStruct.getChildEntity();

                // 遍历组织下级岗位Sven 就近岗位
                if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
                    // 如果下级是用户，判断用户包含对应职位，就可以返回
                    for (PartyStruct childPartyStruct : this
                            .sortPartyStructs(child.getChildStructs())) {
                        if (!"user-position".equals(childPartyStruct
                                .getPartyStructType().getType())) {
                            continue;
                        }

                        if (childPartyStruct.getChildEntity().getPartyType()
                                .getType() != PartyConstants.TYPE_POSITION) {
                            continue;
                        }

                        // 这是个循环，不知道是哪个字段没值还是这个部门或组织下没值
                        logger.info("positionName : " + positionName
                                + " childPartyStruct : "
                                + childPartyStruct.getId() + " entity : "
                                + childPartyStruct.getChildEntity()
                                + " name : "
                                + childPartyStruct.getChildEntity().getName());

                        if (positionName.trim().equals(
                                childPartyStruct.getChildEntity().getName()
                                        .trim())) {
                            logger.info(
                                    "matched : child : {}, struct : {}, position : {}",
                                    child.getName(), childPartyStruct.getId(),
                                    positionName);
                            userIds.add(child.getRef());
                        }
                    }
                }
            }
        } else if ((partyEntity.getPartyType().getType() == PartyConstants.TYPE_POSITION)
                && partyEntity.getName().trim().equals(positionName.trim())) {
            // 如果parent已经是岗位了，而且名字与期望的positionName一致
            for (PartyStruct partyStruct : this.sortPartyStructs(partyEntity
                    .getChildStructs())) {
                PartyEntity child = partyStruct.getChildEntity();

                // 就把岗位下的人直接附加到userIds里
                if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
                    userIds.add(child.getRef());
                }
            }
        }

        // 如果本部门内没找到岗位，继续搜索下级部门
        if (userIds.isEmpty()) {
            for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                userIds.addAll(this.findPositionUserIdsByParent(
                        partyStruct.getChildEntity(), positionName,
                        new HashSet<Long>(visitedIds)));
            }
        }

        return userIds;
    }

    /**
     * 获得上级部门.
     *
     * @param child PartyEnttiy
     * @return PartyEntity
     */
    public PartyEntity findUpperDepartment(PartyEntity child) {
        boolean isPartTime2 = false; // 是否有主职，默认没有

        if (child == null) {
            logger.info("child is null");

            return null;
        }

        for (PartyStruct partyStruct : this.sortPartyStructs(child
                .getParentStructs())) {
            logger.info("child.getParentStructs() size(){}", child
                    .getParentStructs().size());

            if ((partyStruct.getPartTime() != null)
                    && (partyStruct.getPartTime() == 1)) { // 判断用户有没有在部门下面担任主职
                isPartTime2 = true; // 有主职
            }

            PartyEntity parent = partyStruct.getParentEntity();

            if (parent == null) {
                logger.info("parent is null, child : {}", child.getName());

                continue;
            }

            logger.info("parent : {}, child : {}", parent.getName(),
                    child.getName());
            logger.info("admin : [{}]", partyStruct.getAdmin());

            if ((parent.getPartyType().getType() == PartyConstants.TYPE_ORG)
                    && this.isNotAdmin(partyStruct)) {
                logger.info("upper department : {}, admin : [{}]",
                        parent.getName(), partyStruct.getAdmin());

                return parent;
            }
        }

        for (PartyStruct partyStruct : this.sortPartyStructs(child
                .getParentStructs())) {
            PartyEntity parent = partyStruct.getParentEntity();

            if (parent == null) {
                continue;
            }

            if (parent.getParentStructs().size() <= 0) { // 如果父节点(孤儿节点)为空循环下次查找

                continue;
            }

            // sven dazer
            if ((partyStruct.getPartTime() == null)
                    || (partyStruct.getPartTime() == 1)) { // 判断这个人是不是主职，是主职就进去找他的所在的部门. sven

                return this.findUpperDepartment(parent);
            }

            // sven dazer
            if (!isPartTime2) { // 如果没有就去查所有的兼职

                return this.findUpperDepartment(parent);
            }
        }

        return null;
    }

    /**
     * 根据id获取组织信息.
     */
    public OrgDTO findById(String orgId) {
        if (StringUtils.isBlank(orgId)) {
            logger.info("数据不存在 orgId cannot be blank");

            return null;
        }

        PartyEntity partyEntity = this.partyEntityManager.get(Long
                .parseLong(orgId));

        if (partyEntity == null) {
            logger.info("数据不存在   cannot find partyEntity : {}", orgId);

            return null;
        }

        OrgDTO orgDto = new OrgDTO();
        orgDto.setId(Long.toString(partyEntity.getId()));
        orgDto.setName(partyEntity.getName());
        orgDto.setTypeName(partyEntity.getPartyType().getName());
        orgDto.setType(partyEntity.getPartyType().getType());
        orgDto.setRef(partyEntity.getRef());

        return orgDto;
    }

    /**
     * 根据岗位返回用户.
     */
    @SuppressWarnings("unchecked")
    public List<String> findUserByPositionName(String positionName,
            String companyName) {
        logger.info("findUserByPositionName : positionName : {}", positionName);

        String hql = "from PartyEntity where name=? and partyType.type=?";

        // Sven dazer 查找所有公司岗位下面的人员
        List<String> userIds = new ArrayList<String>();
        List<PartyEntity> partyEntitys = this.partyEntityManager.find(hql,
                positionName, PartyConstants.TYPE_POSITION);

        for (PartyEntity partyEntity2 : partyEntitys) {
            // @author Sven
            // 判断这个岗位所在公司和创建流程的用户是不是在用一个公司下面。在同一个公司下面就去找这个岗位的用户并返回
            if (!companyName.equals(this.findCompany(partyEntity2).getName())) {
                continue;
            }

            if (partyEntity2.getChildStructs().size() > 0) {
                logger.info("partyEntity2 : {}", partyEntity2.getId());

                for (PartyStruct partyStruct : this
                        .sortPartyStructs(partyEntity2.getChildStructs())) {
                    PartyEntity child = partyStruct.getChildEntity();

                    // 就把岗位下的人直接附加到userIds里
                    if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
                        userIds.add(child.getRef());
                    }
                }

                break;
            }
        }

        // 如果他所在公司下面没有这个岗位就去找全局的随便一个
        if (userIds.isEmpty()) {
            for (PartyEntity partyEntity2 : partyEntitys) {
                if (partyEntity2.getChildStructs().size() > 0) {
                    logger.info("partyEntity2 : {}", partyEntity2.getId());

                    for (PartyStruct partyStruct : this
                            .sortPartyStructs(partyEntity2.getChildStructs())) {
                        PartyEntity child = partyStruct.getChildEntity();

                        // 就把岗位下的人直接附加到userIds里
                        if (child.getPartyType().getType() == PartyConstants.TYPE_USER) {
                            userIds.add(child.getRef());
                        }
                    }

                    break;
                }
            }
        }

        return userIds;
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
     * 查找距离最近的岗位，包含兄弟部门下的岗位.
     *
     * @param userId String
     * @param positionName String
     * @return List
     */
    public List<String> findUserByNearestPositionName(String userId,
            String positionName) {
        logger.info(
                "findUserByNearestPositionName : userId : {}, positionName : {}",
                userId, positionName);

        PartyEntity partyEntity = this.findUser(userId);

        return this.findPositionUserIds(partyEntity, positionName, true);
    }

    /**
     * 查找这个/部门/岗位/人员所在的公司
     * 
     * @author Sven
     * @param partyEntity PartyEntity
     * @param visitedIds Set
     * @return PartyEntity
     */
    public PartyEntity findCompany(PartyEntity partyEntity, Set<Long> visitedIds) {
        if (visitedIds.contains(partyEntity.getId())) {
            return null;
        }

        visitedIds.add(partyEntity.getId());

        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            PartyEntity parentEntity = partyStruct.getParentEntity();

            if ("公司".equals(parentEntity.getPartyType().getName())) {
                return parentEntity;
            }

            PartyEntity companyEntity = this.findCompany(parentEntity,
                    visitedIds);

            if (companyEntity != null) {
                return companyEntity;
            }
        }

        return null;
    }

    public PartyEntity findCompany(PartyEntity partyEntity) {
        return this.findCompany(partyEntity, new HashSet<Long>());
    }

    /**
     * 根据用户查询用所主职所在的岗位。
     */
    @Override
    public OrgDTO findPositionByUserId(String userId) {
        PartyEntity partyEntity = this.findUser(userId);

        if (partyEntity == null) {
            return null;
        }

        List<PartyStruct> partyStructs = sortPartyStructs(partyEntity
                .getChildStructs());

        if (partyStructs.isEmpty()) {
            return null;
        }

        for (PartyStruct partyStruct : partyStructs) {
            PartyEntity partyEntity2 = partyStruct.getChildEntity();

            if (partyEntity2.getPartyType().getType() != PartyConstants.TYPE_POSITION) {
                continue;
            }

            OrgDTO orgDto = new OrgDTO();
            orgDto.setId(Long.toString(partyEntity2.getId()));
            orgDto.setName(partyEntity2.getName());
            orgDto.setTypeName(partyEntity2.getPartyType().getName());
            orgDto.setType(partyEntity2.getPartyType().getType());
            orgDto.setRef(partyEntity2.getRef());

            return orgDto;
        }

        return null;
    }

    public OrgDTO findCompany(String userCode) {
        PartyEntity partyEntity = this.findUser(userCode);

        if (partyEntity == null) {
            return null;
        }

        Set<Long> visitedIds = new HashSet<Long>();

        PartyEntity company = this.findCompany(partyEntity, visitedIds);

        if (company == null) {
            return null;
        }

        PartyEntity partyEntity2 = company;
        OrgDTO orgDto = new OrgDTO();
        orgDto.setId(Long.toString(partyEntity2.getId()));
        orgDto.setName(partyEntity2.getName());
        orgDto.setTypeName(partyEntity2.getPartyType().getName());
        orgDto.setType(partyEntity2.getPartyType().getType());
        orgDto.setRef(partyEntity2.getRef());

        return orgDto;
    }

    // ~ ==================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
