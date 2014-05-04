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
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        List<PartyStructType> partyStructTypes = partyStructTypeManager
                .getAll();
        List<PartyType> partyTypes = partyTypeManager.getAll();
        model.addAttribute("partyStructTypes", partyStructTypes);
        model.addAttribute("partyTypes", partyTypes);

        if (id != null) {
            PartyStructRule partyStructRule = partyStructRuleManager.get(id);
            model.addAttribute("model", partyStructRule);
        }

        return "party/party-struct-rule-input";
    }

    @RequestMapping("party-struct-rule-save")
    public String save(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("partyStructTypeId") Long partyStructTypeId,
            @RequestParam("parentTypeId") Long parentTypeId,
            @RequestParam("childTypeId") Long childTypeId,
            RedirectAttributes redirectAttributes) {
        if (id != null) {
            PartyStructRule partyStructRule = partyStructRuleManager.get(id);
            partyStructRuleManager.remove(partyStructRule);
        }

        PartyStructRule partyStructRule = new PartyStructRule();
        partyStructRule.setPartyStructType(partyStructTypeManager
                .get(partyStructTypeId));
        partyStructRule.setParentType(partyTypeManager.get(parentTypeId));
        partyStructRule.setChildType(partyTypeManager.get(childTypeId));

        partyStructRuleManager.save(partyStructRule);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-struct-rule-list.do";
    }

    @RequestMapping("party-struct-rule-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        partyStructRuleManager.removeAll(partyStructRuleManager
                .findByIds(selectedItem));
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
}
