package com.mossle.cms.data;

import java.util.List;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.CmsCatalogManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsCatalogCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCatalogCallback.class);
    private CmsCatalogManager cmsCatalogManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String code = list.get(1);

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        name = name.trim();
        code = code.trim().toLowerCase();

        this.createOrUpdateCmsCatalog(name, code, lineNo);
    }

    public void createOrUpdateCmsCatalog(String name, String code, int lineNo) {
        CmsCatalog cmsCatalog = cmsCatalogManager.findUniqueBy("code", code);

        if (cmsCatalog == null) {
            // insert
            cmsCatalog = new CmsCatalog();
            cmsCatalog.setCode(code);
            cmsCatalog.setName(name);
            cmsCatalog.setType(0);
            cmsCatalog.setTemplateIndex("/default/index.html");
            cmsCatalog.setTemplateList("/default/list.html");
            cmsCatalog.setTemplateDetail("/default/detail.html");
            cmsCatalog.setTenantId(defaultTenantId);
            cmsCatalog.setPriority(lineNo);
            cmsCatalogManager.save(cmsCatalog);

            return;
        }

        if (!name.equals(cmsCatalog.getName())) {
            logger.info("{} update {} to {}", code, cmsCatalog.getName(), name);
            cmsCatalog.setName(name);
            cmsCatalogManager.save(cmsCatalog);
        }
    }

    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }
}
