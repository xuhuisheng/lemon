package com.mossle.cms.service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.api.user.UserConnector;

import com.mossle.client.user.UserClient;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsTemplateContent;
import com.mossle.cms.persistence.manager.CmsTemplateContentManager;
import com.mossle.cms.service.CmsService;
import com.mossle.cms.support.CmsHelper;

import com.mossle.core.page.Page;
import com.mossle.core.template.TemplateService;

import org.apache.commons.lang3.StringUtils;

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
    private CmsTemplateContentManager cmsTemplateContentManager;
    private CmsService cmsService;
    private UserClient userClient;

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

    public String view(CmsArticle cmsArticle, List<CmsCatalog> cmsCatalogs,
            Page page) {
        Assert.notNull(cmsArticle, "cmsArticle must not null");

        Map<String, Object> data = new HashMap<String, Object>();
        CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
        data.put("article", cmsArticle);
        data.put("catalog", cmsCatalog);
        data.put("userConnector", userConnector);
        data.put("catalogs", cmsCatalogs);
        data.put("page", page);

        return templateService.render(cmsCatalog.getTemplateDetail(), data);
    }

    // ~ ==================================================
    public String viewIndex(List<CmsCatalog> cmsCatalogs) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("catalogs", cmsCatalogs);

        String html = templateService.render("/default/index.html", data);

        return html;
    }

    public String viewCatalog(CmsCatalog cmsCatalog, Page page,
            List<CmsCatalog> cmsCatalogs) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("catalog", cmsCatalog);
        data.put("userConnector", userConnector);
        data.put("page", page);
        data.put("catalogs", cmsCatalogs);

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

    public String viewSite(List<CmsCatalog> cmsCatalogs) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("catalogs", cmsCatalogs);
        data.put("userConnector", userConnector);

        // data.put("cmsHelper", cmsHelper);
        String html = templateService.render("/default/index.html", data);

        return html;
    }

    public String renderText(String templateCode, String ctx) {
        CmsHelper cmsHelper = new CmsHelper();
        cmsHelper.setCmsService(cmsService);
        cmsHelper.setUserClient(userClient);
        cmsHelper.setCtx(ctx);

        return this.renderText(templateCode, cmsHelper);
    }

    public String renderText(String templateCode, String ctx,
            CmsCatalog cmsCatalog, int pageNo, int pageSize) {
        CmsHelper cmsHelper = new CmsHelper();
        cmsHelper.setCmsService(cmsService);
        cmsHelper.setUserClient(userClient);
        cmsHelper.setCtx(ctx);
        cmsHelper.setCatalog(cmsCatalog);
        cmsHelper.setPageNo(pageNo);
        cmsHelper.setPageSize(pageSize);

        return this.renderText(templateCode, cmsHelper);
    }

    public String renderText(String templateCode, String ctx,
            CmsArticle cmsArticle, int pageNo, int pageSize) {
        CmsHelper cmsHelper = new CmsHelper();
        cmsHelper.setCmsService(cmsService);
        cmsHelper.setUserClient(userClient);
        cmsHelper.setCtx(ctx);
        cmsHelper.setCatalog(cmsArticle.getCmsCatalog());
        cmsHelper.setArticle(cmsArticle);
        cmsHelper.setPageNo(pageNo);
        cmsHelper.setPageSize(pageSize);

        return this.renderText(templateCode, cmsHelper);
    }

    public String renderText(String templateCode, CmsHelper cmsHelper) {
        if (StringUtils.isBlank(templateCode)) {
            logger.error("template code cannot blank");

            return "";
        }

        if (templateCode.startsWith("/")) {
            templateCode = templateCode.substring(1);
        }

        CmsTemplateContent cmsTemplateContent = cmsTemplateContentManager
                .findUniqueBy("path", templateCode);

        if (cmsTemplateContent == null) {
            logger.info("cannot find template : {}", templateCode);

            return "";
        }

        String content = cmsTemplateContent.getContent();

        if (StringUtils.isBlank(content)) {
            logger.info("template content is blank : {}", templateCode);

            return "";
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("cms", cmsHelper);

        String html = templateService.renderText(content, data);

        return html;
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

    @Resource
    public void setCmsTemplateContentManager(
            CmsTemplateContentManager cmsTemplateContentManager) {
        this.cmsTemplateContentManager = cmsTemplateContentManager;
    }

    @Resource
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
