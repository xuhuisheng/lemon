package com.mossle.cms.data;

import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import com.mossle.client.user.UserClient;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsArticleCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsArticleCallback.class);
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private UserClient userClient;
    private String defaultTenantId = "1";
    private String dataFileEncoding = "UTF-8";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String title = list.get(1);
        String createTime = list.get(2);
        String catalog = list.get(3);
        String hit = list.get(4);
        String author = list.get(5);
        String content = this.readText("data/cms/" + code + ".txt",
                dataFileEncoding);
        String userId = author;
        try {
            userId = userClient.findByUsername(author, defaultTenantId)
                    .getId();
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
        }

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateCmsArticle(code, title, createTime, catalog, hit,
                userId, content, lineNo);
    }

    public void createOrUpdateCmsArticle(String code, String title,
            String createTime, String catalog, String hit, String userId,
            String content, int lineNo) {
        CmsArticle cmsArticle = cmsArticleManager.findUniqueBy("code", code);

        if (cmsArticle != null) {
            logger.info("skip exists article : {}", code);

            return;
        }

        CmsCatalog cmsCatalog = cmsCatalogManager.findUniqueBy("code", catalog);

        if (cmsCatalog == null) {
            logger.info("cannot find catalog : {}", catalog);

            return;
        }

        CmsSite cmsSite = cmsCatalog.getCmsSite();

        try {
            Date createTimeValue = new SimpleDateFormat("yyy-MM-dd")
                    .parse(createTime);
            cmsArticle = new CmsArticle();
            cmsArticle.setCode(code);
            cmsArticle.setTitle(title);
            cmsArticle.setCreateTime(createTimeValue);
            cmsArticle.setPublishTime(createTimeValue);
            cmsArticle.setCmsCatalog(cmsCatalog);
            cmsArticle.setCmsSite(cmsSite);
            cmsArticle.setHitCount(Integer.parseInt(hit));
            cmsArticle.setTenantId(defaultTenantId);
            cmsArticle.setContent(content);
            cmsArticle.setUserId(userId);
            cmsArticle.setStatus(1);
            cmsArticleManager.save(cmsArticle);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String readText(String path, String encoding) throws Exception {
        try {
            InputStream is = CmsArticleCallback.class.getClassLoader()
                    .getResourceAsStream(path);

            if (is == null) {
                logger.warn("cannot find : {}", path);

                return "";
            }

            return IOUtils.toString(is, encoding);
        } catch (Exception ex) {
            logger.info("cannot find : {}", path);
            logger.error(ex.getMessage(), ex);

            return "";
        }
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
