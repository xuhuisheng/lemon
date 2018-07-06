package com.mossle.cms.service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.manager.CmsCatalogManager;

import com.mossle.core.page.Page;
import com.mossle.core.template.TemplateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.util.Assert;

@Service
public class CmsService {
    private static Logger logger = LoggerFactory.getLogger(CmsService.class);
    private CmsCatalogManager cmsCatalogManager;

    public List<CmsCatalog> getTopCatalogs() {
        String hql = "from CmsCatalog where cmsCatalog=null order by priority";
        List<CmsCatalog> cmsCatalogs = this.cmsCatalogManager.find(hql);

        return cmsCatalogs;
    }

    // ~ ==================================================
    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }
}
