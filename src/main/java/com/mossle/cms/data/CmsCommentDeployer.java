package com.mossle.cms.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsComment;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsCommentDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCommentDeployer.class);
    private CmsArticleManager cmsArticleManager;
    private CmsCommentManager cmsCommentManager;
    private UserConnector userConnector;
    private String dataFilePath = "data/cms-comment.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    public String readText(String path, String encoding) throws Exception {
        try {
            InputStream is = CmsArticleDeployer.class.getClassLoader()
                    .getResourceAsStream(path);

            return IOUtils.toString(is, encoding);
        } catch (Exception ex) {
            logger.info("cannot find : {}", path);
            logger.error(ex.getMessage(), ex);

            return "";
        }
    }

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", CmsArticleDeployer.class);

            return;
        }

        InputStream is = CmsCommentDeployer.class.getClassLoader()
                .getResourceAsStream(dataFilePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                dataFileEncoding));

        String line = null;
        int lineNo = 0;

        while ((line = reader.readLine()) != null) {
            lineNo++;

            if (lineNo == 1) {
                continue;
            }

            this.processLine(line, lineNo);
        }
    }

    public void processLine(String line, int lineNo) throws Exception {
        String[] array = line.split(",");
        String code = this.processItem(array[0]);
        String createTime = this.processItem(array[1]);
        String author = this.processItem(array[2]);
        String content = this.processItem(array[3]);
        String userId = userConnector.findByUsername(author, defaultTenantId)
                .getId();

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, line);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateCmsComment(code, createTime, userId, content, lineNo);
    }

    public String processItem(String text) {
        if (text == null) {
            logger.info("text is null");

            return "";
        }

        text = text.trim();

        if (text.charAt(0) == '\"') {
            text = text.substring(1);
        }

        if (text.charAt(text.length() - 1) == '\"') {
            text = text.substring(0, text.length() - 1);
        }

        return text;
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

    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsCommentManager(CmsCommentManager cmsCommentManager) {
        this.cmsCommentManager = cmsCommentManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
