package com.mossle.cms.web.portal;

import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.CmsArticleManager;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cms/portal")
public class CmsPortalController {
    private static Logger logger = LoggerFactory
            .getLogger(CmsPortalController.class);
    private CmsArticleManager cmsArticleManager;
    private TenantHolder tenantHolder;

    @RequestMapping("articles")
    public String articles() throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from CmsArticle where tenantId=? order by publishTime desc";
        Page page = cmsArticleManager.pagedQuery(hql, 1, 7, tenantId);
        List<CmsArticle> cmsArticles = (List<CmsArticle>) page.getResult();

        StringBuilder buff = new StringBuilder();
        buff.append("<table class='table'>");
        buff.append("<tbody>");

        for (CmsArticle cmsArticle : cmsArticles) {
            CmsCatalog cmsCatalog = cmsArticle.getCmsCatalog();
            buff.append("<tr>");
            buff.append("  <td>" + cmsCatalog.getName() + "</td>");
            buff.append("  <td><a href='../cms/view/" + cmsCatalog.getCode()
                    + "/" + cmsArticle.getId() + "'>" + cmsArticle.getTitle()
                    + "</a></td>");
            buff.append("  <td>"
                    + new SimpleDateFormat("yyyy-MM-dd").format(cmsArticle
                            .getPublishTime()) + "</td>");
            buff.append("</tr>");
        }

        buff.append("</tbody>");
        buff.append("</table>");

        return buff.toString();
    }

    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
