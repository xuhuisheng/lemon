package com.mossle.cms.data;

import java.util.List;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsAttr;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsAttrManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsAttrCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsAttrCallback.class);
    private CmsArticleManager cmsArticleManager;
    private CmsAttrManager cmsAttrManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String articleCode = list.get(0);
        String catalog = list.get(1);
        int rowIndex = Integer.parseInt(list.get(2));
        String code = list.get(3);
        String name = list.get(4);
        String value = list.get(5);

        if (StringUtils.isBlank(articleCode)) {
            logger.warn("articleCode cannot be blank {} {}", lineNo, list);

            return;
        }

        articleCode = articleCode.trim().toLowerCase();

        this.createOrUpdateCmsAttr(articleCode, catalog, rowIndex, code, name,
                value, lineNo);
    }

    public void createOrUpdateCmsAttr(String articleCode, String catalog,
            int rowIndex, String code, String name, String value, int lineNo) {
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

        String hql = "from CmsAttr where cmsArticle.id=? and catalog=? and rowIndex=? and code=?";
        CmsAttr cmsAttr = cmsAttrManager.findUnique(hql, cmsSite.getId(),
                catalog, rowIndex, code);

        if (cmsAttr != null) {
            return;
        }

        cmsAttr = new CmsAttr();
        cmsAttr.setCatalog(catalog);
        cmsAttr.setRowIndex(rowIndex);
        cmsAttr.setCode(code);
        cmsAttr.setName(name);
        cmsAttr.setValue(value);
        cmsAttr.setPriority(lineNo);
        cmsAttr.setCmsArticle(cmsArticle);
        cmsAttrManager.save(cmsAttr);
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    public void setCmsAttrManager(CmsAttrManager cmsAttrManager) {
        this.cmsAttrManager = cmsAttrManager;
    }
}
