package com.mossle.auth.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.manager.AccessManager;
import com.mossle.auth.persistence.manager.PermManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.spi.auth.ResourcePublisher;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class AccessController {
    private AccessManager accessManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private PermManager permManager;
    private ResourcePublisher resourcePublisher;
    private TenantHolder tenantHolder;

    @RequestMapping("access-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = accessManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "auth/access-list";
    }

    @RequestMapping("access-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            Access access = accessManager.get(id);
            model.addAttribute("model", access);
        }

        List<Perm> perms = permManager.findBy("tenantId",
                tenantHolder.getTenantId());
        model.addAttribute("perms", perms);

        return "auth/access-input";
    }

    @RequestMapping("access-save")
    public String save(@ModelAttribute Access access,
            @RequestParam("permId") Long permId,
            RedirectAttributes redirectAttributes) {
        // copy
        Access dest = null;

        Long id = access.getId();

        if (id != null) {
            dest = accessManager.get(id);
            beanMapper.copy(access, dest);
        } else {
            dest = access;
        }

        // foreign
        Perm perm = permManager.get(permId);
        dest.setPerm(perm);

        if (id == null) {
            dest.setTenantId(tenantHolder.getTenantId());
        }

        // save
        accessManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");
        resourcePublisher.publish();

        return "redirect:/auth/access-list.do";
    }

    @RequestMapping("access-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<Access> accesses = accessManager.findByIds(selectedItem);
        accessManager.removeAll(accesses);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        resourcePublisher.publish();

        return "redirect:/auth/access-list.do";
    }

    @RequestMapping("access-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = accessManager.pagedQuery(page, propertyFilters);

        List<Access> accesses = (List<Access>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("access");
        tableModel.addHeaders("id", "type", "value", "perm.name", "priority",
                "tenantId");
        tableModel.setData(accesses);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
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
    public void setResourcePublisher(ResourcePublisher resourcePublisher) {
        this.resourcePublisher = resourcePublisher;
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
