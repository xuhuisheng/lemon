package com.mossle.party.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.manager.PartyDimManager;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructTypeManager;

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
    private PartyDimManager partyDimManager;

    @RequestMapping("tree-list")
    public String list(
            @RequestParam(value = "partyDimId", required = false) Long partyDimId,
            Model model) {
        List<PartyDim> partyDims = partyDimManager.getAll();

        String hql = "select pdr.partyEntity from PartyDimRoot pdr where pdr.partyDim.id=?";
        List<PartyEntity> partyEntities = partyEntityManager.find(hql,
                partyDimId);
        model.addAttribute("partyDims", partyDims);
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
    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
    }
}
