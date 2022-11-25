package com.mossle.cms.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.client.user.UserClient;

import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsAttrManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsCommentManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.persistence.manager.CmsTagArticleManager;
import com.mossle.cms.persistence.manager.CmsTagManager;

import com.mossle.core.csv.CsvProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.cms.data.CmsDeployer")
public class CmsDeployer {
    private static Logger logger = LoggerFactory.getLogger(CmsDeployer.class);
    private CmsSiteManager cmsSiteManager;
    private CmsCatalogManager cmsCatalogManager;
    private CmsArticleManager cmsArticleManager;
    private CmsCommentManager cmsCommentManager;
    private CmsTagManager cmsTagManager;
    private CmsTagArticleManager cmsTagArticleManager;
    private CmsAttrManager cmsAttrManager;
    private UserClient userClient;
    private String siteDataFilePath = "data/cms/cms-site.csv";
    private String catalogDataFilePath = "data/cms/cms-catalog.csv";
    private String articleDataFilePath = "data/cms/cms-article.csv";
    private String commentDataFilePath = "data/cms/cms-comment.csv";
    private String tagDataFilePath = "data/cms/cms-tag.csv";
    private String attrDataFilePath = "data/cms/cms-attr.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip cms data init");

            return;
        }

        logger.info("start cms data init");

        // site
        CmsSiteCallback cmsSiteCallback = new CmsSiteCallback();
        cmsSiteCallback.setCmsSiteManager(cmsSiteManager);
        new CsvProcessor().process(siteDataFilePath, dataFileEncoding,
                cmsSiteCallback);

        // catalog
        CmsCatalogCallback cmsCatalogCallback = new CmsCatalogCallback();
        cmsCatalogCallback.setCmsCatalogManager(cmsCatalogManager);
        cmsCatalogCallback.setCmsSiteManager(cmsSiteManager);
        new CsvProcessor().process(catalogDataFilePath, dataFileEncoding,
                cmsCatalogCallback);

        // article
        CmsArticleCallback cmsArticleCallback = new CmsArticleCallback();
        cmsArticleCallback.setCmsArticleManager(cmsArticleManager);
        cmsArticleCallback.setCmsCatalogManager(cmsCatalogManager);
        cmsArticleCallback.setUserClient(userClient);
        new CsvProcessor().process(articleDataFilePath, dataFileEncoding,
                cmsArticleCallback);

        // article attribute
        CmsArticleAttributeCallback cmsArticleAttributeCallback = new CmsArticleAttributeCallback();
        cmsArticleAttributeCallback.setCmsArticleManager(cmsArticleManager);
        cmsArticleAttributeCallback.setDefaultTenantId(defaultTenantId);
        cmsArticleAttributeCallback.process();

        // tag
        CmsTagCallback cmsTagCallback = new CmsTagCallback();
        cmsTagCallback.setCmsArticleManager(cmsArticleManager);
        cmsTagCallback.setCmsTagArticleManager(cmsTagArticleManager);
        cmsTagCallback.setCmsTagManager(cmsTagManager);
        new CsvProcessor().process(tagDataFilePath, dataFileEncoding,
                cmsTagCallback);

        // attr
        CmsAttrCallback cmsAttrCallback = new CmsAttrCallback();
        cmsAttrCallback.setCmsArticleManager(cmsArticleManager);
        cmsAttrCallback.setCmsAttrManager(cmsAttrManager);
        new CsvProcessor().process(attrDataFilePath, dataFileEncoding,
                cmsAttrCallback);

        // comment
        CmsCommentCallback cmsCommentCallback = new CmsCommentCallback();
        cmsCommentCallback.setCmsCommentManager(cmsCommentManager);
        cmsCommentCallback.setCmsArticleManager(cmsArticleManager);
        cmsCommentCallback.setUserClient(userClient);
        new CsvProcessor().process(commentDataFilePath, dataFileEncoding,
                cmsCommentCallback);
        logger.info("end cms data init");
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
    public void setCmsTagManager(CmsTagManager cmsTagManager) {
        this.cmsTagManager = cmsTagManager;
    }

    @Resource
    public void setCmsTagArticleManager(
            CmsTagArticleManager cmsTagArticleManager) {
        this.cmsTagArticleManager = cmsTagArticleManager;
    }

    @Resource
    public void setCmsAttrManager(CmsAttrManager cmsAttrManager) {
        this.cmsAttrManager = cmsAttrManager;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Value("${cms.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
