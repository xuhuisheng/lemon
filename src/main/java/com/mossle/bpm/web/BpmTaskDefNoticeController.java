package com.mossle.bpm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.domain.BpmTaskDefNotice;
import com.mossle.bpm.persistence.manager.BpmMailTemplateManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefNoticeManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("bpm")
public class BpmTaskDefNoticeController {
    private BpmTaskDefNoticeManager bpmTaskDefNoticeManager;
    private BpmProcessManager bpmProcessManager;
    private BpmMailTemplateManager bpmMailTemplateManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private MessageHelper messageHelper;

    @RequestMapping("bpm-task-def-notice-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bpmTaskDefNoticeManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "bpm/bpm-task-def-notice-list";
    }

    @RequestMapping("bpm-task-def-notice-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BpmTaskDefNotice bpmTaskDefNotice = bpmTaskDefNoticeManager.get(id);
            model.addAttribute("model", bpmTaskDefNotice);
        }

        List<BpmMailTemplate> bpmMailTemplates = bpmMailTemplateManager
                .getAll();
        model.addAttribute("bpmMailTemplates", bpmMailTemplates);

        return "bpm/bpm-task-def-notice-input";
    }

    @RequestMapping("bpm-task-def-notice-save")
    public String save(@ModelAttribute BpmTaskDefNotice bpmTaskDefNotice,
            @RequestParam("bpmProcessId") Long bpmProcessId,
            @RequestParam("bpmMailTemplateId") Long bpmMailTemplateId,
            RedirectAttributes redirectAttributes) {
        BpmTaskDefNotice dest = null;
        Long id = bpmTaskDefNotice.getId();

        if (id != null) {
            dest = bpmTaskDefNoticeManager.get(id);
            beanMapper.copy(bpmTaskDefNotice, dest);
        } else {
            dest = bpmTaskDefNotice;
        }

        dest.setBpmProcess(bpmProcessManager.get(bpmProcessId));
        dest.setBpmMailTemplate(bpmMailTemplateManager.get(bpmMailTemplateId));
        bpmTaskDefNoticeManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/bpm/bpm-task-def-notice-list.do?bpmProcessId="
                + bpmProcessId;
    }

    @RequestMapping("bpm-task-def-notice-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("bpmProcessId") Long bpmProcessId,
            RedirectAttributes redirectAttributes) {
        List<BpmTaskDefNotice> bpmCategories = bpmTaskDefNoticeManager
                .findByIds(selectedItem);
        bpmTaskDefNoticeManager.removeAll(bpmCategories);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/bpm/bpm-task-def-notice-list.do?bpmProcessId="
                + bpmProcessId;
    }

    @RequestMapping("bpm-task-def-notice-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bpmTaskDefNoticeManager.pagedQuery(page, propertyFilters);

        List<BpmTaskDefNotice> bpmCategories = (List<BpmTaskDefNotice>) page
                .getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("bpm-process");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bpmCategories);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("bpm-task-def-notice-removeNotice")
    public String removeNotice(@RequestParam("id") Long id) {
        BpmTaskDefNotice bpmTaskDefNotice = bpmTaskDefNoticeManager.get(id);
        Long bpmProcessId = bpmTaskDefNotice.getBpmProcess().getId();
        bpmTaskDefNoticeManager.remove(bpmTaskDefNotice);

        return "redirect:/bpm/bpm-task-def-notice-list.do?bpmProcessId="
                + bpmProcessId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmTaskDefNoticeManager(
            BpmTaskDefNoticeManager bpmTaskDefNoticeManager) {
        this.bpmTaskDefNoticeManager = bpmTaskDefNoticeManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmMailTemplateManager(
            BpmMailTemplateManager bpmMailTemplateManager) {
        this.bpmMailTemplateManager = bpmMailTemplateManager;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
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
