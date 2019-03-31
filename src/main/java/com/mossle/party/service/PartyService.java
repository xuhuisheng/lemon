package com.mossle.party.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;

import org.apache.commons.lang3.math.NumberUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class PartyService {
    private static Logger logger = LoggerFactory.getLogger(PartyService.class);
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;

    // ~ ======================================================================
    public PartyEntity getEntity(Long id) {
        return partyEntityManager.get(id);
    }

    public PartyEntity getEntity(String name, String partyType) {
        String hql = "from PartyEntity where name=? and partyType.name=?";

        return partyEntityManager.findUnique(hql, name, partyType);
    }

    public List<PartyEntity> findParentEntities(Long id,
            String partyStructType, String partyType) {
        String hql = "select p from PartyEntity o join o.parentStructs s join s.parentEntity p"
                + " where o.id=? and s.partyStructType.name=? and p.partyType.name=?";

        return partyEntityManager.find(hql, id, partyStructType, partyType);
    }

    public List<PartyEntity> findChildEntities(Long id, String partyStructType,
            String partyType) {
        String hql = "select c from PartyEntity o join o.childStructs s join s.childEntity c"
                + " where o.id=? and s.partyStructType.name=? and c.partyType.name=?";

        return partyEntityManager.find(hql, id, partyStructType, partyType);
    }

    public List<PartyEntity> findEntities(String partyType) {
        String hql = "from PartyEntity where partyType.name=?";

        return partyEntityManager.find(hql, partyType);
    }

    public void removeEntity(long id) {
        partyEntityManager.removeById(id);
    }

    // ~ ======================================================================
    public PartyType getType(Long id) {
        return partyEntityManager.get(PartyType.class, id);
    }

    // ~ ======================================================================
    public void save(Object o) {
        partyEntityManager.save(o);
    }

    public void remove(Object o) {
        partyEntityManager.remove(o);
    }

    // ~ ======================================================================
    /**
     * 同步更新PartyEntity，比如company,department,group,position,user里改了什么信息，就同步修改PartyEntity里的信息.
     *
     * @param partyEntityRef String
     * @param partyTypeRef String
     * @param name String
     */
    public void insertPartyEntity(String partyEntityRef, String partyTypeRef,
            String name) {
        PartyEntity partyEntity = new PartyEntity();
        partyEntity.setRef(partyEntityRef);
        partyEntity.setName(name);

        PartyType partyType = partyTypeManager
                .findUniqueBy("ref", partyTypeRef);
        partyEntity.setPartyType(partyType);
        partyEntityManager.save(partyEntity);
    }

    public void updatePartyEntity(String partyEntityRef, String partyTypeRef,
            String name) {
        PartyType partyType = partyTypeManager
                .findUniqueBy("ref", partyTypeRef);
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where ref=? and partyType.id=?",
                partyEntityRef, partyType.getId());
        partyEntity.setName(name);
        partyEntityManager.save(partyEntity);
    }

    public void removePartyEntity(String partyEntityRef, String partyTypeRef) {
        PartyType partyType = partyTypeManager
                .findUniqueBy("ref", partyTypeRef);
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where ref=? and partyType.id=?",
                partyEntityRef, partyType.getId());
        partyEntityManager.remove(partyEntity);
    }

    // ~ ======================================================================
    public Long getDefaultPartyStructTypeId() {
        PartyStructType partyStructType = partyStructTypeManager
                .findUnique("from PartyStructType");

        return partyStructType.getId();
    }

    public String getDefaultRootPartyEntityRef() {
        Long defaultPartyStructTypeId = getDefaultPartyStructTypeId();
        String hql = "select distinct o from PartyEntity o left join o.parentStructs p with p.partyStructType.id=? "
                + "join o.childStructs c where p.parentEntity is null and c.partyStructType.id=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                defaultPartyStructTypeId, defaultPartyStructTypeId);

        return partyEntity.getRef();
    }

    public Long getDefaultRootPartyEntityId() {
        Long defaultPartyStructTypeId = getDefaultPartyStructTypeId();
        String hql = "select distinct o from PartyEntity o left join o.parentStructs p with p.partyStructType.id=? "
                + "join o.childStructs c where p.parentEntity is null and c.partyStructType.id=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                defaultPartyStructTypeId, defaultPartyStructTypeId);

        if (partyEntity == null) {
            return null;
        }

        return partyEntity.getId();
    }

    public List<PartyEntity> getTopPartyEntities(Long partyStructTypeId) {
        return this.getTopPartyEntities(partyStructTypeId, false);
    }

    public List<PartyEntity> getTopPartyEntities(Long partyStructTypeId,
            boolean onlyDepartment) {
        String hql = "select ps.childEntity from PartyStruct ps where ps.parentEntity is null and ps.partyStructType.id=?";
        List<PartyEntity> partyEntities = partyEntityManager.find(hql,
                NumberUtils.toLong(partyStructTypeId + ""));

        return partyEntities;
    }

    /**
     * 如果对应的child实体，只有当前partyStruct一个上级关系，就需要级联删除所有的下级关系，避免形成孤岛.
     *
     * @param partyStruct PartyStruct
     */
    public void removeOrphan(PartyStruct partyStruct) {
        if (!"struct".equals(partyStruct.getPartyStructType().getType())) {
            return;
        }

        logger.info("remove orphan : {} {} {}", partyStruct.getParentEntity()
                .getName(), partyStruct.getChildEntity().getName(), partyStruct
                .getId());

        boolean isOrphan = true;

        for (PartyStruct parentPartyStruct : partyStruct.getChildEntity()
                .getParentStructs()) {
            if (!parentPartyStruct.getId().equals(partyStruct.getId())) {
                isOrphan = false;

                break;
            }
        }

        if (isOrphan) {
            for (PartyStruct childPartyStruct : partyStruct.getChildEntity()
                    .getChildStructs()) {
                this.removeOrphan(childPartyStruct);
            }
        }

        partyStructManager.remove(partyStruct);
        logger.info("DELETE FROM PARTY_STRUCT WHERE ID=" + partyStruct.getId());
    }

    public List<PartyEntity> findOrphanPartyEntities() {
        String hql = "select pe from PartyEntity pe left join pe.parentStructs ps where ps is null";
        List<PartyEntity> partyEntities = this.partyEntityManager.find(hql);

        return partyEntities;
    }

    public void removeOrphansByPartyEntities() {
        List<PartyEntity> partyEntities = this.findOrphanPartyEntities();

        for (PartyEntity partyEntity : partyEntities) {
            for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
                this.removeOrphan(partyStruct);
            }
        }
    }

    // report line
    public List<PartyEntity> findReportLines(Long partyEntityId) {
        List<PartyEntity> list = new ArrayList<PartyEntity>();
        PartyEntity partyEntity = partyEntityManager.get(partyEntityId);

        if (partyEntity == null) {
            logger.info("cannot find : {}", partyEntityId);

            return Collections.emptyList();
        }

        list.add(partyEntity);
        this.visitReportLine(partyEntity, list);

        return list;
    }

    public void visitReportLine(PartyEntity partyEntity, List<PartyEntity> list) {
        for (PartyStruct partyStruct : partyEntity.getParentStructs()) {
            if (!"report".equals(partyStruct.getPartyStructType().getType())) {
                continue;
            }

            PartyEntity parent = partyStruct.getParentEntity();

            if (parent == null) {
                continue;
            }

            list.add(parent);
            this.visitReportLine(parent, list);
        }
    }

    // ~ ======================================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }
}
