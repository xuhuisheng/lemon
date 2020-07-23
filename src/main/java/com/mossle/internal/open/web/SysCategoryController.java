package com.mossle.internal.open.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.open.persistence.domain.SysCategory;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sys")
public class SysCategoryController {
    private SysCategoryManager sysCategoryManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("sys-category-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sysCategoryManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "sys/sys-category-list";
    }

    @RequestMapping("sys-category-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SysCategory sysCategory = sysCategoryManager.get(id);
            model.addAttribute("model", sysCategory);
        }

        return "sys/sys-category-input";
    }

    @RequestMapping("sys-category-save")
    public String save(@ModelAttribute SysCategory sysCategory,
            RedirectAttributes redirectAttributes) {
        SysCategory dest = null;
        Long id = sysCategory.getId();

        if (id != null) {
            dest = sysCategoryManager.get(id);
            beanMapper.copy(sysCategory, dest);
        } else {
            dest = sysCategory;
        }

        sysCategoryManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sys/sys-category-list.do";
    }

    @RequestMapping("sys-category-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SysCategory> sysCategorys = sysCategoryManager
                .findByIds(selectedItem);
        sysCategoryManager.removeAll(sysCategorys);
        messageHelper.addFlashMessage(redirectAttributes, "core.delete.save",
                "删除成功");

        return "redirect:/sys/sys-category-list.do";
    }

    @RequestMapping("sys-category-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sysCategoryManager.pagedQuery(page, propertyFilters);

        List<SysCategory> sysCategorys = (List<SysCategory>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("sysmenu");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sysCategorys);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSysCategoryManager(SysCategoryManager sysCategoryManager) {
        this.sysCategoryManager = sysCategoryManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
