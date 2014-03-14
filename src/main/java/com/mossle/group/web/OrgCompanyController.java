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

import com.mossle.group.domain.OrgCompany;
import com.mossle.group.manager.OrgCompanyManager;

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
public class OrgCompanyController {
    private OrgCompanyManager orgCompanyManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private PartyService partyService;

    @RequestMapping("org-company-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = orgCompanyManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "group/org-company-list";
    }

    @RequestMapping("org-company-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OrgCompany orgCompany = orgCompanyManager.get(id);
            model.addAttribute("model", orgCompany);
        }

        return "group/org-company-input";
    }

    @RequestMapping("org-company-save")
    public String save(@ModelAttribute OrgCompany orgCompany,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        OrgCompany dest = null;
        Long id = orgCompany.getId();

        if (id != null) {
            dest = orgCompanyManager.get(id);
            beanMapper.copy(orgCompany, dest);
        } else {
            dest = orgCompany;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        orgCompanyManager.save(dest);

        if (id == null) {
            // TODO: sync party
            partyService.insertPartyEntity(Long.toString(dest.getId()),
                    "company", dest.getName());
        } else {
            // TODO: sync party
            partyService.updatePartyEntity(Long.toString(dest.getId()),
                    "company", dest.getName());
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/org-company-list.do";
    }

    @RequestMapping("org-company-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OrgCompany> orgCompanies = orgCompanyManager
                .findByIds(selectedItem);

        for (OrgCompany orgCompany : orgCompanies) {
            orgCompanyManager.remove(orgCompany);
            partyService.removePartyEntity(Long.toString(orgCompany.getId()),
                    "company");
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/org-company-list.do";
    }

    @RequestMapping("org-company-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = orgCompanyManager.pagedQuery(page, propertyFilters);

        List<OrgCompany> orgCompanies = (List<OrgCompany>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name", "status", "description");
        tableModel.setData(orgCompanies);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOrgCompanyManager(OrgCompanyManager orgCompanyManager) {
        this.orgCompanyManager = orgCompanyManager;
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
