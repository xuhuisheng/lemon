package com.mossle.plm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;

import com.mossle.plm.persistence.domain.PlmIssue;
import com.mossle.plm.persistence.domain.PlmProject;
import com.mossle.plm.persistence.domain.PlmRequirement;
import com.mossle.plm.persistence.manager.PlmIssueManager;
import com.mossle.plm.persistence.manager.PlmProjectManager;
import com.mossle.plm.persistence.manager.PlmRequirementManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("plm")
public class PlmRequirementController {
    private static Logger logger = LoggerFactory.getLogger(PlmController.class);
    private PlmProjectManager plmProjectManager;
    private PlmIssueManager plmIssueManager;
    private PlmRequirementManager plmRequirementManager;

    /**
     * 需求列表.
     */
    @RequestMapping("plm-requirement-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");
        page.setDefaultOrder("id", "desc");

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = plmRequirementManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "plm/plm-requirement-list";
    }

    /**
     * 添加需求.
     */
    @RequestMapping("plm-requirement-input")
    public String input(Model model) {
        return "plm/plm-requirement-input";
    }

    /**
     * 添加需求.
     */
    @RequestMapping("plm-requirement-save")
    public String save(PlmRequirement plmRequirement) {
        this.plmRequirementManager.save(plmRequirement);

        return "redirect:/plm/plm-requirement-list.do";
    }

    /**
     * 产品视图.
     */
    @RequestMapping("product")
    public String product(@RequestParam("id") Long id,
            @RequestParam(value = "issueId", required = false) Long issueId,
            Model model) {
        PlmProject plmProject = plmProjectManager.get(id);
        model.addAttribute("plmProject", plmProject);

        String hql = "from PlmRequirement where plmRequirement=null and plmProject.id=?";
        PlmRequirement plmRequirement = this.plmRequirementManager.findUnique(
                hql, id);

        // 初始化需求根节点
        if (plmRequirement == null) {
            plmRequirement = new PlmRequirement();
            plmRequirement.setName(plmProject.getName());
            plmRequirement.setPlmProject(plmProject);
            plmRequirementManager.save(plmRequirement);
        }

        model.addAttribute("plmRequirement", plmRequirement);

        List<PlmIssue> plmIssues = plmIssueManager.findBy("type", "story");
        model.addAttribute("plmIssues", plmIssues);

        if (issueId != null) {
            PlmIssue plmIssue = this.plmIssueManager.get(issueId);
            model.addAttribute("plmIssue", plmIssue);
        }

        return "plm/product";
    }

    /**
     * 添加需求.
     */
    @RequestMapping("requirement-input")
    public String requirementInput(@RequestParam("parentId") Long parentId,
            Model model) {
        PlmRequirement plmRequirement = this.plmRequirementManager
                .get(parentId);
        model.addAttribute("plmRequirement", plmRequirement);
        model.addAttribute("plmProject", plmRequirement.getPlmProject());

        return "plm/requirement-input";
    }

    /**
     * 添加需求.
     */
    @RequestMapping("requirement-save")
    public String requirementSave(@RequestParam("parentId") Long parentId,
            @RequestParam("name") String name) {
        PlmRequirement parent = this.plmRequirementManager.get(parentId);
        PlmRequirement plmRequirement = new PlmRequirement();
        plmRequirement.setName(name);
        plmRequirement.setPlmRequirement(parent);
        plmRequirement.setPlmProject(parent.getPlmProject());
        plmRequirement.setPriority((int) (System.currentTimeMillis() / 1000));
        this.plmRequirementManager.save(plmRequirement);

        return "redirect:/plm/product.do?id="
                + plmRequirement.getPlmProject().getId();
    }

    /**
     * 删除需求.
     */
    @RequestMapping("requirement-remove")
    public String requirementRemove(@RequestParam("id") Long id) {
        PlmRequirement plmRequirement = this.plmRequirementManager.get(id);
        plmRequirementManager.remove(plmRequirement);

        return "redirect:/plm/product.do?id="
                + plmRequirement.getPlmProject().getId();
    }

    /**
     * x * 为需求创建issue.
     */
    @RequestMapping("requirement-link")
    public String requirementLink(@RequestParam("id") Long id) {
        PlmRequirement plmRequirement = this.plmRequirementManager.get(id);
        PlmIssue plmIssue = new PlmIssue();
        plmIssue.setName(plmRequirement.getName());
        plmIssue.setType("story");
        plmIssue.setPlmProject(plmRequirement.getPlmProject());
        plmIssueManager.save(plmIssue);
        plmRequirement.setPlmIssue(plmIssue);
        plmRequirementManager.save(plmRequirement);

        return "redirect:/plm/product.do?id="
                + plmRequirement.getPlmProject().getId() + "&issueId="
                + plmIssue.getId();
    }

    @Resource
    public void setPlmProjectManager(PlmProjectManager plmProjectManager) {
        this.plmProjectManager = plmProjectManager;
    }

    @Resource
    public void setPlmIssueManager(PlmIssueManager plmIssueManager) {
        this.plmIssueManager = plmIssueManager;
    }

    @Resource
    public void setPlmRequirementManager(
            PlmRequirementManager plmRequirementManager) {
        this.plmRequirementManager = plmRequirementManager;
    }
}
