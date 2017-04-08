package com.mossle.party.support;

import javax.annotation.Resource;

import com.mossle.api.party.PartyConnector;
import com.mossle.api.party.PartyDTO;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.manager.PartyEntityManager;

public class PartyConnectorImpl implements PartyConnector {
    private PartyEntityManager partyEntityManager;

    public PartyDTO findById(String partyId) {
        Long id = Long.parseLong(partyId);
        PartyEntity partyEntity = partyEntityManager.get(id);
        PartyDTO partyDto = new PartyDTO();
        partyDto.setId(partyId);
        partyDto.setName(partyEntity.getName());

        return partyDto;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
