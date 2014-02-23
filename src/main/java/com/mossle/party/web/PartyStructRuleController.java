package com.mossle.party.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.party.domain.PartyStructRule;
import com.mossle.party.domain.PartyStructRuleId;
import com.mossle.party.domain.PartyStructType;
import com.mossle.party.domain.PartyType;
import com.mossle.party.manager.PartyStructRuleManager;
import com.mossle.party.manager.PartyStructTypeManager;
import com.mossle.party.manager.PartyTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class PartyStructRuleController {
    private PartyStructRuleManager partyStructRuleManager;
    private PartyStructTypeManager partyStructTypeManager;
    private PartyTypeManager partyTypeManager;
    private MessageHelper messageHelper;

    @RequestMapping("party-struct-rule-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = partyStructRuleManager.pagedQuery(page, propertyFilters);

        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();
        model.addAttribute("page", page);
        model.addAttribute("partyStructTypes", partyStructTypes);

        return "party/party-struct-rule-list";
    }

    @RequestMapping("party-struct-rule-input")
    public String input(
            @RequestParam(value = "partyStructRuleId", required = false) String partyStructRuleId,
            Model model) {
        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();
        List<PartyType> partyTypes = partyTypeManager.getAll();
        model.addAttribute("partyStructTypes", partyStructTypes);
        model.addAttribute("partyTypes", partyTypes);

        if (partyStructRuleId != null) {
            PartyStructRule partyStructRule = convertPartyStructRule(partyStructRuleId);
            model.addAttribute("model", partyStructRule);
        }

        return "party/party-struct-rule-input";
    }

    @RequestMapping("party-struct-rule-save")
    public String save(
            @RequestParam(value = "partyStructRuleId", required = false) String partyStructRuleId,
            @RequestParam("partyStructTypeId") Long partyStructTypeId,
            @RequestParam("parentTypeId") Long parentTypeId,
            @RequestParam("childTypeId") Long childTypeId,
            RedirectAttributes redirectAttributes) {
        if (partyStructRuleId != null) {
            PartyStructRule partyStructRule = convertPartyStructRule(partyStructRuleId);
            partyStructRuleManager.remove(partyStructRule);
        }

        PartyStructRule partyStructRule = new PartyStructRule();

        PartyStructRuleId thePartyStructRuleId = new PartyStructRuleId(
                partyStructTypeId, parentTypeId, childTypeId);
        partyStructRule.setId(thePartyStructRuleId);
        partyStructRuleManager.save(partyStructRule);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-struct-rule-list.do";
    }

    @RequestMapping("party-struct-rule-remove")
    public String remove(
            @RequestParam("selectedItem") List<String> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PartyStructRuleId> ids = new ArrayList<PartyStructRuleId>();

        for (String id : selectedItem) {
            ids.add(convertPartyStructRuleId(id));
        }

        partyStructRuleManager.removeAll(partyStructRuleManager.findByIds(ids));
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/party-struct-rule-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setPartyStructRuleManager(
            PartyStructRuleManager partyStructRuleManager) {
        this.partyStructRuleManager = partyStructRuleManager;
    }

    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setPartyTypeManager(PartyTypeManager partyTypeManager) {
        this.partyTypeManager = partyTypeManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    protected PartyStructRuleId convertPartyStructRuleId(String id) {
        String[] array = id.split(",");
        Long partyStructTypeId = Long.parseLong(array[0]);
        Long parentTypeId = Long.parseLong(array[1]);
        Long childTypeId = Long.parseLong(array[2]);

        PartyStructRuleId thePartyStructRuleId = new PartyStructRuleId(
                partyStructTypeId, parentTypeId, childTypeId);

        return thePartyStructRuleId;
    }

    protected PartyStructRule convertPartyStructRule(String id) {
        return partyStructRuleManager.get(convertPartyStructRuleId(id));
    }
}
