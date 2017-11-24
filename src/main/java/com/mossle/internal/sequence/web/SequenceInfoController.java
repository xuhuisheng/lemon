package com.mossle.internal.sequence.web;

import java.util.ArrayList;
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

import com.mossle.internal.sequence.persistence.domain.SequenceInfo;
import com.mossle.internal.sequence.persistence.manager.SequenceInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sequence")
public class SequenceInfoController {
    private SequenceInfoManager sequenceInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("sequence-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sequenceInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "sequence/sequence-info-list";
    }

    @RequestMapping("sequence-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SequenceInfo sequenceInfo = sequenceInfoManager.get(id);
            model.addAttribute("model", sequenceInfo);
        }

        return "sequence/sequence-info-input";
    }

    @RequestMapping("sequence-info-save")
    public String save(@ModelAttribute SequenceInfo sequenceInfo,
            RedirectAttributes redirectAttributes) {
        Long id = sequenceInfo.getId();
        SequenceInfo dest = null;

        if (id != null) {
            dest = sequenceInfoManager.get(id);
            beanMapper.copy(sequenceInfo, dest);
        } else {
            dest = sequenceInfo;
        }

        sequenceInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sequence/sequence-info-list.do";
    }

    @RequestMapping("sequence-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SequenceInfo> sequenceInfos = sequenceInfoManager
                .findByIds(selectedItem);
        sequenceInfoManager.removeAll(sequenceInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sequence/sequence-info-list.do";
    }

    @RequestMapping("sequence-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = sequenceInfoManager.pagedQuery(page, propertyFilters);

        List<SequenceInfo> sequenceInfos = (List<SequenceInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sequenceInfo");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sequenceInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setsequenceInfoManager(SequenceInfoManager sequenceInfoManager) {
        this.sequenceInfoManager = sequenceInfoManager;
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
