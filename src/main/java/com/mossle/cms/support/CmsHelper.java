package com.mossle.cms.support;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.client.user.UserClient;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.service.CmsService;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

public class CmsHelper {
    private static Logger logger = LoggerFactory.getLogger(CmsHelper.class);
    private String ctx;
    private CmsService cmsService;
    private UserClient userClient;
    private CmsCatalog catalog;
    private CmsArticle article;
    private int pageNo = 1;
    private int pageSize = 10;

    public List<CmsCatalog> catalogs() {
        return cmsService.getTopCatalogs();
    }

    public Page articles(Long catalogId) {
        return cmsService.findArticles(catalogId, pageNo, pageSize);
    }

    public Page comments(Long articleId) {
        return cmsService.findComments(articleId, pageNo, pageSize);
    }

    public String displayName(String userId) {
        try {
            return userClient.findById(userId, "1").getDisplayName();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return "";
        }
    }

    // ~
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    public String getCtx() {
        return ctx;
    }

    public void setCtx(String ctx) {
        this.ctx = ctx;
    }

    public CmsCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(CmsCatalog catalog) {
        this.catalog = catalog;
    }

    public CmsArticle getArticle() {
        return article;
    }

    public void setArticle(CmsArticle article) {
        this.article = article;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
