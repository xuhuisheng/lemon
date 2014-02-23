package com.mossle.party.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.domain.PartyStructId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
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
public class PartyStructController {
    private PartyEntityManager partyEntityManager;
    private PartyStructManager partyStructManager;
    private PartyStructTypeManager partyStructTypeManager;
    private MessageHelper messageHelper;

    @RequestMapping("party-struct-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = partyStructManager.pagedQuery(page, propertyFilters);

        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();
        model.addAttribute("page", page);
        model.addAttribute("partyStructTypes", partyStructTypes);

        return "party/party-struct-list";
    }

    @RequestMapping("party-struct-input")
    public String input(
            @RequestParam(value = "partyStructId", required = false) String partyStructId,
            Model model) {
        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();
        List<PartyEntity> partyEntities = partyEntityManager.getAll();
        model.addAttribute("partyStructTypes", partyStructTypes);
        model.addAttribute("partyEntities", partyEntities);

        if (partyStructId != null) {
            PartyStruct partyStruct = convertPartyStruct(partyStructId);
            model.addAttribute("model", partyStruct);
        }

        return "party/party-struct-input";
    }

    @RequestMapping("party-struct-save")
    public String save(
            @RequestParam(value = "partyStructId", required = false) String partyStructId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId,
            @RequestParam("parentEntityId") Long parentEntityId,
            @RequestParam("childEntityId") Long childEntityId,
            RedirectAttributes redirectAttributes) {
        if (partyStructId != null) {
            PartyStruct partyStruct = convertPartyStruct(partyStructId);
            partyStructManager.remove(partyStruct);
        }

        PartyStructId thePartyStructId = new PartyStructId(partyStructTypeId,
                parentEntityId, childEntityId);
        PartyStruct partyStruct = new PartyStruct();
        partyStruct.setId(thePartyStructId);
        partyStructManager.save(partyStruct);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-struct-list.do";
    }

    @RequestMapping("party-struct-remove")
    public String remove(
            @RequestParam("selectedItem") List<String> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PartyStructId> ids = new ArrayList<PartyStructId>();

        for (String id : selectedItem) {
            ids.add(convertPartyStructId(id));
        }

        partyStructManager.removeAll(partyStructManager.findByIds(ids));
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/party-struct-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
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

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    // ~ ======================================================================
    protected PartyStructId convertPartyStructId(String id) {
        String[] array = id.split(",");
        Long partyStructTypeId = Long.parseLong(array[0]);
        Long parentEntityId = Long.parseLong(array[1]);
        Long childEntityId = Long.parseLong(array[2]);

        PartyStructId thePartyStructId = new PartyStructId(partyStructTypeId,
                parentEntityId, childEntityId);

        return thePartyStructId;
    }

    protected PartyStruct convertPartyStruct(String id) {
        return partyStructManager.get(convertPartyStructId(id));
    }
}
