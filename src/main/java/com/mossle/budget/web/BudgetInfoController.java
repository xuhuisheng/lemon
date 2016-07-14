package com.mossle.budget.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.budget.persistence.domain.BudgetInfo;
import com.mossle.budget.persistence.manager.BudgetInfoManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("budget")
public class BudgetInfoController {
    private BudgetInfoManager budgetInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("budget-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = budgetInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "budget/budget-info-list";
    }

    @RequestMapping("budget-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BudgetInfo budgetInfo = budgetInfoManager.get(id);
            model.addAttribute("model", budgetInfo);
        }

        return "budget/budget-info-input";
    }

    @RequestMapping("budget-info-save")
    public String save(@ModelAttribute BudgetInfo budgetInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        BudgetInfo dest = null;

        Long id = budgetInfo.getId();

        if (id != null) {
            dest = budgetInfoManager.get(id);
            beanMapper.copy(budgetInfo, dest);
        } else {
            dest = budgetInfo;
            dest.setTenantId(tenantId);
        }

        budgetInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/budget/budget-info-list.do";
    }

    @RequestMapping("budget-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<BudgetInfo> budgetInfos = budgetInfoManager
                .findByIds(selectedItem);

        budgetInfoManager.removeAll(budgetInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/budget/budget-info-list.do";
    }

    @RequestMapping("budget-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = budgetInfoManager.pagedQuery(page, propertyFilters);

        List<BudgetInfo> budgetInfos = (List<BudgetInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("budget info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(budgetInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setBudgetInfoManager(BudgetInfoManager budgetInfoManager) {
        this.budgetInfoManager = budgetInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
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
