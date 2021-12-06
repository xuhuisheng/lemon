package com.mossle.cms.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.cms.persistence.domain.CmsTag;
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
public class CmsTagController {
    private CmsTagManager cmsTagManager;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("cms-tag-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cmsTagManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-tag-list";
    }

    @RequestMapping("cms-tag-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsTag cmsTag = cmsTagManager.get(id);
            model.addAttribute("model", cmsTag);
        }

        return "cms/cms-tag-input";
    }

    @RequestMapping("cms-tag-save")
    public String save(@ModelAttribute CmsTag cmsTag,
            RedirectAttributes redirectAttributes) {
        // String tenantId = tenantHolder.getTenantId();
        Long id = cmsTag.getId();
        CmsTag dest = null;

        if (id != null) {
            dest = cmsTagManager.get(id);
            beanMapper.copy(cmsTag, dest);
        } else {
            dest = cmsTag;

            // dest.setTenantId(tenantId);
        }

        cmsTagManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-tag-list.do";
    }

    @RequestMapping("cms-tag-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsTag> cmsTags = cmsTagManager.findByIds(selectedItem);
        cmsTagManager.removeAll(cmsTags);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-tag-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setCmsTagManager(CmsTagManager cmsTagManager) {
        this.cmsTagManager = cmsTagManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
