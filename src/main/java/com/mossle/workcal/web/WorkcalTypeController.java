package com.mossle.workcal.web;

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

import com.mossle.workcal.domain.WorkcalType;
import com.mossle.workcal.manager.WorkcalTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workcal")
public class WorkcalTypeController {
    private WorkcalTypeManager workcalTypeManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("workcal-type-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = workcalTypeManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "workcal/workcal-type-list";
    }

    @RequestMapping("workcal-type-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WorkcalType workcalType = workcalTypeManager.get(id);
            model.addAttribute("model", workcalType);
        }

        return "workcal/workcal-type-input";
    }

    @RequestMapping("workcal-type-save")
    public String save(@ModelAttribute WorkcalType workcalType,
            RedirectAttributes redirectAttributes) {
        Long id = workcalType.getId();
        WorkcalType dest = null;

        if (id != null) {
            dest = workcalTypeManager.get(id);
            beanMapper.copy(workcalType, dest);
        } else {
            dest = workcalType;
        }

        workcalTypeManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/workcal/workcal-type-list.do";
    }

    @RequestMapping("workcal-type-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WorkcalType> workcalTypes = workcalTypeManager
                .findByIds(selectedItem);
        workcalTypeManager.removeAll(workcalTypes);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/workcal/workcal-type-list.do";
    }

    @RequestMapping("workcal-type-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = workcalTypeManager.pagedQuery(page, propertyFilters);

        List<WorkcalType> workcalTypes = (List<WorkcalType>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("workcalType");
        tableModel.addHeaders("id", "name");
        tableModel.setData(workcalTypes);
        exportor.export(response, tableModel);
    }

    @RequestMapping("workcal-type-checkName")
    @ResponseBody
    public boolean checkName(@RequestParam("name") String name,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from WorkcalType where name=?";
        Object[] params = { name };

        if (id != null) {
            hql = "from WorkcalType where name=? and id<>?";
            params = new Object[] { name, id };
        }

        WorkcalType workcalType = workcalTypeManager.findUnique(hql, params);

        boolean result = (workcalType == null);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setWorkcalTypeManager(WorkcalTypeManager workcalTypeManager) {
        this.workcalTypeManager = workcalTypeManager;
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
