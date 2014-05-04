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

import com.mossle.group.domain.OrgPosition;
import com.mossle.group.manager.OrgPositionManager;

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
public class OrgPositionController {
    private OrgPositionManager orgPositionManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private PartyService partyService;

    @RequestMapping("org-position-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = orgPositionManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/org-position-list";
    }

    @RequestMapping("org-position-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OrgPosition orgPosition = orgPositionManager.get(id);
            model.addAttribute("model", orgPosition);
        }

        return "group/org-position-input";
    }

    @RequestMapping("org-position-save")
    public String save(@ModelAttribute OrgPosition orgPosition,
            RedirectAttributes redirectAttributes) {
        OrgPosition dest = null;
        Long id = orgPosition.getId();

        if (id != null) {
            dest = orgPositionManager.get(id);
            beanMapper.copy(orgPosition, dest);
        } else {
            dest = orgPosition;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        orgPositionManager.save(dest);

        if (id == null) {
            // sync party
            partyService.insertPartyEntity(Long.toString(dest.getId()),
                    "position", dest.getName());
        } else {
            // sync party
            partyService.updatePartyEntity(Long.toString(dest.getId()),
                    "position", dest.getName());
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/org-position-list.do";
    }

    @RequestMapping("org-position-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OrgPosition> orgPositions = orgPositionManager
                .findByIds(selectedItem);

        for (OrgPosition orgPosition : orgPositions) {
            orgPositionManager.remove(orgPosition);
            partyService.removePartyEntity(Long.toString(orgPosition.getId()),
                    "position");
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/org-position-list.do";
    }

    @RequestMapping("org-position-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = orgPositionManager.pagedQuery(page, propertyFilters);

        List<OrgPosition> orgPositions = (List<OrgPosition>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name", "status", "description");
        tableModel.setData(orgPositions);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOrgPositionManager(OrgPositionManager orgPositionManager) {
        this.orgPositionManager = orgPositionManager;
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
