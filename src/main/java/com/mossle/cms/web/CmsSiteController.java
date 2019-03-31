package com.mossle.cms.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.service.RenderService;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cms")
public class CmsSiteController {
    private CmsSiteManager cmsSiteManager;
    private CmsCatalogManager cmsCatalogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;

    @RequestMapping("cms-site-view")
    public String view(Model model) {
        List<CmsCatalog> cmsCatalogs = this.cmsCatalogManager.getAll();
        String html = renderService.viewSite(cmsCatalogs);
        model.addAttribute("html", html);

        return "cms/cms-site-view";
    }

    @RequestMapping("cms-site-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cmsSiteManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-site-list";
    }

    @RequestMapping("cms-site-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsSite cmsSite = cmsSiteManager.get(id);
            model.addAttribute("model", cmsSite);
        }

        return "cms/cms-site-input";
    }

    @RequestMapping("cms-site-save")
    public String save(@ModelAttribute CmsSite cmsSite,
            RedirectAttributes redirectAttributes) {
        // String tenantId = tenantHolder.getTenantId();
        Long id = cmsSite.getId();
        CmsSite dest = null;

        if (id != null) {
            dest = cmsSiteManager.get(id);
            beanMapper.copy(cmsSite, dest);
        } else {
            dest = cmsSite;

            // dest.setTenantId(tenantId);
        }

        cmsSiteManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-site-list.do";
    }

    @RequestMapping("cms-site-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsSite> cmsSites = cmsSiteManager.findByIds(selectedItem);
        cmsCatalogManager.removeAll(cmsSites);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-site-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
    }

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
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
    public void estRenderService(RenderService renderService) {
        this.renderService = renderService;
    }
}
