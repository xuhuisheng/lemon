package com.mossle.party.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyStructTypeManager;
import com.mossle.party.persistence.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * party test.
 */
@Controller
@RequestMapping("party")
public class PartyTestController {
    private static Logger logger = LoggerFactory
            .getLogger(PartyTestController.class);
    private OrgConnector orgConnector;
    private PartyEntityManager partyEntityManager;

    @ResponseBody
    @RequestMapping("party-test-superiour")
    public String testSuperiour(@RequestParam("username") String username) {
        logger.info("test superiour : {}", username);

        PartyEntity partyEntity = partyEntityManager.findUniqueBy("name",
                username);

        if (partyEntity == null) {
            logger.info("cannot find user : {}", username);

            return "cannot find user " + username;
        }

        String superiourId = orgConnector.getSuperiorId(partyEntity.getRef());

        if (superiourId == null) {
            logger.info("cannot find superiour : {}", username);

            return "cannot find superiour " + username;
        }

        partyEntity = partyEntityManager.findUniqueBy("ref", superiourId);

        if (partyEntity == null) {
            logger.info("cannot find partyEntity : {}", superiourId);

            return "cannot find partyEntity " + superiourId;
        }

        return partyEntity.getName();
    }

    @Resource
    public void setOrgConnector(OrgConnector orgConnector) {
        this.orgConnector = orgConnector;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }
}
