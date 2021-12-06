package com.mossle.cms.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.cms.persistence.domain.CmsAttr;
import com.mossle.cms.persistence.manager.CmsAttrManager;
import com.mossle.cms.persistence.manager.CmsTagManager;

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
@RequestMapping("cms")
public class CmsAttrController {
    private CmsTagManager cmsTagManager;
    private CmsAttrManager cmsAttrManager;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("cms-attr-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cmsAttrManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-attr-list";
    }

    @RequestMapping("cms-attr-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsAttr cmsAttr = cmsAttrManager.get(id);
            model.addAttribute("model", cmsAttr);
        }

        return "cms/cms-attr-input";
    }

    @RequestMapping("cms-attr-save")
    public String save(@ModelAttribute CmsAttr cmsAttr,
            RedirectAttributes redirectAttributes) {
        // String tenantId = tenantHolder.getTenantId();
        Long id = cmsAttr.getId();
        CmsAttr dest = null;

        if (id != null) {
            dest = cmsAttrManager.get(id);
            beanMapper.copy(cmsAttr, dest);
        } else {
            dest = cmsAttr;

            // dest.setTenantId(tenantId);
        }

        cmsTagManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-attr-list.do";
    }

    @RequestMapping("cms-attr-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsAttr> cmsAttrs = cmsAttrManager.findByIds(selectedItem);
        cmsAttrManager.removeAll(cmsAttrs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-attr-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setCmsTagManager(CmsTagManager cmsTagManager) {
        this.cmsTagManager = cmsTagManager;
    }

    @Resource
    public void setCmsAttrManager(CmsAttrManager cmsAttrManager) {
        this.cmsAttrManager = cmsAttrManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
