package com.mossle.party.web.party;

import java.util.List;

import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructTypeManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

public class TreeAction extends BaseAction implements ModelDriven<PartyEntity>,
        Preparable {
    private PartyEntityManager partyEntityManager;
    private PartyStructTypeManager partyStructTypeManager;
    private long partyStructTypeId;
    private List<PartyStructType> partyStructTypes;
    private List<PartyEntity> partyEntities;

    public String execute() {
        partyStructTypes = partyStructTypeManager.getAll();

        String hql = "select distinct o from PartyEntity o left join o.parentStructs p with p.partyStructType.id=? "
                + "join o.childStructs c where p is null and c.partyStructType.id=?";
        partyEntities = partyEntityManager.find(hql, partyStructTypeId,
                partyStructTypeId);

        return SUCCESS;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public PartyEntity getModel() {
        return null;
    }

    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    // ~ ======================================================================
    public void setPartyStructTypeId(long partyStructTypeId) {
        this.partyStructTypeId = partyStructTypeId;
    }

    public List<PartyStructType> getPartyStructTypes() {
        return partyStructTypes;
    }

    public List<PartyEntity> getPartyEntities() {
        return partyEntities;
    }
}
