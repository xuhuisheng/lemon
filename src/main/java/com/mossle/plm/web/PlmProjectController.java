package com.mossle.plm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.plm.persistence.domain.PlmProject;
import com.mossle.plm.persistence.manager.PlmProjectManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("plm")
public class PlmProjectController {
    private PlmProjectManager plmProjectManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("plm-project-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = plmProjectManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "plm/plm-project-list";
    }

    @RequestMapping("plm-project-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PlmProject plmProject = plmProjectManager.get(id);
            model.addAttribute("model", plmProject);
        }

        return "plm/plm-project-input";
    }

    @RequestMapping("plm-project-save")
    public String save(@ModelAttribute PlmProject plmProject,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        PlmProject dest = null;

        Long id = plmProject.getId();

        if (id != null) {
            dest = plmProjectManager.get(id);
            beanMapper.copy(plmProject, dest);
        } else {
            dest = plmProject;
        }

        plmProjectManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/plm/plm-project-list.do";
    }

    @RequestMapping("plm-project-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PlmProject> plmProjects = plmProjectManager
                .findByIds(selectedItem);

        plmProjectManager.removeAll(plmProjects);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/plm/plm-project-list.do";
    }

    @RequestMapping("plm-project-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = plmProjectManager.pagedQuery(page, propertyFilters);

        List<PlmProject> plmProjects = (List<PlmProject>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("plm project");
        tableModel.addHeaders("id", "name");
        tableModel.setData(plmProjects);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPlmProjectManager(PlmProjectManager plmProjectManager) {
        this.plmProjectManager = plmProjectManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
