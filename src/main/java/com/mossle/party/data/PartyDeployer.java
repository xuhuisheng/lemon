package com.mossle.party.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.party.PartyConstants;
import com.mossle.party.persistence.domain.PartyStructRule;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyStructRuleManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyDeployer {
    private static Logger logger = LoggerFactory.getLogger(PartyDeployer.class);
    private PartyRuleDeployer partyRuleDeployer;
    private PartyDataDeployer partyDataDeployer;

    @PostConstruct
    public void init() throws Exception {
        this.partyRuleDeployer.init();
        this.partyDataDeployer.init();
    }

    @Resource
    public void setPartyRuleDeployer(PartyRuleDeployer partyRuleDeployer) {
        this.partyRuleDeployer = partyRuleDeployer;
    }

    @Resource
    public void setPartyDataDeployer(PartyDataDeployer partyDataDeployer) {
        this.partyDataDeployer = partyDataDeployer;
    }
}
