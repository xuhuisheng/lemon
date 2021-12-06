package com.mossle.cms.web.preview;

import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.domain.CmsTag;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.service.CmsService;
import com.mossle.cms.service.RenderService;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("cms/preview")
public class CmsPreviewController {
    private static Logger logger = LoggerFactory
            .getLogger(CmsPreviewController.class);
    public static final String CMS_PREFIX = "cms/preview/";
    private CmsSiteManager cmsSiteManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private RenderService renderService;
    private CurrentUserHolder currentUserHolder;
    private CmsService cmsService;

    /**
     * 模板，站点首页.
     * 
     * <p>
     * 首页维度，可能显示分类树，标签，推荐文章。
     * </p>
     */
    @RequestMapping("{siteCode}")
    public String index(@PathVariable("siteCode") String siteCode,
            @RequestParam Map<String, Object> parameterMap, Page page,
            Model model, HttpServletRequest request) {
        String targetCmsPrefix = CMS_PREFIX + siteCode;
        String currentUserId = this.currentUserHolder.getUserId();
        CmsSite cmsSite = cmsSiteManager.findUniqueBy("code", siteCode);
        String templateCode = cmsSite.getTemplateCode();
        String ctx = request.getContextPath();

        page.setDefaultOrder("publishTime", Page.DESC);

        String html = renderService.renderText(templateCode, ctx,
                targetCmsPrefix, cmsSite, page, parameterMap, currentUserId);

        model.addAttribute("html", html);

        return "cms/preview/index";
    }

    /**
     * 模板，分类文章列表.
     * 
     * <p>
     * 分类维度，比首页多一个当前分类的实体。
     * </p>
     */
    @RequestMapping("{siteCode}/catalog/{catalogCode}/articles")
    public String catalogArticles(@PathVariable("siteCode") String siteCode,
            @PathVariable("catalogCode") String catalogCode,
            @RequestParam Map<String, Object> parameterMap, Page page,
            Model model, HttpServletRequest request) {
        String targetCmsPrefix = CMS_PREFIX + siteCode;
        String currentUserId = this.currentUserHolder.getUserId();
        CmsSite cmsSite = cmsSiteManager.findUniqueBy("code", siteCode);
        CmsCatalog cmsCatalog = cmsCatalogManager.findUnique(
                "from CmsCatalog where cmsSite.id=? and code=?",
                cmsSite.getId(), catalogCode);
        String templateCode = cmsCatalog.getTemplateList();
        String ctx = request.getContextPath();

        page.setDefaultOrder("publishTime", Page.DESC);

        String html = renderService.renderText(templateCode, ctx,
                targetCmsPrefix, cmsCatalog, page, parameterMap, currentUserId);
        model.addAttribute("html", html);

        return "cms/preview/index";
    }

    /**
     * 模板，标签文章列表.
     * 
     * <p>
     * 标签维度，比首页多一个当前标签的实体。
     * </p>
     */
    @RequestMapping("{siteCode}/tag/{tagCode}/articles")
    public String tagArticles(@PathVariable("siteCode") String siteCode,
            @PathVariable("tagCode") String tagCode,
            @RequestParam Map<String, Object> parameterMap, Page page,
            Model model, HttpServletRequest request) {
        String targetCmsPrefix = CMS_PREFIX + siteCode;
        String currentUserId = this.currentUserHolder.getUserId();
        CmsSite cmsSite = cmsSiteManager.findUniqueBy("code", siteCode);
        CmsTag cmsTag = cmsService.findTagByCode(tagCode, cmsSite);
        String templateCode = cmsSite.getTemplateTag();
        String ctx = request.getContextPath();

        page.setDefaultOrder("publishTime", Page.DESC);

        String html = renderService.renderText(templateCode, ctx,
                targetCmsPrefix, cmsTag, page, parameterMap, currentUserId);
        model.addAttribute("html", html);

        return "cms/preview/index";
    }

    /**
     * 模板，文章详情.
     * 
     * <p>
     * 文章维度，比首页多一个当前文章的实体。
     * </p>
     */
    @RequestMapping("{siteCode}/article/{articleId}")
    public String article(@PathVariable("siteCode") String siteCode,
            @PathVariable("articleId") Long articleId,
            @RequestParam Map<String, Object> parameterMap, Page page,
            Model model, HttpServletRequest request) {
        String targetCmsPrefix = CMS_PREFIX + siteCode;
        String currentUserId = this.currentUserHolder.getUserId();
        CmsArticle cmsArticle = cmsArticleManager.get(articleId);

        if (cmsArticle == null) {
            logger.info("cannot find article : {}", articleId);

            return "cms/preview/404";
        }

        String userId = currentUserHolder.getUserId();
        this.cmsService.recordClick(articleId, userId);

        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        String templateCode = cmsCatalog.getTemplateDetail();
        String ctx = request.getContextPath();
        String html = renderService.renderText(templateCode, ctx,
                targetCmsPrefix, cmsArticle, page, parameterMap, currentUserId);

        model.addAttribute("html", html);

        return "cms/preview/index";
    }

    /**
     * 根据aritcleCode转发至文章详情.
     */
    @RequestMapping("{siteCode}/article/code/{articleCode}")
    public String redirectArticle(@PathVariable("siteCode") String siteCode,
            @PathVariable("articleCode") String articleCode) {
        CmsArticle cmsArticle = cmsArticleManager.findUniqueBy("code",
                articleCode);
        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        String catalogCode = cmsCatalog.getCode();
        Long articleId = cmsArticle.getId();

        return "redirect:/cms/preview/" + siteCode + "/article/" + articleId;
    }

    /**
     * 模板，渲染固定模板.
     */
    @RequestMapping("{siteCode}/template/{templateName}")
    public String template(@PathVariable("siteCode") String siteCode,
            @PathVariable("templateName") String templateName,
            @RequestParam Map<String, Object> parameterMap, Model model,
            Page page, HttpServletRequest request) {
        String targetCmsPrefix = CMS_PREFIX + siteCode;
        String currentUserId = this.currentUserHolder.getUserId();
        CmsSite cmsSite = cmsSiteManager.findUniqueBy("code", siteCode);

        // CmsTemplateContent cmsTemplateContent = cmsTemplateContentManager
        // .get(templateId);
        String templateCode = templateName.replace("-0-", "/").replace("-1-",
                ".");

        // logger.info("template code : {}", templateCode);
        String ctx = request.getContextPath();

        page.setDefaultOrder("publishTime", Page.DESC);

        // model.addAttribute("page", page);
        String html = renderService.renderText(templateCode, ctx,
                targetCmsPrefix, cmsSite, page, parameterMap, currentUserId);
        model.addAttribute("html", html);

        return "cms/preview/index";
    }

    /**
     * TODO: search
     */

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
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
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
