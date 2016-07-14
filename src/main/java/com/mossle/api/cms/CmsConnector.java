package com.mossle.api.cms;

import com.mossle.core.page.Page;

public interface CmsConnector {
    /**
     * 根据code获得catalog分类.
     */
    CatalogDTO findCatalogByCode(String code, String tenantId);

    /**
     * 获取分类下文章.
     */
    Page findArticles(String code, String tenantId, int pageNo, int pageSize);

    /**
     * 只返回分页信息，不返回数据.
     */
    Page findArticlePageInfo(String code, String tenantId, int pageNo,
            int pageSize);

    /**
     * 根据id获取一个article文章.
     */
    ArticleDTO findArticleById(String id);

    /**
     * 获取对应文章的评论.
     */
    Page findComments(String id, int pageNo, int pageSize);

    /**
     * 只返回分页信息，不返回数据.
     */
    Page findCommentPageInfo(String id, int pageNo, int pageSize);

    /**
     * 添加评论.
     */
    void addComment(String articleId, String content, String userId);
}
