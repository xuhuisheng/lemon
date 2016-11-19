package com.mossle.dict.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.dict.persistence.domain.DictSchema;
import com.mossle.dict.persistence.manager.DictSchemaManager;
import com.mossle.dict.persistence.manager.DictTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("dict")
public class DictSchemaController {
    private DictSchemaManager dictSchemaManager;
    private DictTypeManager dictTypeManager;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private Exportor exportor;
    private TenantHolder tenantHolder;

    @RequestMapping("dict-schema-config")
    public String config(@RequestParam("typeId") Long typeId, Model model) {
        String hql = "from DictSchema where dictType.id=? order by priority";
        List<DictSchema> dictSchemas = dictSchemaManager.find(hql, typeId);

        model.addAttribute("dictSchemas", dictSchemas);

        return "dict/dict-schema-config";
    }

    @RequestMapping("dict-schema-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = dictSchemaManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "dict/dict-schema-list";
    }

    @RequestMapping("dict-schema-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            DictSchema dictSchema = dictSchemaManager.get(id);
            model.addAttribute("model", dictSchema);
        }

        return "dict/dict-schema-input";
    }

    @RequestMapping("dict-schema-save")
    public String save(@ModelAttribute DictSchema dictSchema,
            @RequestParam("typeId") Long typeId,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        DictSchema dest = null;

        Long id = dictSchema.getId();

        if (id != null) {
            dest = dictSchemaManager.get(id);
            beanMapper.copy(dictSchema, dest);
        } else {
            dest = dictSchema;
            dest.setTenantId(tenantId);
        }

        dest.setDictType(dictTypeManager.get(typeId));

        dictSchemaManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/dict/dict-schema-config.do?typeId=" + typeId;
    }

    @RequestMapping("dict-schema-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("typeId") Long typeId,
            RedirectAttributes redirectAttributes) {
        List<DictSchema> dictSchemas = dictSchemaManager
                .findByIds(selectedItem);

        dictSchemaManager.removeAll(dictSchemas);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/dict/dict-schema-config.do?typeId=" + typeId;
    }

    @RequestMapping("dict-schema-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = dictSchemaManager.pagedQuery(page, propertyFilters);

        List<DictSchema> dictSchemas = (List<DictSchema>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("dict info");
        tableModel.addHeaders("id", "name", "stringValue", "description");
        tableModel.setData(dictSchemas);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setDictSchemaManager(DictSchemaManager dictSchemaManager) {
        this.dictSchemaManager = dictSchemaManager;
    }

    @Resource
    public void setDictTypeManager(DictTypeManager dictTypeManager) {
        this.dictTypeManager = dictTypeManager;
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
}
