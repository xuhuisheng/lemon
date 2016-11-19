package com.mossle.cms.service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;

import com.mossle.core.template.TemplateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.util.Assert;

@Service
public class RenderService {
    private static Logger logger = LoggerFactory.getLogger(RenderService.class);
    private TemplateService templateService;
    private UserConnector userConnector;
    private String baseDir;

    public void render(CmsArticle cmsArticle) {
        this.renderDetail(cmsArticle);
        this.renderIndex(cmsArticle.getCmsCatalog());
    }

    public void renderDetail(CmsArticle cmsArticle) {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
            data.put("article", cmsArticle);
            data.put("catalog", cmsCatalog);

            String html = templateService.render(
                    cmsCatalog.getTemplateDetail(), data);
            String path = baseDir + "/cms/html/" + cmsArticle.getId() + ".html";
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "UTF-8"));
            writer.print(html);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void renderIndex(CmsCatalog cmsCatalog) {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("catalog", cmsCatalog);
            data.put("articles", cmsCatalog.getCmsArticles());

            String html = templateService.render(cmsCatalog.getTemplateIndex(),
                    data);
            String path = baseDir + "/cms/html/index.html";
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "UTF-8"));
            writer.print(html);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String view(CmsArticle cmsArticle) {
        Assert.notNull(cmsArticle, "cmsArticle must not null");

        Map<String, Object> data = new HashMap<String, Object>();
        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        data.put("article", cmsArticle);
        data.put("catalog", cmsCatalog);
        data.put("userConnector", userConnector);

        return templateService.render(cmsCatalog.getTemplateDetail(), data);
    }

    // ~ ==================================================
    public String viewIndex(List<CmsCatalog> cmsCatalogs) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("catalogs", cmsCatalogs);

        String html = templateService.render("/default/index.html", data);

        return html;
    }

    public String viewCatalog(CmsCatalog cmsCatalog) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("catalog", cmsCatalog);
        data.put("articles", cmsCatalog.getCmsArticles());

        String html = templateService
                .render(cmsCatalog.getTemplateList(), data);

        return html;
    }

    public String viewArticle(CmsArticle cmsArticle) {
        Map<String, Object> data = new HashMap<String, Object>();
        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        data.put("article", cmsArticle);
        data.put("catalog", cmsCatalog);
        data.put("userConnector", userConnector);

        return templateService.render(cmsCatalog.getTemplateDetail(), data);
    }

    // ~ ==================================================
    @Resource
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
