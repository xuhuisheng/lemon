package com.mossle.party.service;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;

import org.springframework.stereotype.Component;

@Component
public class PartyService {
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
                + "join o.childStructs c where p is null and c.partyStructType.id=?";
        PartyEntity partyEntity = partyEntityManager.findUnique(hql,
                defaultPartyStructTypeId, defaultPartyStructTypeId);

        return partyEntity.getRef();
    }

    public List<PartyEntity> getTopPartyEntities(Long partyStructTypeId) {
        String hql = "select ps.childEntity from PartyStruct ps where ps.parentEntity is null and ps.partyStructType.id=?";

        return partyEntityManager.find(hql, partyStructTypeId);
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
