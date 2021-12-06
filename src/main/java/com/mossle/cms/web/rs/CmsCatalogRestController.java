package com.mossle.cms.web.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.service.CmsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cms/rs")
public class CmsCatalogRestController {
    private static Logger logger = LoggerFactory
            .getLogger(CmsCatalogRestController.class);
    private CmsCatalogManager cmsCatalogManager;
    private CmsService cmsService;

    @RequestMapping("catalog/tree")
    public List<Map> catalogTree(
            @CookieValue(value = "currentSiteId", required = false) Long currentSiteId)
            throws Exception {
        List<CmsCatalog> cmsCatalogs = cmsService.findTopCatalogs(cmsService
                .findCurrentSite(currentSiteId));

        return this.generateCatalogs(cmsCatalogs);
    }

    public List<Map> generateCatalogs(List<CmsCatalog> cmsCatalogs) {
        if (cmsCatalogs == null) {
            return null;
        }

        List<Map> list = new ArrayList<Map>();

        try {
            for (CmsCatalog cmsCatalog : cmsCatalogs) {
                list.add(this.generateCatalog(cmsCatalog));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return list;
    }

    public Map<String, Object> generateCatalog(CmsCatalog cmsCatalog) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            map.put("id", cmsCatalog.getId());
            map.put("name", cmsCatalog.getName());

            String hql = "from CmsCatalog where cmsCatalog=? order by priority";
            List<CmsCatalog> children = this.cmsCatalogManager.find(hql,
                    cmsCatalog);

            if (children.isEmpty()) {
                map.put("open", false);
            } else {
                map.put("open", true);
                map.put("children", this.generateCatalogs(children));
            }

            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return map;
        }
    }

    // ~
    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }
}
