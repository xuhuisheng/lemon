package com.mossle.cms.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsArticle;
import com.mossle.cms.persistence.manager.CmsArticleManager;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("cms/widget")
public class CmsWidgetResource {
    private static Logger logger = LoggerFactory
            .getLogger(CmsWidgetResource.class);
    private CmsArticleManager cmsArticleManager;
    private TenantHolder tenantHolder;

    @GET
    @Path("articles")
    @Produces(MediaType.TEXT_HTML)
    public String articles() throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from CmsArticle where tenantId=? order by createTime desc";
        List<CmsArticle> cmsArticles = cmsArticleManager.find(hql, tenantId);

        StringBuilder buff = new StringBuilder();
        buff.append("<marquee direction='up' scrollamount='2'>");

        for (CmsArticle cmsArticle : cmsArticles) {
            buff.append("<div>");
            buff.append("  <h4><a href='../cms/cms-article-view.do?id="
                    + cmsArticle.getId() + "'>" + cmsArticle.getTitle()
                    + "</h4>");
            buff.append("  <p>" + cmsArticle.getContent() + "</p>");
            buff.append("</div>");
        }

        buff.append("</marquee>");

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
