package com.mossle.doc.web;

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

import com.mossle.doc.persistence.domain.DocInfo;
import com.mossle.doc.persistence.manager.DocInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("doc")
public class DocInfoController {
    private DocInfoManager docInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("doc-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = docInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "doc/doc-info-list";
    }

    @RequestMapping("doc-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            DocInfo docInfo = docInfoManager.get(id);
            model.addAttribute("model", docInfo);
        }

        return "doc/doc-info-input";
    }

    @RequestMapping("doc-info-save")
    public String save(@ModelAttribute DocInfo docInfo,
            RedirectAttributes redirectAttributes) {
        Long id = docInfo.getId();
        DocInfo dest = null;

        if (id != null) {
            dest = docInfoManager.get(id);
            beanMapper.copy(docInfo, dest);
        } else {
            dest = docInfo;
        }

        docInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/doc/doc-info-list.do";
    }

    @RequestMapping("doc-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<DocInfo> docInfos = docInfoManager.findByIds(selectedItem);
        docInfoManager.removeAll(docInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/doc/doc-info-list.do";
    }

    @RequestMapping("doc-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = docInfoManager.pagedQuery(page, propertyFilters);

        List<DocInfo> docInfos = (List<DocInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("docInfo");
        tableModel.addHeaders("id", "name");
        tableModel.setData(docInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setdocInfoManager(DocInfoManager docInfoManager) {
        this.docInfoManager = docInfoManager;
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
