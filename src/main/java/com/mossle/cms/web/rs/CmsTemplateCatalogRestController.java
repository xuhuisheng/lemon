package com.mossle.cms.web.rs;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsTemplateCatalog;
import com.mossle.cms.persistence.domain.CmsTemplateContent;
import com.mossle.cms.persistence.manager.CmsTemplateCatalogManager;
import com.mossle.cms.persistence.manager.CmsTemplateContentManager;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cms/rs/template")
public class CmsTemplateCatalogRestController {
    private static Logger logger = LoggerFactory
            .getLogger(CmsTemplateCatalogRestController.class);
    private CmsTemplateCatalogManager cmsTemplateCatalogManager;
    private CmsTemplateContentManager cmsTemplateContentManager;
    private TenantHolder tenantHolder;

    @RequestMapping("tree")
    public List<Map> tree() throws Exception {
        String hql = "from CmsTemplateCatalog where cmsTemplateCatalog=null order by priority";
        List<CmsTemplateCatalog> cmsTemplateCatalogs = cmsTemplateCatalogManager
                .find(hql);

        if (cmsTemplateCatalogs.isEmpty()) {
            CmsTemplateCatalog cmsTemplateCatalog = new CmsTemplateCatalog();
            cmsTemplateCatalog.setName("/");
            cmsTemplateCatalogManager.save(cmsTemplateCatalog);
            cmsTemplateCatalogs.add(cmsTemplateCatalog);
        }

        return this.generateTemplateCatalogs(cmsTemplateCatalogs, null);
    }

    public List<Map> generateTemplateCatalogs(
            List<CmsTemplateCatalog> cmsTemplateCatalogs,
            List<CmsTemplateContent> cmsTemplateContents) {
        if ((cmsTemplateCatalogs == null) && (cmsTemplateContents == null)) {
            return null;
        }

        List<Map> list = new ArrayList<Map>();

        try {
            if (cmsTemplateCatalogs != null) {
                for (CmsTemplateCatalog cmsTemplateCatalog : cmsTemplateCatalogs) {
                    list.add(this.generateTemplateCatalog(cmsTemplateCatalog));
                }
            }

            if (cmsTemplateContents != null) {
                for (CmsTemplateContent cmsTemplateContent : cmsTemplateContents) {
                    list.add(this.generateTemplateContent(cmsTemplateContent));
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return list;
    }

    public Map<String, Object> generateTemplateCatalog(
            CmsTemplateCatalog cmsTemplateCatalog) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            map.put("id", cmsTemplateCatalog.getId());
            map.put("name", cmsTemplateCatalog.getName());
            map.put("type", "catalog");

            String hql = "from CmsTemplateCatalog where cmsTemplateCatalog=? order by priority";
            List<CmsTemplateCatalog> children = this.cmsTemplateCatalogManager
                    .find(hql, cmsTemplateCatalog);

            List<CmsTemplateContent> contents = this.cmsTemplateContentManager
                    .find("from CmsTemplateContent where cmsTemplateCatalog=? order by priority",
                            cmsTemplateCatalog);

            if (children.isEmpty() && contents.isEmpty()) {
                map.put("open", false);
            } else {
                map.put("open", true);
                map.put("children",
                        this.generateTemplateCatalogs(children, contents));
            }

            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return map;
        }
    }

    public Map<String, Object> generateTemplateContent(
            CmsTemplateContent cmsTemplateContent) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            map.put("id", cmsTemplateContent.getId());
            map.put("name", cmsTemplateContent.getName());
            map.put("type", "content");
            map.put("open", false);
            map.put("categoryId", cmsTemplateContent.getCmsTemplateCatalog()
                    .getId());

            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return map;
        }
    }

    @Resource
    public void setCmsTemplateCatalogManager(
            CmsTemplateCatalogManager cmsTemplateCatalogManager) {
        this.cmsTemplateCatalogManager = cmsTemplateCatalogManager;
    }

    @Resource
    public void setCmsTemplateContentManager(
            CmsTemplateContentManager cmsTemplateContentManager) {
        this.cmsTemplateContentManager = cmsTemplateContentManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
