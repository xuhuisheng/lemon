package com.mossle.form.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.MultipartHandler;
import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.MultipartFileDataSource;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.keyvalue.KeyValue;
import com.mossle.form.keyvalue.Prop;
import com.mossle.form.keyvalue.Record;
import com.mossle.form.keyvalue.RecordBuilder;
import com.mossle.form.manager.FormTemplateManager;
import com.mossle.form.xform.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("form")
public class FormTemplateController {
    private static Logger logger = LoggerFactory
            .getLogger(FormTemplateController.class);
    private FormTemplateManager formTemplateManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private MessageHelper messageHelper;
    private KeyValue keyValue;
    private MultipartResolver multipartResolver;
    private StoreConnector storeConnector;

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

    @RequestMapping("form-template-preview")
    public String preview(@RequestParam("id") Long id, Model model)
            throws Exception {
        FormTemplate formTemplate = formTemplateManager.get(id);
        model.addAttribute("formTemplate", formTemplate);

        Record record = keyValue.findByRef(formTemplate.getCode());

        if (record == null) {
            record = new Record();
            record.setName(formTemplate.getName());
            record.setRef(formTemplate.getCode());
            keyValue.save(record);
        }

        model.addAttribute("record", record);

        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formTemplate.getContent()).setRecord(record)
                .build();
        model.addAttribute("xform", xform);

        return "form/form-template-preview";
    }

    @RequestMapping("form-template-test")
    public String test(HttpServletRequest request) throws Exception {
        MultipartHandler multipartHandler = new MultipartHandler(
                multipartResolver);
        FormTemplate formTemplate = null;

        try {
            multipartHandler.handle(request);
            logger.info("{}", multipartHandler.getMultiValueMap());
            logger.info("{}", multipartHandler.getMultiFileMap());

            String ref = multipartHandler.getMultiValueMap().getFirst("ref");
            formTemplate = formTemplateManager.findUniqueBy("code", ref);

            Record record = keyValue.findByRef(ref);

            record = new RecordBuilder().build(record, multipartHandler,
                    storeConnector);

            keyValue.save(record);
        } finally {
            multipartHandler.clear();
        }

        if (formTemplate == null) {
            return "redirect:/form/form-template-list.do";
        } else {
            return "redirect:/form/form-template-preview.do?id="
                    + formTemplate.getId();
        }
    }

    @RequestMapping("form-template-copy")
    public String copy(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        FormTemplate formTemplate = formTemplateManager.get(id);

        if (formTemplate == null) {
            return "redirect:/form/form-template.do";
        }

        int index = 1;
        String code = formTemplate.getCode();
        String name = formTemplate.getName();

        while (true) {
            FormTemplate targetFormTemplate = formTemplateManager.findUniqueBy(
                    "code", code + "" + index);

            if (targetFormTemplate == null) {
                code = code + "" + index;
                name = name + "" + index;

                break;
            }

            index++;
        }

        FormTemplate targetFormTemplate = new FormTemplate();
        beanMapper.copy(formTemplate, targetFormTemplate);
        targetFormTemplate.setId(null);
        targetFormTemplate.setCode(code);
        targetFormTemplate.setName(name);
        formTemplateManager.save(targetFormTemplate);

        return "redirect:/form/form-template-list.do";
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

    @Resource
    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
    }

    @Resource
    public void setMultipartResolver(MultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
