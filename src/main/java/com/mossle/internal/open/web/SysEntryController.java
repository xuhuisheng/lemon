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
import com.mossle.internal.open.persistence.domain.SysEntry;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysEntryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sys")
public class SysEntryController {
    private SysInfoManager sysInfoManager;
    private SysCategoryManager sysCategoryManager;
    private SysEntryManager sysEntryManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("sys-entry-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sysEntryManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "sys/sys-entry-list";
    }

    @RequestMapping("sys-entry-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SysEntry sysEntry = sysEntryManager.get(id);
            model.addAttribute("model", sysEntry);
        }

        model.addAttribute("sysCategories", sysCategoryManager.getAll());

        return "sys/sys-entry-input";
    }

    @RequestMapping("sys-entry-save")
    public String save(@ModelAttribute SysEntry sysEntry,
            @RequestParam("categoryId") Long categoryId,
            RedirectAttributes redirectAttributes) {
        SysEntry dest = null;
        Long id = sysEntry.getId();

        if (id != null) {
            dest = sysEntryManager.get(id);
            beanMapper.copy(sysEntry, dest);
        } else {
            dest = sysEntry;
        }

        dest.setSysCategory(sysCategoryManager.get(categoryId));

        sysEntryManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sys/sys-entry-list.do";
    }

    @RequestMapping("sys-entry-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SysEntry> sysEntries = sysEntryManager.findByIds(selectedItem);
        sysEntryManager.removeAll(sysEntries);
        messageHelper.addFlashMessage(redirectAttributes, "core.delete.save",
                "删除成功");

        return "redirect:/sys/sys-entry-list.do";
    }

    @RequestMapping("sys-entry-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sysInfoManager.pagedQuery(page, propertyFilters);

        List<SysEntry> sysEntries = (List<SysEntry>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("sysmenu");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sysEntries);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }

    @Resource
    public void setSysCategoryManager(SysCategoryManager sysCategoryManager) {
        this.sysCategoryManager = sysCategoryManager;
    }

    @Resource
    public void setSysEntryManager(SysEntryManager sysEntryManager) {
        this.sysEntryManager = sysEntryManager;
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
