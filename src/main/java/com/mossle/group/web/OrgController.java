package com.mossle.group.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.page.Page;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyDimManager;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyStructTypeManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.manager.PartyTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class OrgController {
    private PartyDimManager partyDimManager;
    private PartyEntityManager partyEntityManager;
    private PartyTypeManager partyTypeManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private UserConnector userConnector;

    public void init(Model model, Long partyDimId, Long partyEntityId) {
        List<PartyDim> partyDims = partyDimManager.getAll("priority", true);
        PartyDim partyDim = null;

        if (partyDimId == null) {
            partyDim = partyDims.get(0);
            partyDimId = partyDim.getId();
        } else {
            partyDim = partyDimManager.get(partyDimId);
        }

        if (partyEntityId == null) {
            partyEntityId = partyDim.getPartyDimRoots().iterator().next()
                    .getPartyEntity().getId();
        }

        model.addAttribute("partyDim", partyDim);
        model.addAttribute("partyDimId", partyDimId);
        model.addAttribute("partyEntityId", partyEntityId);
    }

    @RequestMapping("org-users")
    public String users(
            Model model,
            @RequestParam(value = "partyDimId", required = false) Long partyDimId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            @ModelAttribute Page page) {
        init(model, partyDimId, partyEntityId);

        String hql = "from PartyStruct where childEntity.partyType.person=1 and parentEntity.id=?";
        page = partyDimManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId);
        model.addAttribute("page", page);

        return "group/org-users";
    }

    @RequestMapping("org-inputUser")
    public String inputUser(
            Model model,
            @RequestParam(value = "partyDimId", required = false) Long partyDimId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        init(model, partyDimId, partyEntityId);

        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();

        model.addAttribute("partyStructTypes", partyStructTypes);

        return "group/org-inputUser";
    }

    @RequestMapping("org-saveUser")
    public String saveUser(@RequestParam("name") String name,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId,
            @RequestParam("partyDimId") Long partyDimId,
            @RequestParam("status") int status) throws Exception {
        UserDTO userDto = userConnector.findByUsername(name,
                ScopeHolder.getUserRepoRef());
        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where partyType.person=1 and ref=?",
                userDto.getId());
        PartyEntity parent = partyEntityManager.findUnique(
                "from PartyEntity where partyType.person<>1 and ref=?",
                Long.toString(partyEntityId));

        PartyStruct partyStruct = new PartyStruct();
        PartyStructId partyStructId = new PartyStructId(partyStructTypeId,
                parent.getId(), child.getId());
        partyStruct.setId(partyStructId);
        partyStruct.setPartyDim(partyDimManager.get(partyDimId));
        partyStruct.setStatus(status);
        partyStructManager.save(partyStruct);

        return "redirect:/group/org-users.do?partyDimId=" + partyDimId
                + "&partyEntityId=" + partyEntityId;
    }

    @RequestMapping("org-removeUser")
    public String removeUser(
            @RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyDimId") Long partyDimId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId) {
        for (Long childId : selectedItem) {
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person<>1 and id=?",
                    partyEntityId);
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person=1 and id=?",
                    childId);

            PartyStructId partyStructId = new PartyStructId(partyStructTypeId,
                    parent.getId(), child.getId());

            PartyStruct partyStruct = partyStructManager.get(partyStructId);
            partyStructManager.remove(partyStruct);
        }

        // addActionMessage(messages.getMessage("core.success.delete", "删除成功"));
        return "redirect:/group/org-users.do?partyDimId=" + partyDimId
                + "&partyEntityId=" + partyEntityId;
    }

    // ~ ==================================================
    @RequestMapping("org-children")
    public String children(
            Model model,
            @RequestParam(value = "partyDimId", required = false) Long partyDimId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId,
            @ModelAttribute Page page) throws Exception {
        init(model, partyDimId, partyEntityId);

        String hql = "select child from PartyEntity child join child.parentStructs ps join ps.parentEntity parent"
                + " where child.partyType.person<>1 and parent.id=?";
        page = partyEntityManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), partyEntityId);
        model.addAttribute("page", page);

        return "group/org-children";
    }

    @RequestMapping("org-inputChild")
    public String inputChild(
            Model model,
            @RequestParam(value = "partyDimId", required = false) Long partyDimId,
            @RequestParam(value = "partyEntityId", required = false) Long partyEntityId)
            throws Exception {
        init(model, partyDimId, partyEntityId);

        List<PartyType> partyTypes = partyTypeManager
                .find("from PartyType where person<>1");

        model.addAttribute("partyTypes", partyTypes);

        return "group/org-inputChild";
    }

    @RequestMapping("org-saveChild")
    public String saveChild(@RequestParam("name") String name,
            @RequestParam("partyTypeId") Long partyTypeId,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyDimId") Long partyDimId) {
        PartyEntity child = partyEntityManager.findUnique(
                "from PartyEntity where name=? and partyType.id=?", name,
                partyTypeId);
        PartyEntity parent = partyEntityManager.findUnique(
                "from PartyEntity where partyType.person<>1 and ref=?",
                Long.toString(partyEntityId));
        Assert.notNull(child, name + "(" + partyTypeId + ") is null");
        Assert.notNull(parent, partyEntityId + " is null");

        PartyStruct partyStruct = new PartyStruct();
        PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                child.getId());
        partyStruct.setId(partyStructId);
        partyStruct.setPartyDim(partyDimManager.get(partyDimId));
        partyStructManager.save(partyStruct);

        return "redirect:/group/org-children.do?partyDimId=" + partyDimId
                + "&partyEntityId=" + partyEntityId;
    }

    @RequestMapping("org-removeChild")
    public String removeChild(
            @RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyDimId") Long partyDimId) {
        for (Long childId : selectedItem) {
            PartyEntity parent = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person<>1 and id=?",
                    partyEntityId);
            PartyEntity child = partyEntityManager.findUnique(
                    "from PartyEntity where partyType.person<>1 and id=?",
                    childId);

            PartyStructId partyStructId = new PartyStructId(1L, parent.getId(),
                    child.getId());
            PartyStruct partyStruct = partyStructManager.get(partyStructId);
            partyStructManager.remove(partyStruct);
        }

        return "redirect:/group/org-children.do?partyDimId=" + partyDimId
                + "&partyEntityId=" + partyEntityId;
    }

    // ~ ==================================================
    @Resource
    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setPartyStructManager(PartyStructManager partyStructManager) {
        this.partyStructManager = partyStructManager;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
