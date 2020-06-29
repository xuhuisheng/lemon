package com.mossle.cms.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsTemplateCatalog;
import com.mossle.cms.persistence.domain.CmsTemplateContent;
import com.mossle.cms.persistence.manager.CmsTemplateCatalogManager;
import com.mossle.cms.persistence.manager.CmsTemplateContentManager;
import com.mossle.cms.service.RenderService;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("cms/template")
public class CmsTemplateController {
    private CmsTemplateCatalogManager cmsTemplateCatalogManager;
    private CmsTemplateContentManager cmsTemplateContentManager;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;
    private TenantHolder tenantHolder;

    @RequestMapping("index")
    public String index(
            @RequestParam(value = "catalogId", required = false) Long catalogId,
            Model model) {
        CmsTemplateCatalog cmsTemplateCatalog = null;

        if (catalogId != null) {
            cmsTemplateCatalog = cmsTemplateCatalogManager.get(catalogId);
        }

        String tenantId = tenantHolder.getTenantId();
        String catalogHql = "from CmsTemplateCatalog where cmsTemplateCatalog=? order by priority";
        List<CmsTemplateCatalog> cmsTemplateCatalogs = cmsTemplateCatalogManager
                .find(catalogHql, cmsTemplateCatalog);
        model.addAttribute("cmsTemplateCatalogs", cmsTemplateCatalogs);

        String contentHql = "from CmsTemplateContent where cmsTemplateCatalog=? order by priority";
        List<CmsTemplateContent> cmsTemplateContents = cmsTemplateContentManager
                .find(contentHql, cmsTemplateCatalog);
        model.addAttribute("cmsTemplateContents", cmsTemplateContents);

        return "cms/template/index";
    }

    @RequestMapping("dir-input")
    public String dirInput(
            @RequestParam(value = "catalogId", required = false) Long catalogId,
            Model model) {
        return "cms/template/dir-input";
    }

    @RequestMapping("dir-save")
    public String dirSave(@RequestParam("catalogId") Long catalogId,
            @RequestParam("name") String name, Model model) {
        CmsTemplateCatalog cmsTemplateCatalog = new CmsTemplateCatalog();
        cmsTemplateCatalog.setName(name);
        cmsTemplateCatalog.setCmsTemplateCatalog(cmsTemplateCatalogManager
                .get(catalogId));
        cmsTemplateCatalogManager.save(cmsTemplateCatalog);

        return "redirect:/cms/template/index.do?catalogId=" + catalogId;
    }

    @RequestMapping("file-input")
    public String fileInput(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "catalogId", required = false) Long catalogId,
            Model model) {
        if (id != null) {
            model.addAttribute("model", cmsTemplateContentManager.get(id));
        }

        model.addAttribute("cmsTemplateCatalog",
                cmsTemplateCatalogManager.get(catalogId));

        return "cms/template/file-input";
    }

    @RequestMapping("file-save")
    public String fileSave(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("catalogId") Long catalogId,
            @RequestParam("name") String name,
            @RequestParam("content") String content, Model model) {
        CmsTemplateContent cmsTemplateContent = null;

        if (id != null) {
            cmsTemplateContent = cmsTemplateContentManager.get(id);
        } else {
            cmsTemplateContent = new CmsTemplateContent();
            cmsTemplateContent.setCmsTemplateCatalog(cmsTemplateCatalogManager
                    .get(catalogId));
        }

        cmsTemplateContent.setName(name);
        cmsTemplateContent.setContent(content);
        cmsTemplateContentManager.save(cmsTemplateContent);

        return "redirect:/cms/template/index.do?catalogId=" + catalogId;
    }

    // ~ ======================================================================
    @Resource
    public void setCmsTemplateCatalogManager(
            CmsTemplateCatalogManager cmsTemplateCatalogManager) {
        this.cmsTemplateCatalogManager = cmsTemplateCatalogManager;
    }

    @Resource
    public void setCmsTemplateContentgManager(
            CmsTemplateContentManager cmsTemplateContentManager) {
        this.cmsTemplateContentManager = cmsTemplateContentManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
