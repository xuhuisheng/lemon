package com.mossle.cms.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;

import com.mossle.core.csv.CsvProcessor;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsDeployer {
    private static Logger logger = LoggerFactory.getLogger(CmsDeployer.class);
    private CmsSiteManager cmsSiteManager;
    private CmsCatalogManager cmsCatalogManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCommentManager cmsCommentManager;
    private UserConnector userConnector;
    private String siteDataFilePath = "data/cms-site.csv";
    private String catalogDataFilePath = "data/cms-catalog.csv";
    private String articleDataFilePath = "data/cms-article.csv";
    private String commentDataFilePath = "data/cms-comment.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", CmsDeployer.class);

            return;
        }

        // site
        CmsSiteCallback cmsSiteCallback = new CmsSiteCallback();
        cmsSiteCallback.setCmsSiteManager(cmsSiteManager);
        new CsvProcessor().process(siteDataFilePath, dataFileEncoding,
                cmsSiteCallback);

        // catalog
        CmsCatalogCallback cmsCatalogCallback = new CmsCatalogCallback();
        cmsCatalogCallback.setCmsCatalogManager(cmsCatalogManager);
        new CsvProcessor().process(catalogDataFilePath, dataFileEncoding,
                cmsCatalogCallback);

        // article
        CmsArticleCallback cmsArticleCallback = new CmsArticleCallback();
        cmsArticleCallback.setCmsArticleManager(cmsArticleManager);
        cmsArticleCallback.setCmsCatalogManager(cmsCatalogManager);
        cmsArticleCallback.setUserConnector(userConnector);
        new CsvProcessor().process(articleDataFilePath, dataFileEncoding,
                cmsArticleCallback);

        // article attribute
        CmsArticleAttributeCallback cmsArticleAttributeCallback = new CmsArticleAttributeCallback();
        cmsArticleAttributeCallback.setCmsArticleManager(cmsArticleManager);
        cmsArticleAttributeCallback.process();

        // comment
        CmsCommentCallback cmsCommentCallback = new CmsCommentCallback();
        cmsCommentCallback.setCmsCommentManager(cmsCommentManager);
        cmsCommentCallback.setCmsArticleManager(cmsArticleManager);
        cmsCommentCallback.setUserConnector(userConnector);
        new CsvProcessor().process(commentDataFilePath, dataFileEncoding,
                cmsCommentCallback);
    }

    @Resource
    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
    }

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
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
