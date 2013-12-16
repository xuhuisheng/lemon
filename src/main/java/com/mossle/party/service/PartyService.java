package com.mossle.party.service;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyStructTypeManager;
import com.mossle.party.manager.PartyTypeManager;

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
    public void insertPartyEntity(String ref, Long partyTypeId, String name) {
        PartyEntity partyEntity = new PartyEntity();
        partyEntity.setRef(ref);
        partyEntity.setName(name);

        PartyType partyType = partyTypeManager.get(partyTypeId);
        partyEntity.setPartyType(partyType);
        partyEntityManager.save(partyEntity);
    }

    public void updatePartyEntity(String ref, Long partyTypeId, String name) {
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where ref=? and partyTypeId=?", ref,
                partyTypeId);
        partyEntity.setName(name);
        partyEntityManager.save(partyEntity);
    }

    public void removePartyEntity(String ref, Long partyTypeId) {
        PartyEntity partyEntity = partyEntityManager.findUnique(
                "from PartyEntity where ref=? and partyTypeId=?", ref,
                partyTypeId);
        partyEntityManager.remove(partyEntity);
    }

    public void insertPartyStruct(Long partyStructTypeId,
            Long parentPartyEntityId, Long childPartyEntityId) {
        PartyStructId partyStructId = new PartyStructId(partyStructTypeId,
                parentPartyEntityId, childPartyEntityId);
        PartyStruct partyStruct = new PartyStruct();
        partyStruct.setId(partyStructId);
        partyStructManager.save(partyStruct);
    }

    public void removePartyStruct(Long partyStructTypeId,
            Long parentPartyEntityId, Long childPartyEntityId) {
        PartyStructId partyStructId = new PartyStructId(partyStructTypeId,
                parentPartyEntityId, childPartyEntityId);
        partyStructManager.removeById(partyStructId);
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
