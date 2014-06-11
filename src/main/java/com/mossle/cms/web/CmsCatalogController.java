package com.mossle.cms.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.cms.domain.CmsCatalog;
import com.mossle.cms.manager.CmsCatalogManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cms")
public class CmsCatalogController {
    private CmsCatalogManager cmsCatalogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("cms-catalog-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsCatalogManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-catalog-list";
    }

    @RequestMapping("cms-catalog-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsCatalog cmsCatalog = cmsCatalogManager.get(id);
            model.addAttribute("model", cmsCatalog);
        }

        return "cms/cms-catalog-input";
    }

    @RequestMapping("cms-catalog-save")
    public String save(@ModelAttribute CmsCatalog cmsCatalog,
            RedirectAttributes redirectAttributes) {
        Long id = cmsCatalog.getId();
        CmsCatalog dest = null;

        if (id != null) {
            dest = cmsCatalogManager.get(id);
            beanMapper.copy(cmsCatalog, dest);
        } else {
            dest = cmsCatalog;
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
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsCatalogManager.pagedQuery(page, propertyFilters);

        List<CmsCatalog> cmsCatalogs = (List<CmsCatalog>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cmsCatalog");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsCatalogs);
        exportor.export(response, tableModel);
    }

    @RequestMapping("cms-catalog-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from CmsCatalog where name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from CmsCatalog where name=? and id<>?";
            params = new Object[] { name, id };
        }

        CmsCatalog cmsCatalog = cmsCatalogManager.findUnique(hql, params);

        boolean result = (cmsCatalog == null);

        return result;
    }

    // ~ ======================================================================
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
}
