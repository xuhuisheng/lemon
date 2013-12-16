package com.mossle.party.web.party;

import java.util.List;

import com.mossle.core.struts2.BaseAction;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.manager.PartyDimManager;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructTypeManager;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

public class TreeAction extends BaseAction implements ModelDriven<PartyEntity>,
        Preparable {
    private PartyEntityManager partyEntityManager;
    private PartyStructTypeManager partyStructTypeManager;
    private PartyDimManager partyDimManager;
    private long partyStructTypeId;
    private List<PartyStructType> partyStructTypes;
    private List<PartyEntity> partyEntities;
    private List<PartyDim> partyDims;
    private long partyDimId;

    public String execute() {
        partyDims = partyDimManager.getAll();

        String hql = "select pdr.partyEntity from PartyDimRoot pdr where pdr.partyDim.id=?";
        partyEntities = partyEntityManager.find(hql, partyDimId);

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

    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
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

    public List<PartyDim> getPartyDims() {
        return partyDims;
    }

    public void setPartyDimId(long partyDimId) {
        this.partyDimId = partyDimId;
    }
}
