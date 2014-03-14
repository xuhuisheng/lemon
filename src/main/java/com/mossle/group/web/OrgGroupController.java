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

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.group.domain.OrgGroup;
import com.mossle.group.manager.OrgGroupManager;

import com.mossle.party.domain.PartyEntity;
import com.mossle.party.domain.PartyStruct;
import com.mossle.party.manager.PartyEntityManager;
import com.mossle.party.manager.PartyStructManager;
import com.mossle.party.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class OrgGroupController {
    private OrgGroupManager orgGroupManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private PartyService partyService;

    @RequestMapping("org-group-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = orgGroupManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/org-group-list";
    }

    @RequestMapping("org-group-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OrgGroup orgGroup = orgGroupManager.get(id);
            model.addAttribute("model", orgGroup);
        }

        return "group/org-group-input";
    }

    @RequestMapping("org-group-save")
    public String save(@ModelAttribute OrgGroup orgGroup,
            RedirectAttributes redirectAttributes) {
        OrgGroup dest = null;
        Long id = orgGroup.getId();

        if (id != null) {
            dest = orgGroupManager.get(id);
            beanMapper.copy(orgGroup, dest);
        } else {
            dest = orgGroup;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        orgGroupManager.save(dest);

        if (id == null) {
            // sync party
            partyService.insertPartyEntity(Long.toString(dest.getId()),
                    "group", dest.getName());
        } else {
            // sync party
            partyService.updatePartyEntity(Long.toString(dest.getId()),
                    "group", dest.getName());
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/org-group-list.do";
    }

    @RequestMapping("org-group-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OrgGroup> orgGroups = orgGroupManager.findByIds(selectedItem);

        for (OrgGroup orgGroup : orgGroups) {
            orgGroupManager.remove(orgGroup);
            partyService.removePartyEntity(Long.toString(orgGroup.getId()),
                    "group");
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/org-group-list.do";
    }

    @RequestMapping("org-group-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = orgGroupManager.pagedQuery(page, propertyFilters);

        List<OrgGroup> orgGroups = (List<OrgGroup>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name", "status", "description");
        tableModel.setData(orgGroups);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOrgGroupManager(OrgGroupManager orgGroupManager) {
        this.orgGroupManager = orgGroupManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }
}
