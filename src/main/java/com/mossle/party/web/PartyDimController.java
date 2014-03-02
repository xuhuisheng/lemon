package com.mossle.party.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.party.domain.PartyDim;
import com.mossle.party.manager.PartyDimManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class PartyDimController {
    private PartyDimManager partyDimManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("party-dim-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = partyDimManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "party/party-dim-list";
    }

    @RequestMapping("party-dim-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PartyDim partyDim = partyDimManager.get(id);
            model.addAttribute("model", partyDim);
        }

        return "party/party-dim-input";
    }

    @RequestMapping("party-dim-save")
    public String save(@ModelAttribute PartyDim partyDim,
            RedirectAttributes redirectAttributes) {
        PartyDim dest = null;
        Long id = partyDim.getId();

        if (id != null) {
            dest = partyDimManager.get(id);
            beanMapper.copy(partyDim, dest);
        } else {
            dest = partyDim;
        }

        partyDimManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-dim-list.do";
    }

    @RequestMapping("party-dim-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        partyDimManager.removeAll(partyDimManager.findByIds(selectedItem));
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/party-dim-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
