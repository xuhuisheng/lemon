package com.mossle.party.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.party.persistence.domain.PartyStructType;
import com.mossle.party.persistence.manager.PartyStructTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("party")
public class PartyStructTypeController {
    private PartyStructTypeManager partyStructTypeManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("party-struct-type-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = partyStructTypeManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "party/party-struct-type-list";
    }

    @RequestMapping("party-struct-type-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PartyStructType partyStructType = partyStructTypeManager.get(id);
            model.addAttribute("model", partyStructType);
        }

        return "party/party-struct-type-input";
    }

    @RequestMapping("party-struct-type-save")
    public String save(@ModelAttribute PartyStructType partyStructType,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        PartyStructType dest = null;
        Long id = partyStructType.getId();

        if (id != null) {
            dest = partyStructTypeManager.get(id);
            beanMapper.copy(partyStructType, dest);
        } else {
            dest = partyStructType;
            dest.setTenantId(tenantId);
        }

        partyStructTypeManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/party/party-struct-type-list.do";
    }

    @RequestMapping("party-struct-type-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        partyStructTypeManager.removeAll(partyStructTypeManager
                .findByIds(selectedItem));
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/party/party-struct-type-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setPartyStructTypeManager(
            PartyStructTypeManager partyStructTypeManager) {
        this.partyStructTypeManager = partyStructTypeManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
