package com.mossle.form.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.FormTemplateManager;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("form")
public class FormTemplateController {
    private FormTemplateManager formTemplateManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("form-template-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = formTemplateManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "form/form-template-list";
    }

    @RequestMapping("form-template-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            FormTemplate formTemplate = formTemplateManager.get(id);
            model.addAttribute("model", formTemplate);
        }

        return "form/form-template-input";
    }

    @RequestMapping("form-template-save")
    public String save(@ModelAttribute FormTemplate formTemplate,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        FormTemplate dest = null;
        Long id = formTemplate.getId();

        if (id != null) {
            dest = formTemplateManager.get(id);
            beanMapper.copy(formTemplate, dest);
        } else {
            dest = formTemplate;
            dest.setType(0);
        }

        formTemplateManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/form/form-template-list.do";
    }

    @RequestMapping("form-template-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<FormTemplate> formTemplates = formTemplateManager
                .findByIds(selectedItem);

        formTemplateManager.removeAll(formTemplates);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/form/form-template-list.do";
    }

    @RequestMapping("form-template-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = formTemplateManager.pagedQuery(page, propertyFilters);

        List<FormTemplate> dynamicModels = (List<FormTemplate>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("dynamic model");
        tableModel.addHeaders("id", "name");
        tableModel.setData(dynamicModels);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
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
