package com.mossle.cms.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.domain.CmsTag;
import com.mossle.cms.service.CmsService;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 最常用的应该就是列表搜索
public class CmsHelper {
    private static Logger logger = LoggerFactory.getLogger(CmsHelper.class);
    private String ctx;
    private String cdnPrefix;
    private String cmsPrefix;
    private CmsService cmsService;
    private CmsTag currentTag;
    private CmsCatalog currentCatalog;
    private CmsArticle currentArticle;
    private CmsSite currentSite;
    private String currentUserId;
    private int pageNo = 1;
    private int pageSize = 10;
    private Map<String, Object> serviceMap = new HashMap<String, Object>();
    private Properties prop;
    @Deprecated
    private UserClient uClient;
    private Page page;
    private Map<String, Object> parameterMap;

    public List<CmsCatalog> findTopCatalogs() {
        return cmsService.findTopCatalogs(this.currentSite);
    }

    public CmsCatalog findCatalogByCode(String code) {
        return cmsService.findCatalogByCode(code, this.currentSite);
    }

    public Page findArticlesByCatalogId(Long catalogId) {
        CmsCatalog cmsCatalog = cmsService.findCatalog(catalogId);

        return cmsService.findArticlesByCatalog(cmsCatalog, this.page,
                this.parameterMap);
    }

    /**
     * 搜索一个分类下的文章.
     */
    public Page findArticlesByCatalogCode(String code) {
        CmsCatalog cmsCatalog = cmsService.findCatalogByCode(code,
                this.currentSite);

        return cmsService.findArticlesByCatalog(cmsCatalog, this.page,
                this.parameterMap);
    }

    /**
     * 搜索一个标签下的文章.
     */
    public Page findArticlesByTagCode(String code) {
        CmsTag cmsTag = cmsService.findTagByCode(code, this.currentSite);

        return cmsService.findArticlesByTag(cmsTag, this.page,
                this.parameterMap);
    }

    /**
     * 搜索所有文章.
     */
    public Page findArticles() {
        return cmsService.findArticles(this.currentSite, this.page,
                this.parameterMap);
    }

    public Page findCommentsByArticleId(Long articleId) {
        return cmsService.findComments(articleId, pageNo, pageSize);
    }

    @Deprecated
    public String displayName(String userId) {
        try {
            UserClient userClient = (UserClient) this.findService("userClient");

            if (userClient == null) {
                logger.info("unsupport userClient");

                return userId;
            }

            UserDTO userDto = userClient.findById(userId, "1");

            if (userDto == null) {
                logger.info("cannot find : {}", userId);

                return userId;
            }

            return userDto.getDisplayName();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return "";
        }
    }

    public int findCountByEvent(Long articleId, String event) {
        return this.cmsService.findCountByEvent(articleId, event);
    }

    public List<Map<String, String>> findAttrs(Long articleId) {
        return this.cmsService.findAttrs(articleId);
    }

    public List<CmsTag> findTags() {
        return this.cmsService.findTags(this.currentSite.getId());
    }

    public String findCatalogPathByCatalogId(Long catalogId) {
        return cmsService.findCatalogPathByCatalogId(catalogId);
    }

    // ~
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

    public String getCmsPrefix() {
        return cmsPrefix;
    }

    public void setCmsPrefix(String cmsPrefix) {
        this.cmsPrefix = cmsPrefix;
    }

    public CmsSite getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(CmsSite currentSite) {
        this.currentSite = currentSite;
    }

    public CmsCatalog getCurrentCatalog() {
        return currentCatalog;
    }

    public void setCurrentCatalog(CmsCatalog currentCatalog) {
        this.currentCatalog = currentCatalog;
    }

    public CmsTag getCurrentTag() {
        return this.currentTag;
    }

    public void setCurrentTag(CmsTag currentTag) {
        this.currentTag = currentTag;
    }

    public CmsArticle getCurrentArticle() {
        return currentArticle;
    }

    public void setCurrentArticle(CmsArticle currentArticle) {
        this.currentArticle = currentArticle;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    // service
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

    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    /**
     * @deprecated use findService instead.
     */
    @Deprecated
    public void setUserClient(UserClient uClient) {
        this.uClient = uClient;
    }

    // page
    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
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

    // parameter
    public Map<String, Object> getParameterMap(Map<String, Object> parameterMap) {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public Map<String, Object> getParams() {
        return parameterMap;
    }
}
