package com.mossle.workcal.web;

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

import com.mossle.workcal.persistence.domain.WorkcalRule;
import com.mossle.workcal.persistence.manager.WorkcalRuleManager;
import com.mossle.workcal.persistence.manager.WorkcalTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("workcal")
public class WorkcalRuleController {
    private WorkcalRuleManager workcalRuleManager;
    private WorkcalTypeManager workcalTypeManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("workcal-rule-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = workcalRuleManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "workcal/workcal-rule-list";
    }

    @RequestMapping("workcal-rule-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id != null) {
            WorkcalRule workcalRule = workcalRuleManager.get(id);
            model.addAttribute("model", workcalRule);
        }

        model.addAttribute("workcalTypes",
                workcalTypeManager.findBy("tenantId", tenantId));

        return "workcal/workcal-rule-input";
    }

    @RequestMapping("workcal-rule-save")
    public String save(@ModelAttribute WorkcalRule workcalRule,
            @RequestParam("workcalTypeId") Long workcalTypeId,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = workcalRule.getId();
        WorkcalRule dest = null;

        if (id != null) {
            dest = workcalRuleManager.get(id);
            beanMapper.copy(workcalRule, dest);
        } else {
            dest = workcalRule;
            dest.setTenantId(tenantId);
        }

        dest.setWorkcalType(workcalTypeManager.get(workcalTypeId));
        workcalRuleManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/workcal/workcal-rule-list.do";
    }

    @RequestMapping("workcal-rule-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WorkcalRule> workcalRules = workcalRuleManager
                .findByIds(selectedItem);
        workcalRuleManager.removeAll(workcalRules);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/workcal/workcal-rule-list.do";
    }

    @RequestMapping("workcal-rule-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = workcalRuleManager.pagedQuery(page, propertyFilters);

        List<WorkcalRule> workcalRules = (List<WorkcalRule>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("workcalRule");
        tableModel.addHeaders("id", "name");
        tableModel.setData(workcalRules);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("workcal-rule-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from WorkcalRule where name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from WorkcalRule where name=? and id<>?";
            params = new Object[] { name, id };
        }

        WorkcalRule workcalRule = workcalRuleManager.findUnique(hql, params);

        boolean result = (workcalRule == null);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setWorkcalRuleManager(WorkcalRuleManager workcalRuleManager) {
        this.workcalRuleManager = workcalRuleManager;
    }

    @Resource
    public void setWorkcalTypeManager(WorkcalTypeManager workcalTypeManager) {
        this.workcalTypeManager = workcalTypeManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
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
