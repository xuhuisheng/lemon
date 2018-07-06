package com.mossle.plm.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import com.mossle.plm.persistence.domain.*;
import com.mossle.plm.persistence.domain.PlmCategory;
import com.mossle.plm.persistence.domain.PlmComment;
import com.mossle.plm.persistence.domain.PlmConfig;
import com.mossle.plm.persistence.domain.PlmIssue;
import com.mossle.plm.persistence.domain.PlmLog;
import com.mossle.plm.persistence.domain.PlmProject;
import com.mossle.plm.persistence.domain.PlmSprint;
import com.mossle.plm.persistence.domain.PlmVersion;
import com.mossle.plm.persistence.manager.*;
import com.mossle.plm.persistence.manager.PlmCategoryManager;
import com.mossle.plm.persistence.manager.PlmCommentManager;
import com.mossle.plm.persistence.manager.PlmConfigManager;
import com.mossle.plm.persistence.manager.PlmIssueManager;
import com.mossle.plm.persistence.manager.PlmLogManager;
import com.mossle.plm.persistence.manager.PlmProjectManager;
import com.mossle.plm.persistence.manager.PlmSprintManager;
import com.mossle.plm.persistence.manager.PlmVersionManager;
import com.mossle.plm.service.PlmLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

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
    private PlmVersionManager plmVersionManager;
    private PlmIssueManager plmIssueManager;
    private PlmCommentManager plmCommentManager;
    private PlmCategoryManager plmCategoryManager;
    private PlmLogManager plmLogManager;
    private PlmSprintManager plmSprintManager;
    private PlmConfigManager plmConfigManager;
    private PlmRequirementManager plmRequirementManager;
    private PlmLogService plmLogService;
    private CurrentUserHolder currentUserHolder;
    private JdbcTemplate jdbcTemplate;
    private BeanMapper beanMapper = new BeanMapper();

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
     * 为需求创建issue.
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
    public void setPlmVersionManager(PlmVersionManager plmVersionManager) {
        this.plmVersionManager = plmVersionManager;
    }

    @Resource
    public void setPlmIssueManager(PlmIssueManager plmIssueManager) {
        this.plmIssueManager = plmIssueManager;
    }

    @Resource
    public void setPlmCommentManager(PlmCommentManager plmCommentManager) {
        this.plmCommentManager = plmCommentManager;
    }

    @Resource
    public void setPlmCategoryManager(PlmCategoryManager plmCategoryManager) {
        this.plmCategoryManager = plmCategoryManager;
    }

    @Resource
    public void setPlmLogManager(PlmLogManager plmLogManager) {
        this.plmLogManager = plmLogManager;
    }

    @Resource
    public void setPlmSprintManager(PlmSprintManager plmSprintManager) {
        this.plmSprintManager = plmSprintManager;
    }

    @Resource
    public void setPlmConfigManager(PlmConfigManager plmConfigManager) {
        this.plmConfigManager = plmConfigManager;
    }

    @Resource
    public void setPlmRequirementManager(
            PlmRequirementManager plmRequirementManager) {
        this.plmRequirementManager = plmRequirementManager;
    }

    @Resource
    public void setPlmLogService(PlmLogService plmLogService) {
        this.plmLogService = plmLogService;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
