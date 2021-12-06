package com.mossle.cms.data;

import java.util.List;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.domain.CmsTag;
import com.mossle.cms.persistence.domain.CmsTagArticle;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsTagArticleManager;
import com.mossle.cms.persistence.manager.CmsTagManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsTagCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsTagCallback.class);
    private CmsArticleManager cmsArticleManager;
    private CmsTagManager cmsTagManager;
    private CmsTagArticleManager cmsTagArticleManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String articleCode = list.get(0);
        String code = list.get(1);
        String name = list.get(2);
        String type = list.get(3);

        if (StringUtils.isBlank(articleCode)) {
            logger.warn("articleCode cannot be blank {} {}", lineNo, list);

            return;
        }

        articleCode = articleCode.trim().toLowerCase();

        this.createOrUpdateCmsTag(articleCode, code, name, type, lineNo);
    }

    public void createOrUpdateCmsTag(String articleCode, String code,
            String name, String type, int lineNo) {
        CmsArticle cmsArticle = cmsArticleManager.findUniqueBy("code",
                articleCode);

        if (cmsArticle == null) {
            logger.info("cannot find article : {}", articleCode);

            return;
        }

        CmsSite cmsSite = cmsArticle.getCmsSite();

        if (cmsSite == null) {
            logger.info("cannot find site : {}", articleCode);
        }

        CmsTag cmsTag = cmsTagManager.findUnique(
                "from CmsTag where cmsSite.id=? and code=?", cmsSite.getId(),
                code);

        if (cmsTag == null) {
            cmsTag = new CmsTag();
            cmsTag.setCode(code);
            cmsTag.setName(name);
            cmsTag.setCmsSite(cmsSite);
            cmsTagManager.save(cmsTag);
        }

        CmsTagArticle cmsTagArticle = cmsTagArticleManager.findUnique(
                "from CmsTagArticle where cmsTag.id=? and cmsArticle.id=?",
                cmsTag.getId(), cmsArticle.getId());

        if (cmsTagArticle != null) {
            return;
        }

        try {
            cmsTagArticle = new CmsTagArticle();
            cmsTagArticle.setType(type);
            cmsTagArticle.setCmsTag(cmsTag);
            cmsTagArticle.setCmsArticle(cmsArticle);
            cmsTagArticle.setPriority(lineNo);
            cmsTagArticleManager.save(cmsTagArticle);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    public void setCmsTagManager(CmsTagManager cmsTagManager) {
        this.cmsTagManager = cmsTagManager;
    }

    public void setCmsTagArticleManager(
            CmsTagArticleManager cmsTagArticleManager) {
        this.cmsTagArticleManager = cmsTagArticleManager;
    }
}
