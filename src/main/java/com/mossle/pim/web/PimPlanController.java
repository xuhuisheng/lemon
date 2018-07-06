package com.mossle.pim.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.pim.persistence.domain.PimPlan;
import com.mossle.pim.persistence.manager.PimPlanManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimPlanController {
    private PimPlanManager pimPlanManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("pim-plan-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimPlanManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "pim/pim-plan-list";
    }

    @RequestMapping("pim-plan-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimPlan pimPlan = pimPlanManager.get(id);
            model.addAttribute("model", pimPlan);
        }

        return "pim/pim-plan-input";
    }

    @RequestMapping("pim-plan-save")
    public String save(@ModelAttribute PimPlan pimPlan,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = pimPlan.getId();
        PimPlan dest = null;

        if (id != null) {
            dest = pimPlanManager.get(id);
            beanMapper.copy(pimPlan, dest);
        } else {
            dest = pimPlan;
            dest.setUserId(userId);
            dest.setCreateTime(new Date());
			dest.setStatus("active");
        }

        pimPlanManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-plan-list.do";
    }

    @RequestMapping("pim-plan-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimPlan> pimPlans = pimPlanManager.findByIds(selectedItem);
        pimPlanManager.removeAll(pimPlans);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-plan-list.do";
    }

    @RequestMapping("pim-plan-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimPlanManager.pagedQuery(page, propertyFilters);

        List<PimPlan> pimPlans = (List<PimPlan>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimPlans);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPimPlanManager(PimPlanManager pimPlanManager) {
        this.pimPlanManager = pimPlanManager;
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
