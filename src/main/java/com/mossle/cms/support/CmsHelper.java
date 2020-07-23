package com.mossle.cms.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

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
    private String cdnPrefix;
    private CmsService cmsService;
    private CmsCatalog catalog;
    private CmsArticle article;
    private String currentUserId;
    private int pageNo = 1;
    private int pageSize = 10;
    private Map<String, Object> serviceMap = new HashMap<String, Object>();
    private Properties prop;
    @Deprecated
    private UserClient uClient;

    public List<CmsCatalog> catalogs() {
        return cmsService.getTopCatalogs();
    }

    public CmsCatalog catalog(String code) {
        return cmsService.findCatalogByCode(code);
    }

    public Page articles(Long catalogId) {
        return cmsService.findArticles(catalogId, pageNo, pageSize);
    }

    public Page findArticlesByCatalogCode(String code) {
        return cmsService.findArticlesByCatalogCode(code, pageNo, pageSize);
    }

    public Page comments(Long articleId) {
        return cmsService.findComments(articleId, pageNo, pageSize);
    }

    @Deprecated
    public String displayName(String userId) {
        try {
            UserClient userClient = (UserClient) this.findService("userClient");

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

    @Deprecated
    public void setUserClient(UserClient uClient) {
        this.uClient = uClient;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    public Properties getProp() {
        return prop;
    }

    public String getCtx() {
        return ctx;
    }

    public void setCtx(String ctx) {
        this.ctx = ctx;
    }

    public String getCdnPrefix() {
        return cdnPrefix;
    }

    public void setCdnPrefix(String cdnPrefix) {
        this.cdnPrefix = cdnPrefix;
    }

    public CmsCatalog getCurrentCatalog() {
        return catalog;
    }

    public void setCurrentCatalog(CmsCatalog catalog) {
        this.catalog = catalog;
    }

    public CmsArticle getCurrentArticle() {
        return article;
    }

    public void setCurrentArticle(CmsArticle article) {
        this.article = article;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
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

    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }

    public Object findService(String name) {
        return serviceMap.get(name);
    }

    public UserDTO getUser() {
        try {
            UserClient userClient = (UserClient) this.findService("userClient");

            if (userClient == null) {
                logger.info("cannot find userClient");

                return null;
            }

            return userClient.findById(currentUserId, "1");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }
}
