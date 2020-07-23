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
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sys")
public class SysInfoController {
    private SysInfoManager sysInfoManager;
    private SysCategoryManager sysCategoryManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("sys-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sysInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "sys/sys-info-list";
    }

    @RequestMapping("sys-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SysInfo sysInfo = sysInfoManager.get(id);
            model.addAttribute("model", sysInfo);
        }

        model.addAttribute("sysCategories", sysCategoryManager.getAll());

        return "sys/sys-info-input";
    }

    @RequestMapping("sys-info-save")
    public String save(@ModelAttribute SysInfo sysInfo,
            @RequestParam("categoryId") Long categoryId,
            RedirectAttributes redirectAttributes) {
        SysInfo dest = null;
        Long id = sysInfo.getId();

        if (id != null) {
            dest = sysInfoManager.get(id);
            beanMapper.copy(sysInfo, dest);
        } else {
            dest = sysInfo;
        }

        dest.setSysCategory(sysCategoryManager.get(categoryId));

        sysInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sys/sys-info-list.do";
    }

    @RequestMapping("sys-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SysInfo> sysInfos = sysInfoManager.findByIds(selectedItem);
        sysInfoManager.removeAll(sysInfos);
        messageHelper.addFlashMessage(redirectAttributes, "core.delete.save",
                "删除成功");

        return "redirect:/sys/sys-info-list.do";
    }

    @RequestMapping("sys-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sysInfoManager.pagedQuery(page, propertyFilters);

        List<SysInfo> sysInfos = (List<SysInfo>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("sysmenu");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sysInfos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("view")
    public String view(@RequestParam("id") Long id, Model model) {
        SysInfo sysInfo = this.sysInfoManager.get(id);
        model.addAttribute("sysInfo", sysInfo);

        return "sys/view";
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
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
