package com.mossle.expense.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.expense.persistence.domain.ExpenseInfo;
import com.mossle.expense.persistence.manager.ExpenseInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("expense")
public class ExpenseInfoController {
    private ExpenseInfoManager expenseInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("expense-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = expenseInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "expense/expense-info-list";
    }

    @RequestMapping("expense-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ExpenseInfo expenseInfo = expenseInfoManager.get(id);
            model.addAttribute("model", expenseInfo);
        }

        return "expense/expense-info-input";
    }

    @RequestMapping("expense-info-save")
    public String save(@ModelAttribute ExpenseInfo expenseInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        ExpenseInfo dest = null;

        Long id = expenseInfo.getId();

        if (id != null) {
            dest = expenseInfoManager.get(id);
            beanMapper.copy(expenseInfo, dest);
        } else {
            dest = expenseInfo;
            dest.setTenantId(tenantId);
        }

        expenseInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/expense/expense-info-list.do";
    }

    @RequestMapping("expense-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ExpenseInfo> expenseInfos = expenseInfoManager
                .findByIds(selectedItem);

        expenseInfoManager.removeAll(expenseInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/expense/expense-info-list.do";
    }

    @RequestMapping("expense-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = expenseInfoManager.pagedQuery(page, propertyFilters);

        List<ExpenseInfo> expenseInfos = (List<ExpenseInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("expense info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(expenseInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setExpenseInfoManager(ExpenseInfoManager expenseInfoManager) {
        this.expenseInfoManager = expenseInfoManager;
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
