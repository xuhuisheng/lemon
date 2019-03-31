package com.mossle.cms.data;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsCommentCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCommentCallback.class);
    private CmsCommentManager cmsCommentManager;
    private CmsArticleManager cmsArticleManager;
    private UserConnector userConnector;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String createTime = list.get(1);
        String author = list.get(2);
        String content = list.get(3);

        String userId = userConnector.findByUsername(author, defaultTenantId)
                .getId();

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateCmsComment(code, createTime, userId, content, lineNo);
    }

    public void createOrUpdateCmsComment(String code, String createTime,
            String userId, String content, int lineNo) {
        CmsArticle cmsArticle = cmsArticleManager.findUniqueBy("code", code);

        if (cmsArticle == null) {
            logger.info("cannot find article : {}", code);

            return;
        }

        String hql = "from CmsComment where content=? and userId=?";
        CmsComment cmsComment = cmsCommentManager.findUnique(hql, content,
                userId);

        if (cmsComment != null) {
            logger.info("skip exists comment : {}", content);

            return;
        }

        try {
            Date createTimeValue = new SimpleDateFormat("yyy-MM-dd HH:mm:ss")
                    .parse(createTime);
            cmsComment = new CmsComment();
            cmsComment.setCmsArticle(cmsArticle);
            cmsComment.setContent(content);
            cmsComment.setUserId(userId);
            cmsComment.setCreateTime(createTimeValue);
            cmsComment.setTenantId(defaultTenantId);
            cmsCommentManager.save(cmsComment);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setCmsCommentManager(CmsCommentManager cmsCommentManager) {
        this.cmsCommentManager = cmsCommentManager;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }
}
