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

import com.mossle.internal.open.persistence.domain.OpenApp;
import com.mossle.internal.open.persistence.manager.OpenAppManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("open")
public class OpenAppController {
    private OpenAppManager openAppManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("open-app-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = openAppManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "open/open-app-list";
    }

    @RequestMapping("open-app-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            OpenApp openApp = openAppManager.get(id);
            model.addAttribute("model", openApp);
        }

        model.addAttribute("openApps", openAppManager.getAll());

        return "open/open-app-input";
    }

    @RequestMapping("open-app-save")
    public String save(@ModelAttribute OpenApp openApp,
            @RequestParam(value = "parentId", required = false) Long parentId,
            RedirectAttributes redirectAttributes) {
        OpenApp dest = null;
        Long id = openApp.getId();

        if (id != null) {
            dest = openAppManager.get(id);
            beanMapper.copy(openApp, dest);
        } else {
            dest = openApp;
        }

        openAppManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/open/open-app-list.do";
    }

    @RequestMapping("open-app-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<OpenApp> openApps = openAppManager.findByIds(selectedItem);
        openAppManager.removeAll(openApps);
        messageHelper.addFlashMessage(redirectAttributes, "core.delete.save",
                "删除成功");

        return "redirect:/open/open-app-list.do";
    }

    @RequestMapping("open-app-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = openAppManager.pagedQuery(page, propertyFilters);

        List<OpenApp> openApps = (List<OpenApp>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("openmenu");
        tableModel.addHeaders("id", "name");
        tableModel.setData(openApps);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setOpenAppManager(OpenAppManager openAppManager) {
        this.openAppManager = openAppManager;
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
