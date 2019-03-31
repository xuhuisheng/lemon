package com.mossle.cms.web.view;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.service.CmsService;
import com.mossle.cms.service.RenderService;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cms/view-comment")
public class CmsViewCommentController {
    private CmsSiteManager cmsSiteManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private CmsCommentManager cmsCommentManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;
    private TenantHolder tenantHolder;
    private CmsService cmsService;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("submit")
    public String submit(@ModelAttribute CmsComment cmsComment,
            @RequestParam("articleId") Long articleId,
            RedirectAttributes redirectAttributes) {
        CmsArticle cmsArticle = cmsArticleManager.get(articleId);

        String userId = currentUserHolder.getUserId();
        cmsComment.setCmsArticle(cmsArticle);
        cmsComment.setCreateTime(new Date());
        cmsComment.setUserId(userId);
        cmsCommentManager.save(cmsComment);

        return "redirect:/cms/view/" + cmsArticle.getCmsCatalog().getCode()
                + "/" + articleId;
    }

    @RequestMapping("reply")
    @ResponseBody
    public String reply(@RequestParam("articleId") Long articleId,
            @RequestParam("commentId") Long commentId,
            @RequestParam("content") String content) {
        CmsComment parent = cmsCommentManager.get(commentId);
        Long conversation = parent.getId();

        if (parent.getConversation() != null) {
            conversation = parent.getConversation();
        }

        String userId = currentUserHolder.getUserId();
        CmsComment cmsComment = new CmsComment();
        cmsComment.setCmsArticle(cmsArticleManager.get(articleId));
        cmsComment.setCreateTime(new Date());
        cmsComment.setUserId(userId);
        cmsComment.setConversation(conversation);
        cmsComment.setContent(content);
        cmsComment.setCmsComment(parent);
        cmsCommentManager.save(cmsComment);

        return "true";
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
    public void setCmsCommentManager(CmsCommentManager cmsCommentManager) {
        this.cmsCommentManager = cmsCommentManager;
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
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
