package com.mossle.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsAttr;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsClick;
import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.domain.CmsCount;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.domain.CmsTag;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsAttrManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsClickManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.persistence.manager.CmsCountManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.persistence.manager.CmsTagManager;
import com.mossle.cms.support.CommentDTO;

import com.mossle.core.page.Page;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class CmsService {
    private static Logger logger = LoggerFactory.getLogger(CmsService.class);
    private CmsCatalogManager cmsCatalogManager;
    private CmsSiteManager cmsSiteManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCommentManager cmsCommentManager;
    private CmsClickManager cmsClickManager;
    private CmsCountManager cmsCountManager;
    private CmsAttrManager cmsAttrManager;
    private CmsTagManager cmsTagManager;

    /**
     * 查询顶级分类.
     */
    public List<CmsCatalog> findTopCatalogs(CmsSite cmsSite) {
        String hql = "from CmsCatalog where cmsCatalog=null and cmsSite=? order by priority";
        List<CmsCatalog> cmsCatalogs = this.cmsCatalogManager
                .find(hql, cmsSite);

        return cmsCatalogs;
    }

    /**
     * 根据id查询分类.
     */
    public CmsCatalog findCatalog(Long catalogId) {
        return cmsCatalogManager.get(catalogId);
    }

    /**
     * 根据code查询分类.
     */
    public CmsCatalog findCatalogByCode(String code, CmsSite cmsSite) {
        String hql = "from CmsCatalog where code=? and cmsSite=?";

        return cmsCatalogManager.findUnique(hql, code, cmsSite);
    }

    /**
     * 分类路径.
     */
    public String findCatalogPathByCatalogId(Long catalogId) {
        StringBuilder buff = new StringBuilder();
        CmsCatalog cmsCatalog = cmsCatalogManager.get(catalogId);

        while (cmsCatalog != null) {
            buff.insert(0, "/").insert(1, cmsCatalog.getName());
            cmsCatalog = cmsCatalog.getCmsCatalog();
        }

        buff.deleteCharAt(0);

        return buff.toString();
    }

    /**
     * 获取默认站点.
     */
    public CmsSite findDefaultSite() {
        String hql = "from CmsSite order by priority";
        CmsSite cmsSite = cmsSiteManager.findUnique(hql);

        return cmsSite;
    }

    public CmsSite findCurrentSite(Long currentSiteId) {
        if (currentSiteId == null) {
            return this.findDefaultSite();
        }

        CmsSite cmsSite = cmsSiteManager.get(currentSiteId);

        if (cmsSite == null) {
            return this.findDefaultSite();
        }

        return cmsSite;
    }

    /**
     * 根据code查询站点.
     */
    public CmsSite findSiteByCode(String siteCode) {
        if (StringUtils.isBlank(siteCode)) {
            logger.error("site code cannot be blank");

            return null;
        }

        String hql = "from CmsSite where code=?";
        CmsSite cmsSite = this.cmsSiteManager.findUnique(hql, siteCode);

        return cmsSite;
    }

    /**
     * 查询所有文章.
     */
    public Page findArticles(CmsSite cmsSite, Page page,
            Map<String, Object> parameterMap) {
        String hql = "from CmsArticle where cmsSite=? order by weight asc";

        if (page.isOrderEnabled()) {
            hql += (", " + page.getOrderBy() + " " + page.getOrder());
        }

        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, cmsSite);
    }

    /**
     * 根据catalogId查询文章.
     */
    public Page findArticlesByCatalogId(Long catalogId, int pageNo, int pageSize) {
        String hql = "from CmsArticle where cmsCatalog.id=? order by publishTime desc";

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, catalogId);
    }

    /**
     * 根据catalogCode查询文章.
     */
    public Page findArticlesByCatalogCode(String catalogCode, int pageNo,
            int pageSize) {
        String hql = "select cmsArticle from CmsArticle cmsArticle "
                + "where cmsArticle.cmsCatalog.code=? order by cmsArticle.publishTime desc";

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, catalogCode);
    }

    public Page findArticlesByCatalog(CmsCatalog cmsCatalog, Page page,
            Map<String, Object> parameterMap) {
        if (page == null) {
            logger.info("page cannot be null");

            return null;
        }

        String hql = "from CmsArticle where cmsCatalog=? order by weight asc";

        if (page.isOrderEnabled()) {
            hql += (", " + page.getOrderBy() + " " + page.getOrder());
        }

        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, cmsCatalog);
    }

    public Page findArticlesByTag(CmsTag cmsTag, Page page,
            Map<String, Object> parameterMap) {
        String hql = "select article from CmsArticle as article left join article.cmsTagArticles as tagArticle "
                + "where tagArticle.cmsTag=? order by article.weight asc";

        if (page.isOrderEnabled()) {
            hql += (", article." + page.getOrderBy() + " " + page.getOrder());
        }

        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();

        return cmsArticleManager.pagedQuery(hql, pageNo, pageSize, cmsTag);
    }

    /**
     * 根据articleId查询评论.
     * 
     * <p>
     * 如果使用评论服务，应该就不用从本地查询评论了
     * </p>
     */
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

    /**
     * 记录点击量.
     */
    public void recordClick(Long articleId, String userId) {
        CmsArticle cmsArticle = cmsArticleManager.get(articleId);
        cmsArticle.setHitCount(cmsArticle.getHitCount() + 1);
        cmsArticleManager.save(cmsArticle);

        CmsClick cmsClick = cmsClickManager.findUnique(
                "from CmsClick where cmsArticle.id=? and userId=?", articleId,
                userId);

        if (cmsClick == null) {
            cmsClick = new CmsClick();
            cmsClick.setCmsArticle(cmsArticle);
            cmsClick.setUserId(userId);
            cmsClick.setCreateTime(new Date());
            cmsClickManager.save(cmsClick);
        }
    }

    /**
     * 获取文章的事件计数.
     */
    public int findCountByEvent(Long articleId, String event) {
        CmsArticle cmsArticle = this.cmsArticleManager.get(articleId);

        if (cmsArticle == null) {
            logger.info("cannot find article : {}", articleId);

            return 0;
        }

        CmsCount cmsCount = cmsCountManager.findUnique(
                "from CmsCount where cmsArticle.id=? and code=?", articleId,
                event);

        if (cmsCount == null) {
            return 0;
        }

        return cmsCount.getValue();
    }

    /**
     * 对文章事件计数.
     */
    public void recordEventCount(Long articleId, String event) {
        CmsArticle cmsArticle = cmsArticleManager.get(articleId);

        if (cmsArticle == null) {
            logger.info("cannot find article : {}", articleId);

            return;
        }

        CmsCount cmsCount = cmsCountManager.findUnique(
                "from CmsCount where cmsArticle.id=? and code=?", articleId,
                event);

        if (cmsCount == null) {
            cmsCount = new CmsCount();
            cmsCount.setCmsArticle(cmsArticle);
            cmsCount.setCode(event);
            cmsCount.setValue(1);
            cmsCountManager.save(cmsCount);
        } else {
            cmsCount.setValue(cmsCount.getValue() + 1);
            cmsCountManager.save(cmsCount);
        }
    }

    /**
     * 根据articleId获取属性.
     */
    public List<Map<String, String>> findAttrs(Long articleId) {
        String hql = "from CmsAttr where cmsArticle.id=? order by priority";
        List<CmsAttr> cmsAttrs = this.cmsAttrManager.find(hql, articleId);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        for (CmsAttr cmsAttr : cmsAttrs) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("code", cmsAttr.getCode());
            map.put("value", cmsAttr.getValue());
            list.add(map);
        }

        return list;
    }

    /**
     * 查询标签.
     */
    public List<CmsTag> findTags(Long siteId) {
        String hql = "from CmsTag where cmsSite.id=?";
        List<CmsTag> cmsTags = this.cmsTagManager.find(hql, siteId);

        return cmsTags;
    }

    public CmsTag findTagByCode(String code, CmsSite cmsSite) {
        String hql = "from CmsTag where code=? and cmsSite=?";

        return this.cmsTagManager.findUnique(hql, code, cmsSite);
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

    @Resource
    public void setCmsClickManager(CmsClickManager cmsClickManager) {
        this.cmsClickManager = cmsClickManager;
    }

    @Resource
    public void setCmsCountManager(CmsCountManager cmsCountManager) {
        this.cmsCountManager = cmsCountManager;
    }

    @Resource
    public void setCmsAttrManager(CmsAttrManager cmsAttrManager) {
        this.cmsAttrManager = cmsAttrManager;
    }

    @Resource
    public void setCmsTagManager(CmsTagManager cmsTagManager) {
        this.cmsTagManager = cmsTagManager;
    }
}
