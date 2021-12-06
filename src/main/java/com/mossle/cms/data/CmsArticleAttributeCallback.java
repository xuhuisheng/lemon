package com.mossle.cms.data;

import java.io.InputStream;

import java.util.List;
import java.util.Map;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.manager.CmsArticleManager;

import com.mossle.core.mapper.JsonMapper;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsArticleAttributeCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsArticleAttributeCallback.class);
    private CmsArticleManager cmsArticleManager;
    private String defaultTenantId = "1";
    private String filePath = "data/cms-article-attribute.json";
    private String encoding = "utf-8";
    private JsonMapper jsonMapper = new JsonMapper();

    public void process() throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        InputStream is = CmsArticleAttributeCallback.class.getClassLoader()
                .getResourceAsStream(filePath);

        if (is == null) {
            logger.info("skip : {}", filePath);

            return;
        }

        String text = IOUtils.toString(is, encoding);

        List<Map<String, String>> list = jsonMapper.fromJson(text, List.class);

        for (Map<String, String> map : list) {
            String code = map.get("code");
            CmsArticle cmsArticle = cmsArticleManager
                    .findUniqueBy("code", code);

            if (cmsArticle == null) {
                logger.info("skip article : {}", code);

                continue;
            }

            String source = map.get("source");

            if (source != null) {
                cmsArticle.setSource(source);
            }

            String logo = map.get("logo");

            if (logo != null) {
                cmsArticle.setLogo(logo);
            }

            String summary = map.get("summary");

            if (summary != null) {
                cmsArticle.setSummary(summary);
            }

            cmsArticleManager.save(cmsArticle);
        }
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
