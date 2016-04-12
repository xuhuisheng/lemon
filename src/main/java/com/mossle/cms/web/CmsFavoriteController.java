package com.mossle.cms.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.cms.persistence.domain.CmsFavorite;
import com.mossle.cms.persistence.manager.CmsFavoriteManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cms")
public class CmsFavoriteController {
    private CmsFavoriteManager cmsFavoriteManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("cms-favorite-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsFavoriteManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "cms/cms-favorite-list";
    }

    @RequestMapping("cms-favorite-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CmsFavorite cmsFavorite = cmsFavoriteManager.get(id);
            model.addAttribute("model", cmsFavorite);
        }

        return "cms/cms-favorite-input";
    }

    @RequestMapping("cms-favorite-save")
    public String save(@ModelAttribute CmsFavorite cmsFavorite,
            RedirectAttributes redirectAttributes) {
        Long id = cmsFavorite.getId();
        CmsFavorite dest = null;

        if (id != null) {
            dest = cmsFavoriteManager.get(id);
            beanMapper.copy(cmsFavorite, dest);
        } else {
            dest = cmsFavorite;
        }

        cmsFavoriteManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/cms/cms-favorite-list.do";
    }

    @RequestMapping("cms-favorite-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CmsFavorite> cmsFavorites = cmsFavoriteManager
                .findByIds(selectedItem);
        cmsFavoriteManager.removeAll(cmsFavorites);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/cms/cms-favorite-list.do";
    }

    @RequestMapping("cms-favorite-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = cmsFavoriteManager.pagedQuery(page, propertyFilters);

        List<CmsFavorite> cmsFavorites = (List<CmsFavorite>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("cmsFavorite");
        tableModel.addHeaders("id", "name");
        tableModel.setData(cmsFavorites);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("cms-favorite-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from CmsFavorite where name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from CmsFavorite where name=? and id<>?";
            params = new Object[] { name, id };
        }

        CmsFavorite cmsFavorite = cmsFavoriteManager.findUnique(hql, params);

        boolean result = (cmsFavorite == null);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setCmsFavoriteManager(CmsFavoriteManager cmsFavoriteManager) {
        this.cmsFavoriteManager = cmsFavoriteManager;
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
