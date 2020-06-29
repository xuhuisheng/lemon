package com.mossle.cms.web.view;

import java.util.List;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.service.CmsService;
import com.mossle.cms.service.RenderService;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("cms/view")
public class CmsViewController {
    private CmsSiteManager cmsSiteManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private CmsService cmsService;

    /**
     * 模板，站点首页.
     */
    @RequestMapping("")
    public String index(Model model, HttpServletRequest request) {
        String currentUserId = this.currentUserHolder.getUserId();
        CmsSite cmsSite = cmsService.findDefaultSite();
        String templateCode = cmsSite.getTemplateCode();
        String ctx = request.getContextPath();

        String html = renderService
                .renderText(templateCode, ctx, currentUserId);

        model.addAttribute("html", html);

        return "cms/view/index";
    }

    /**
     * 模板，分类列表.
     */
    @RequestMapping("{catalogCode}")
    public String catalog(
            @PathVariable("catalogCode") String catalogCode,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            Model model, HttpServletRequest request) {
        CmsCatalog cmsCatalog = cmsCatalogManager.findUniqueBy("code",
                catalogCode);
        String templateCode = cmsCatalog.getTemplateList();
        String ctx = request.getContextPath();
        String html = renderService.renderText(templateCode, ctx, cmsCatalog,
                pageNo, pageSize);
        model.addAttribute("html", html);

        return "cms/view/index";
    }

    /**
     * 模板，文章详情.
     */
    @RequestMapping("{catalogCode}/{articleId}")
    public String article(
            @PathVariable("catalogCode") String catalogCode,
            @PathVariable("articleId") Long articleId,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            Model model, HttpServletRequest request) {
        CmsCatalog cmsCatalog = cmsCatalogManager.findUniqueBy("code",
                catalogCode);
        CmsArticle cmsArticle = cmsArticleManager.get(articleId);
        String templateCode = cmsCatalog.getTemplateDetail();
        String ctx = request.getContextPath();
        String html = renderService.renderText(templateCode, ctx, cmsArticle,
                pageNo, pageSize);

        model.addAttribute("html", html);

        return "cms/view/index";
    }

    /**
     * 预览.
     */
    @RequestMapping("preview/{catalogCode}/{articleId}")
    public String previewArticle(
            @PathVariable("catalogCode") String catalogCode,
            @PathVariable("articleId") Long articleId, Model model,
            HttpServletRequest request) {
        CmsCatalog cmsCatalog = cmsCatalogManager.findUniqueBy("code",
                catalogCode);
        CmsArticle cmsArticle = cmsArticleManager.get(articleId);
        String templateCode = cmsCatalog.getTemplateDetail();
        String ctx = request.getContextPath();
        int pageNo = 1;
        int pageSize = 10;
        String html = renderService.renderText(templateCode, ctx, cmsArticle,
                pageNo, pageSize);

        model.addAttribute("html", html);

        return "cms/view/preview";
    }

    @RequestMapping("redirect/{articleCode}")
    public String redirectArticle(
            @PathVariable("articleCode") String articleCode) {
        CmsArticle cmsArticle = cmsArticleManager.findUniqueBy("code",
                articleCode);
        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        String catalogCode = cmsCatalog.getCode();
        Long articleId = cmsArticle.getId();

        return "redirect:/cms/view/" + catalogCode + "/" + articleId;
    }

    // ~ ======================================================================
    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
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
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }
}
