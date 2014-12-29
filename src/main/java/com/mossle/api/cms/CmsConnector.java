package com.mossle.api.cms;

import com.mossle.core.page.Page;

public interface CmsConnector {
    CatalogDTO findCatalogByCode(String code);

    Page findArticles(String code, int pageNo, int pageSize);

    Page findArticlePageInfo(String code, int pageNo, int pageSize);

    ArticleDTO findArticleById(String id);

    Page findComments(String id, int pageNo, int pageSize);

    Page findCommentPageInfo(String id, int pageNo, int pageSize);

    void addComment(String articleId, String content, String userId);
}
