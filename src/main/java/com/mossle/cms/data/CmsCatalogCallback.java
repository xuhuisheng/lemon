package com.mossle.cms.data;

import java.util.List;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsCatalogCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCatalogCallback.class);
    private CmsCatalogManager cmsCatalogManager;
    private CmsSiteManager cmsSiteManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String code = list.get(1);
        String parent = list.get(2);
        String site = list.get(3);
        String catalogTemplate = list.get(4);
        String articleTemplate = list.get(5);

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(site)) {
            site = "";
        }

        name = name.trim();
        code = code.trim().toLowerCase();

        this.createOrUpdateCmsCatalog(name, code, parent, site,
                catalogTemplate, articleTemplate, lineNo);
    }

    public void createOrUpdateCmsCatalog(String name, String code,
            String parent, String site, String catalogTemplate,
            String articleTemplate, int lineNo) {
        CmsSite cmsSite = cmsSiteManager.findUniqueBy("code", site);

        if (cmsSite == null) {
            cmsSite = new CmsSite();
            cmsSite.setCode(code);
            cmsSiteManager.save(cmsSite);
        }

        if (StringUtils.isBlank(catalogTemplate)) {
            catalogTemplate = "/default/list.html";
        }

        if (StringUtils.isBlank(articleTemplate)) {
            articleTemplate = "/default/detail.html";
        }

        CmsCatalog cmsCatalog = cmsCatalogManager.findUnique(
                "from CmsCatalog where code=? and cmsSite.id=?", code,
                cmsSite.getId());

        if (cmsCatalog == null) {
            // insert
            cmsCatalog = new CmsCatalog();
            cmsCatalog.setCode(code);
            cmsCatalog.setName(name);
            cmsCatalog.setType(0);
            cmsCatalog.setTemplateIndex("/default/index.html");
            cmsCatalog.setTemplateList(catalogTemplate);
            cmsCatalog.setTemplateDetail(articleTemplate);
            cmsCatalog.setTenantId(defaultTenantId);
            cmsCatalog.setPriority(lineNo);
            cmsCatalog.setCmsSite(cmsSite);

            if (StringUtils.isNotBlank(parent)) {
                CmsCatalog parentCatalog = this.cmsCatalogManager.findUnique(
                        "from CmsCatalog where code=? and cmsSite.id=?",
                        parent, cmsSite.getId());
                cmsCatalog.setCmsCatalog(parentCatalog);
            }

            cmsCatalogManager.save(cmsCatalog);

            return;
        }

        if (!name.equals(cmsCatalog.getName())) {
            logger.info("{} update {} to {}", code, cmsCatalog.getName(), name);
            cmsCatalog.setName(name);
            cmsCatalogManager.save(cmsCatalog);
        }
    }

    // ~
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
    }
}
