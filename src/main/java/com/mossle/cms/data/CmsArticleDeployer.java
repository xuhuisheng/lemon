package com.mossle.cms.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsArticleDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(CmsArticleDeployer.class);
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private UserConnector userConnector;
    private String dataFilePath = "data/cms-article.csv";
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

        InputStream is = CmsArticleDeployer.class.getClassLoader()
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
        String title = this.processItem(array[1]);
        String createTime = this.processItem(array[2]);
        String catalog = this.processItem(array[3]);
        String hit = this.processItem(array[4]);
        String author = this.processItem(array[5]);
        String content = this.readText("data/cms/" + code + ".txt",
                dataFileEncoding);
        String userId = userConnector.findByUsername(author, defaultTenantId)
                .getId();

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, line);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateCmsArticle(code, title, createTime, catalog, hit,
                userId, content, lineNo);
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
        }

        try {
            Date createTimeValue = new SimpleDateFormat("yyy-MM-dd")
                    .parse(createTime);
            cmsArticle = new CmsArticle();
            cmsArticle.setCode(code);
            cmsArticle.setTitle(title);
            cmsArticle.setCreateTime(createTimeValue);
            cmsArticle.setPublishTime(createTimeValue);
            cmsArticle.setCmsCatalog(cmsCatalog);
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

    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
