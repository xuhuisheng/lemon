package com.mossle.auth.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.persistence.domain.PermType;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.PermTypeManager;

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
@RequestMapping("auth")
public class PermTypeController {
    private PermTypeManager permTypeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private PermManager permManager;
    private TenantHolder tenantHolder;

    @RequestMapping("perm-type-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = permTypeManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "auth/perm-type-list";
    }

    @RequestMapping("perm-type-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PermType permType = permTypeManager.get(id);
            model.addAttribute("model", permType);
        }

        return "auth/perm-type-input";
    }

    @RequestMapping("perm-type-save")
    public String save(@ModelAttribute PermType permType,
            RedirectAttributes redirectAttributes) {
        // copy
        PermType dest = null;
        Long id = permType.getId();

        if (id != null) {
            dest = permTypeManager.get(id);
            beanMapper.copy(permType, dest);
        } else {
            dest = permType;
        }

        if (id == null) {
            dest.setTenantId(tenantHolder.getTenantId());
        }

        // save
        permTypeManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/auth/perm-type-list.do";
    }

    @RequestMapping("perm-type-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PermType> permTypes = permTypeManager.findByIds(selectedItem);
        permTypeManager.removeAll(permTypes);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/auth/perm-type-list.do";
    }

    @RequestMapping("perm-type-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = permTypeManager.pagedQuery(page, propertyFilters);

        List<PermType> permTypees = (List<PermType>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("permType");
        tableModel.addHeaders("id", "type", "value", "perm.name", "priority",
                "app.name");
        tableModel.setData(permTypees);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
