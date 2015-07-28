package com.mossle.api.cms;

import com.mossle.core.page.Page;

public class MockCmsConnector implements CmsConnector {
    public CatalogDTO findCatalogByCode(String code) {
        return null;
    }

    public Page findArticles(String code, int pageNo, int pageSize) {
        return null;
    }

    public Page findArticlePageInfo(String code, int pageNo, int pageSize) {
        return null;
    }

    public ArticleDTO findArticleById(String id) {
        return null;
    }

    public Page findComments(String id, int pageNo, int pageSize) {
        return null;
    }

    public Page findCommentPageInfo(String id, int pageNo, int pageSize) {
        return null;
    }

    public void addComment(String articleId, String content, String userId) {
    }
}
