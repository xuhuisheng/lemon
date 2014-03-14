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

import com.mossle.group.domain.OrgPositionType;
import com.mossle.group.manager.OrgPositionTypeManager;

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
public class OrgPositionTypeController {
    private OrgPositionTypeManager orgPositionTypeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("org-position-type-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = orgPositionTypeManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/org-position-type-list";
    }

    @RequestMapping("org-position-type-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OrgPositionType orgPositionType = orgPositionTypeManager.get(id);
            model.addAttribute("model", orgPositionType);
        }

        return "group/org-position-type-input";
    }

    @RequestMapping("org-position-type-save")
    public String save(@ModelAttribute OrgPositionType orgPositionType,
            RedirectAttributes redirectAttributes) {
        OrgPositionType dest = null;
        Long id = orgPositionType.getId();

        if (id != null) {
            dest = orgPositionTypeManager.get(id);
            beanMapper.copy(orgPositionType, dest);
        } else {
            dest = orgPositionType;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        orgPositionTypeManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/org-position-type-list.do";
    }

    @RequestMapping("org-position-type-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OrgPositionType> orgCompanies = orgPositionTypeManager
                .findByIds(selectedItem);

        for (OrgPositionType orgPositionType : orgCompanies) {
            orgPositionTypeManager.remove(orgPositionType);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/org-position-type-list.do";
    }

    @RequestMapping("org-position-type-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = orgPositionTypeManager.pagedQuery(page, propertyFilters);

        List<OrgPositionType> orgPositionTypes = (List<OrgPositionType>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name", "status", "description");
        tableModel.setData(orgPositionTypes);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOrgPositionTypeManager(
            OrgPositionTypeManager orgPositionTypeManager) {
        this.orgPositionTypeManager = orgPositionTypeManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
