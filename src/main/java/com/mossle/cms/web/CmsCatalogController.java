package com.mossle.cms.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.cms.persistence.domain.CmsCatalog;
import com.mossle.cms.persistence.domain.CmsSite;
import com.mossle.cms.persistence.manager.CmsArticleManager;
import com.mossle.cms.persistence.manager.CmsCatalogManager;
import com.mossle.cms.persistence.manager.CmsSiteManager;
import com.mossle.cms.service.CmsService;
import com.mossle.cms.service.RenderService;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cms")
public class CmsCatalogController {
    private CmsCatalogManager cmsCatalogManager;
    private CmsArticleManager cmsArticleManager;
    private CmsSiteManager cmsSiteManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private RenderService renderService;
    private CmsService cmsService;

    @RequestMapping("cms-catalog-list")
    public String list(
            @ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            @CookieValue(value = "currentSiteId", required = false) Long currentSiteId,
            Model model) {
        model.addAttribute("cmsSites", cmsSiteManager.getAll("id", true));

        CmsSite cmsSite = this.cmsService.findCurrentSite(currentSiteId);

        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQL_cmsSite.id", Long
                .toString(cmsSite.getId())));
        page = cmsCatalogManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-catalog-list";
    }

    @RequestMapping("cms-catalog-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        model.addAttribute("cmsSites", cmsSiteManager.getAll("id", true));

        if (id != null) {
            CmsCatalog cmsCatalog = cmsCatalogManager.get(id);
            model.addAttribute("model", cmsCatalog);
        }

        return "cms/cms-catalog-input";
    }

    @RequestMapping("cms-catalog-save")
    public String save(@ModelAttribute CmsCatalog cmsCatalog,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = cmsCatalog.getId();
        CmsCatalog dest = null;

        if (id != null) {
            dest = cmsCatalogManager.get(id);
            beanMapper.copy(cmsCatalog, dest);
        } else {
            dest = cmsCatalog;
            dest.setTenantId(tenantId);
        }

        cmsCatalogManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-catalog-list.do";
    }

    @RequestMapping("cms-catalog-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsCatalog> cmsCatalogs = cmsCatalogManager
                .findByIds(selectedItem);
        cmsCatalogManager.removeAll(cmsCatalogs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-catalog-list.do";
    }

    @RequestMapping("cms-catalog-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = cmsCatalogManager.pagedQuery(page, propertyFilters);

        List<CmsCatalog> cmsCatalogs = (List<CmsCatalog>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cmsCatalog");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsCatalogs);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("cms-catalog-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from CmsCatalog where name=? and tenantId=?";
        Object[] params = { name, tenantId };

        if (id != null) {
            hql = "from CmsCatalog where name=? and tenantId=? and id<>?";
            params = new Object[] { name, tenantId, id };
        }

        CmsCatalog cmsCatalog = cmsCatalogManager.findUnique(hql, params);

        boolean result = (cmsCatalog == null);

        return result;
    }

    @RequestMapping("cms-catalog-view")
    public String view(@RequestParam("id") Long id, @ModelAttribute Page page,
            Model model) {
        List<CmsCatalog> cmsCatalogs = this.cmsCatalogManager.getAll();
        CmsCatalog cmsCatalog = cmsCatalogManager.get(id);
        String hql = "from CmsArticle where cmsCatalog.id=? order by createTime desc";
        page = this.cmsArticleManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), id);

        String html = renderService.viewCatalog(cmsCatalog, page, cmsCatalogs);

        model.addAttribute("html", html);

        return "cms/cms-catalog-view";
    }

    // ~ ======================================================================
    @Resource
    public void setCmsCatalogManager(CmsCatalogManager cmsCatalogManager) {
        this.cmsCatalogManager = cmsCatalogManager;
    }

    @Resource
    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    @Resource
    public void setCmsSiteManager(CmsSiteManager cmsSiteManager) {
        this.cmsSiteManager = cmsSiteManager;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    @Resource
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }
}
