package com.mossle.party.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructTypeManager;
import com.mossle.party.service.PartyService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class TreeController {
    private PartyEntityManager partyEntityManager;
    private PartyStructTypeManager partyStructTypeManager;
    private PartyService partyService;

    @RequestMapping("tree-list")
    public String list(
            @RequestParam(value = "partyStructTypeId", required = false) Long partyStructTypeId,
            Model model) {
        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();

        List<PartyEntity> partyEntities = partyService
                .getTopPartyEntities(partyStructTypeId);
        model.addAttribute("partyStructTypes", partyStructTypes);
        model.addAttribute("partyEntities", partyEntities);

        return "party/tree-list";
    }

    // ~ ======================================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }
}
