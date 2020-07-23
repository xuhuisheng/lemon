package com.mossle.cms.service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.support.CommentDTO;

import com.mossle.core.page.Page;
import com.mossle.core.template.TemplateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.util.Assert;

@Service
public class CmsService {
    private static Logger logger = LoggerFactory.getLogger(CmsService.class);
    private CmsCatalogManager cmsCatalogManager;
    private CmsSiteManager cmsSiteManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCommentManager cmsCommentManager;

    public List<CmsCatalog> getTopCatalogs() {
        String hql = "from CmsCatalog where cmsCatalog=null order by priority";
        List<CmsCatalog> cmsCatalogs = this.cmsCatalogManager.find(hql);

        return cmsCatalogs;
    }

    public CmsCatalog findCatalogByCode(String code) {
        return cmsCatalogManager.findUniqueBy("code", code);
    }

    public CmsSite findDefaultSite() {
        String hql = "from CmsSite";
        CmsSite cmsSite = cmsSiteManager.findUnique(hql);

        return cmsSite;
    }

    public Page findArticles(Long catalogId, int pageNo, int pageSize) {
        String hql = "from CmsArticle where cmsCatalog.id=? order by publishTime desc";

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, catalogId);
    }

    public Page findArticlesByCatalogCode(String catalogCode, int pageNo,
            int pageSize) {
        String hql = "select cmsArticle from CmsArticle cmsArticle "
                + "where cmsArticle.cmsCatalog.code=? order by cmsArticle.publishTime desc";

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, catalogCode);
    }

    public Page findComments(Long articleId, int pageNo, int pageSize) {
        Page page = this.cmsCommentManager
                .pagedQuery(
                        "from CmsComment where cmsArticle.id=? and conversation=null order by id desc",
                        pageNo, pageSize, articleId);

        List<CmsComment> cmsComments = (List<CmsComment>) page.getResult();

        List<CommentDTO> commentDtos = new ArrayList<CommentDTO>();
        page.setResult(commentDtos);

        for (CmsComment cmsComment : cmsComments) {
            CommentDTO commentDto = new CommentDTO();
            commentDto.setCmsComment(cmsComment);

            String hql = "from CmsComment where conversation=? order by id asc";
            commentDto.setChildren(cmsCommentManager.find(hql,
                    cmsComment.getId()));
            commentDtos.add(commentDto);
        }

        return page;
    }

    // ~ ==================================================
    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
    }

    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsCommentManager(CmsCommentManager cmsCommentManager) {
        this.cmsCommentManager = cmsCommentManager;
    }
}
