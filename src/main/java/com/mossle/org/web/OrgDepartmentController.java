package com.mossle.org.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.org.persistence.domain.OrgDepartment;
import com.mossle.org.persistence.manager.OrgDepartmentManager;

import com.mossle.party.service.PartyService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("org")
public class OrgDepartmentController {
    private OrgDepartmentManager orgDepartmentManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private PartyService partyService;
    private TenantHolder tenantHolder;

    @RequestMapping("org-department-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = orgDepartmentManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "org/org-department-list";
    }

    @RequestMapping("org-department-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OrgDepartment orgDepartment = orgDepartmentManager.get(id);
            model.addAttribute("model", orgDepartment);
        }

        return "org/org-department-input";
    }

    @RequestMapping("org-department-save")
    public String save(@ModelAttribute OrgDepartment orgDepartment,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        OrgDepartment dest = null;
        Long id = orgDepartment.getId();

        if (id != null) {
            dest = orgDepartmentManager.get(id);
            beanMapper.copy(orgDepartment, dest);
        } else {
            dest = orgDepartment;
            dest.setTenantId(tenantId);
        }

        orgDepartmentManager.save(dest);

        if (id == null) {
            // sync party
            partyService.insertPartyEntity(Long.toString(dest.getId()),
                    "department", dest.getName());
        } else {
            // sync party
            partyService.updatePartyEntity(Long.toString(dest.getId()),
                    "department", dest.getName());
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/org/org-department-list.do";
    }

    @RequestMapping("org-department-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OrgDepartment> orgDepartments = orgDepartmentManager
                .findByIds(selectedItem);

        for (OrgDepartment orgDepartment : orgDepartments) {
            orgDepartmentManager.remove(orgDepartment);
            partyService.removePartyEntity(
                    Long.toString(orgDepartment.getId()), "department");
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/org/org-department-list.do";
    }

    @RequestMapping("org-department-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = orgDepartmentManager.pagedQuery(page, propertyFilters);

        List<OrgDepartment> orgdepartments = (List<OrgDepartment>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name", "status", "description");
        tableModel.setData(orgdepartments);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOrgDepartmentManager(
            OrgDepartmentManager orgDepartmentManager) {
        this.orgDepartmentManager = orgDepartmentManager;
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

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
