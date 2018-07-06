package com.mossle.cms.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.CmsCatalogManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsCatalogDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCatalogDeployer.class);
    private CmsCatalogManager cmsCatalogManager;
    private String dataFilePath = "data/cms-catalog.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", CmsCatalogDeployer.class);

            return;
        }

        InputStream is = CmsCatalogDeployer.class.getClassLoader()
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

    public void processLine(String line, int lineNo) {
        String[] array = line.split(",");
        String name = this.processItem(array[0]);
        String code = this.processItem(array[1]);

        if (StringUtils.isBlank(name)) {
            logger.warn("name cannot be blank {} {}", lineNo, line);

            return;
        }

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, line);

            return;
        }

        name = name.trim();
        code = code.trim().toLowerCase();

        this.createOrUpdateCmsCatalog(name, code, lineNo);
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

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }
}
