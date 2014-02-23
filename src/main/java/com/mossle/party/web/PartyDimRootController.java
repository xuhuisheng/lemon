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
import com.mossle.party.domain.PartyDimRoot;
import com.mossle.party.domain.PartyEntity;
import com.mossle.party.manager.PartyDimManager;
import com.mossle.party.manager.PartyDimRootManager;
import com.mossle.party.manager.PartyEntityManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class PartyDimRootController {
    private PartyDimRootManager partyDimRootManager;
    private PartyDimManager partyDimManager;
    private PartyEntityManager partyEntityManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("party-dim-root-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = partyDimRootManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "party/party-dim-root-list";
    }

    @RequestMapping("party-dim-root-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PartyDimRoot partyDimRoot = partyDimRootManager.get(id);
            model.addAttribute("model", partyDimRoot);
        }

        List<PartyEntity> partyEntities = partyEntityManager.getAll();
        List<PartyDim> partyDims = partyDimManager.getAll();
        model.addAttribute("partyEntities", partyEntities);
        model.addAttribute("partyDims", partyDims);

        return "party/party-dim-root-input";
    }

    @RequestMapping("party-dim-root-save")
    public String save(@ModelAttribute PartyDimRoot partyDimRoot,
            @RequestParam("partyEntityId") Long partyEntityId,
            @RequestParam("partyDimId") Long partyDimId,
            RedirectAttributes redirectAttributes) {
        PartyDimRoot dest = null;
        Long id = partyDimRoot.getId();

        if (id != null) {
            dest = partyDimRootManager.get(id);
            beanMapper.copy(partyDimRoot, dest);
        } else {
            dest = partyDimRoot;
        }

        dest.setPartyEntity(partyEntityManager.get(partyEntityId));
        dest.setPartyDim(partyDimManager.get(partyDimId));
        partyDimRootManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-dim-root-list.do";
    }

    @RequestMapping("party-dim-root-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        partyDimRootManager.removeAll(partyDimRootManager
                .findByIds(selectedItem));
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/party-dim-root-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setPartyDimRootManager(PartyDimRootManager partyDimRootManager) {
        this.partyDimRootManager = partyDimRootManager;
    }

    @Resource
    public void setPartyDimManager(PartyDimManager partyDimManager) {
        this.partyDimManager = partyDimManager;
    }

    @Resource
    public void setPartyEntityManager(PartyEntityManager partyEntityManager) {
        this.partyEntityManager = partyEntityManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
