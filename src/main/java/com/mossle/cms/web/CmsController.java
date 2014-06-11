package com.mossle.cms.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.cms.domain.CmsArticle;
import com.mossle.cms.domain.CmsCatalog;
import com.mossle.cms.manager.CmsArticleManager;
import com.mossle.cms.manager.CmsCatalogManager;
import com.mossle.cms.service.RenderService;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cms")
public class CmsController {
    private CmsArticleManager cmsArticleManager;
    private CmsCatalogManager cmsCatalogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private RenderService renderService;
    private StoreConnector storeConnector;

    @RequestMapping("index")
    public String index(Model model) {
        List<CmsCatalog> cmsCatalogs = cmsCatalogManager.getAll();
        model.addAttribute("cmsCatalogs", cmsCatalogs);

        return "cms/index";
    }

    @RequestMapping("catalog")
    public String catalog(@RequestParam("id") Long id, Model model) {
        CmsCatalog cmsCatalog = cmsCatalogManager.get(id);
        model.addAttribute("cmsCatalog", cmsCatalog);

        return "cms/catalog";
    }

    @RequestMapping("article")
    public String article(@RequestParam("id") Long id, Model model) {
        CmsArticle cmsArticle = cmsArticleManager.get(id);
        model.addAttribute("cmsArticle", cmsArticle);

        return "cms/article";
    }

    // ~ ======================================================================
    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
