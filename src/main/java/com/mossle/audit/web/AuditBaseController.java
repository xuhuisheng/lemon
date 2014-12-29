package com.mossle.audit.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.audit.domain.AuditBase;
import com.mossle.audit.manager.AuditBaseManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("audit")
public class AuditBaseController {
    private AuditBaseManager auditBaseManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("audit-base-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = auditBaseManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "audit/audit-base-list";
    }

    @RequestMapping("audit-base-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AuditBase auditBase = auditBaseManager.get(id);
            model.addAttribute("model", auditBase);
        }

        return "audit/audit-base-input";
    }

    @RequestMapping("audit-base-save")
    public String save(@ModelAttribute AuditBase auditBase,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        AuditBase dest = null;

        Long id = auditBase.getId();

        if (id != null) {
            dest = auditBaseManager.get(id);
            beanMapper.copy(auditBase, dest);
        } else {
            dest = auditBase;
        }

        auditBaseManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/audit/audit-base-list.do";
    }

    @RequestMapping("audit-base-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AuditBase> auditBases = auditBaseManager.findByIds(selectedItem);

        auditBaseManager.removeAll(auditBases);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/audit/audit-base-list.do";
    }

    @RequestMapping("audit-base-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = auditBaseManager.pagedQuery(page, propertyFilters);

        List<AuditBase> auditBases = (List<AuditBase>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("audit base");
        tableModel.addHeaders("id", "client", "server", "resource");
        tableModel.setData(auditBases);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAuditBaseManager(AuditBaseManager auditBaseManager) {
        this.auditBaseManager = auditBaseManager;
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
