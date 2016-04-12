package com.mossle.auth.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.persistence.domain.Perm;
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
public class PermController {
    private PermManager permManager;
    private PermTypeManager permTypeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("perm-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = permManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "auth/perm-list";
    }

    @RequestMapping("perm-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            Perm perm = permManager.get(id);
            model.addAttribute("model", perm);
        }

        List<PermType> permTypes = permTypeManager.findBy("tenantId",
                tenantHolder.getTenantId());

        model.addAttribute("permTypes", permTypes);

        return "auth/perm-input";
    }

    @RequestMapping("perm-save")
    public String save(@ModelAttribute Perm perm,
            @RequestParam("permTypeId") Long permTypeId,
            RedirectAttributes redirectAttributes) {
        Perm dest = null;
        Long id = perm.getId();

        if (id != null) {
            dest = permManager.get(id);
            beanMapper.copy(perm, dest);
        } else {
            dest = perm;
        }

        if (id == null) {
            dest.setTenantId(tenantHolder.getTenantId());
        }

        dest.setPermType(permTypeManager.get(permTypeId));
        permManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/auth/perm-list.do";
    }

    @RequestMapping("perm-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<Perm> perms = permManager.findByIds(selectedItem);
        permManager.removeAll(perms);
        messageHelper.addFlashMessage(redirectAttributes, "core.delete.save",
                "删除成功");

        return "redirect:/auth/perm-list.do";
    }

    @RequestMapping("perm-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = permManager.pagedQuery(page, propertyFilters);

        List<Perm> perms = (List<Perm>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("perm");
        tableModel.addHeaders("id", "name");
        tableModel.setData(perms);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
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
