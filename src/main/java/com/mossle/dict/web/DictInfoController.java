package com.mossle.dict.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.dict.DictConnector;
import com.mossle.api.dict.DictDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.dict.persistence.domain.DictData;
import com.mossle.dict.persistence.domain.DictInfo;
import com.mossle.dict.persistence.domain.DictSchema;
import com.mossle.dict.persistence.domain.DictType;
import com.mossle.dict.persistence.manager.DictDataManager;
import com.mossle.dict.persistence.manager.DictInfoManager;
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
public class DictInfoController {
    private DictInfoManager dictInfoManager;
    private DictTypeManager dictTypeManager;
    private DictSchemaManager dictSchemaManager;
    private DictDataManager dictDataManager;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private Exportor exportor;
    private DictConnector dictConnector;
    private TenantHolder tenantHolder;

    @RequestMapping("dict-info-list")
    public String list(@RequestParam("typeId") Long typeId, Model model) {
        String hql = "from DictInfo where dictType.id=? order by priority";
        List<DictInfo> dictInfos = dictInfoManager.find(hql, typeId);

        model.addAttribute("dictInfos", dictInfos);

        return "dict/dict-info-list";
    }

    @RequestMapping("dict-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("typeId") Long typeId, Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id != null) {
            DictInfo dictInfo = dictInfoManager.get(id);
            model.addAttribute("model", dictInfo);

            DictDTO dictDto = dictConnector.findDictByName(dictInfo.getName(),
                    dictInfo.getDictType().getName(), tenantId);
            model.addAttribute("dictDto", dictDto);
        } else {
            DictType dictType = dictTypeManager.get(typeId);
            DictDTO dictDto = dictConnector.findDictByType(dictType.getName(),
                    tenantId);
            model.addAttribute("dictDto", dictDto);
        }

        DictType dictType = dictTypeManager.get(typeId);
        model.addAttribute("dictType", dictType);

        return "dict/dict-info-input";
    }

    @RequestMapping("dict-info-save")
    public String save(@ModelAttribute DictInfo dictInfo,
            @RequestParam("typeId") Long typeId,
            @RequestParam Map<String, String> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        DictInfo dest = null;

        Long id = dictInfo.getId();

        if (id != null) {
            dest = dictInfoManager.get(id);
            beanMapper.copy(dictInfo, dest);
        } else {
            dest = dictInfo;
            dest.setTenantId(tenantId);
        }

        DictType dictType = dictTypeManager.get(typeId);
        dictInfo.setDictType(dictType);
        dictInfoManager.save(dest);

        for (DictSchema dictSchema : dictType.getDictSchemas()) {
            String hql = "from DictData where dictInfo=? and dictSchema=?";
            DictData dictData = dictDataManager.findUnique(hql, dictInfo,
                    dictSchema);

            if (dictData == null) {
                dictData = new DictData();
                dictData.setDictInfo(dictInfo);
                dictData.setDictSchema(dictSchema);
                dictData.setTenantId(tenantId);
            }

            dictData.setName(dictSchema.getName());

            String value = parameterMap.get("dictData_" + dictSchema.getName());
            dictData.setValue(value);
            dictDataManager.save(dictData);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/dict/dict-info-list.do?typeId=" + typeId;
    }

    @RequestMapping("dict-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("typeId") Long typeId,
            RedirectAttributes redirectAttributes) {
        List<DictInfo> dictInfos = dictInfoManager.findByIds(selectedItem);

        dictInfoManager.removeAll(dictInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/dict/dict-info-list.do?typeId=" + typeId;
    }

    @RequestMapping("dict-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = dictInfoManager.pagedQuery(page, propertyFilters);

        List<DictInfo> dictInfos = (List<DictInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("dict info");
        tableModel.addHeaders("id", "name", "stringValue", "description");
        tableModel.setData(dictInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setDictInfoManager(DictInfoManager dictInfoManager) {
        this.dictInfoManager = dictInfoManager;
    }

    @Resource
    public void setDictTypeManager(DictTypeManager dictTypeManager) {
        this.dictTypeManager = dictTypeManager;
    }

    @Resource
    public void setDictSchemaManager(DictSchemaManager dictSchemaManager) {
        this.dictSchemaManager = dictSchemaManager;
    }

    @Resource
    public void setDictDataManager(DictDataManager dictDataManager) {
        this.dictDataManager = dictDataManager;
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
    public void setDictConnector(DictConnector dictConnector) {
        this.dictConnector = dictConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
