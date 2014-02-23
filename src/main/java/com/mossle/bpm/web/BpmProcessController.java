package com.mossle.bpm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.cmd.FindGraphCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.graph.ActivitiGraphBuilder;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.graph.Node;
import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskDef;
import com.mossle.bpm.persistence.domain.BpmTaskDefNotice;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmMailTemplateManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefNoticeManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("bpm")
public class BpmProcessController {
    private BpmProcessManager bpmProcessManager;
    private BpmCategoryManager bpmCategoryManager;
    private BpmTaskDefNoticeManager bpmTaskDefNoticeManager;
    private BpmMailTemplateManager bpmMailTemplateManager;
    private BpmTaskDefManager bpmTaskDefManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private MessageHelper messageHelper;

    @RequestMapping("bpm-process-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bpmProcessManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "bpm/bpm-process-list";
    }

    @RequestMapping("bpm-process-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BpmProcess bpmProcess = bpmProcessManager.get(id);
            model.addAttribute("model", bpmProcess);
        }

        List<BpmCategory> bpmCategories = bpmCategoryManager.getAll();
        List<BpmConfBase> bpmConfBases = bpmConfBaseManager.getAll();
        model.addAttribute("bpmCategories", bpmCategories);
        model.addAttribute("bpmConfBases", bpmConfBases);

        return "bpm/bpm-process-input";
    }

    @RequestMapping("bpm-process-save")
    public String save(@ModelAttribute BpmProcess bpmProcess,
            @RequestParam("bpmCategoryId") Long bpmCategoryId,
            @RequestParam("bpmConfBaseId") Long bpmConfBaseId,
            RedirectAttributes redirectAttributes) {
        BpmProcess dest = null;
        Long id = bpmProcess.getId();

        if (id != null) {
            dest = bpmProcessManager.get(id);
            beanMapper.copy(bpmProcess, dest);
        } else {
            dest = bpmProcess;
        }

        dest.setBpmCategory(bpmCategoryManager.get(bpmCategoryId));
        dest.setBpmConfBase(bpmConfBaseManager.get(bpmConfBaseId));
        bpmProcessManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/bpm/bpm-process-list.do";
    }

    @RequestMapping("bpm-process-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<BpmProcess> bpmCategories = bpmProcessManager
                .findByIds(selectedItem);
        bpmProcessManager.removeAll(bpmCategories);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/bpm/bpm-process-list.do";
    }

    @RequestMapping("bpm-process-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bpmProcessManager.pagedQuery(page, propertyFilters);

        List<BpmProcess> bpmCategories = (List<BpmProcess>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("bpm-process");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bpmCategories);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
    }

    @Resource
    public void setBpmTaskDefNoticeManager(
            BpmTaskDefNoticeManager bpmTaskDefNoticeManager) {
        this.bpmTaskDefNoticeManager = bpmTaskDefNoticeManager;
    }

    @Resource
    public void setBpmMailTemplate(BpmMailTemplateManager bpmMailTemplateManager) {
        this.bpmMailTemplateManager = bpmMailTemplateManager;
    }

    @Resource
    public void setBpmTaskDefManager(BpmTaskDefManager bpmTaskDefManager) {
        this.bpmTaskDefManager = bpmTaskDefManager;
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
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
