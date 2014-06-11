package com.mossle.cms.service;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.cms.domain.CmsArticle;
import com.mossle.cms.domain.CmsCatalog;

import com.mossle.ext.template.TemplateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
public class RenderService {
    private static Logger logger = LoggerFactory.getLogger(RenderService.class);
    private TemplateService templateService;
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
        Map<String, Object> data = new HashMap<String, Object>();
        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        data.put("article", cmsArticle);
        data.put("catalog", cmsCatalog);

        return templateService.render(cmsCatalog.getTemplateDetail(), data);
    }

    @Resource
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
