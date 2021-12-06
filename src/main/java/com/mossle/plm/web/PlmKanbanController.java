package com.mossle.plm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.plm.persistence.domain.PlmSprint;
import com.mossle.plm.persistence.domain.PlmStep;
import com.mossle.plm.persistence.manager.PlmIssueManager;
import com.mossle.plm.persistence.manager.PlmSprintManager;
import com.mossle.plm.persistence.manager.PlmStepManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("plm")
public class PlmKanbanController {
    private PlmIssueManager plmIssueManager;
    private PlmSprintManager plmSprintManager;
    private PlmStepManager plmStepManager;

    /**
     * 看板.
     */
    @RequestMapping("kanban")
    public String kanban(@RequestParam("sprintId") Long sprintId, Model model)
            throws Exception {
        // 获得迭代
        PlmSprint plmSprint = plmSprintManager.get(sprintId);
        model.addAttribute("plmSprint", plmSprint);

        if (plmSprint != null) {
            // 获得迭代对应的步骤
            String hql = "from PlmStep where plmConfig=? order by priority";
            List<PlmStep> plmSteps = plmStepManager.find(hql,
                    plmSprint.getPlmConfig());
            model.addAttribute("plmSteps", plmSteps);

            // 获得每个步骤下的任务
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            model.addAttribute("list", list);

            for (PlmStep plmStep : plmSteps) {
                Map<String, Object> map = new HashMap<String, Object>();
                list.add(map);
                map.put("plmStep", plmStep);

                String hqlSelectIssue = "from PlmIssue where plmSprint=? and step=?";
                map.put("plmIssues",
                        plmIssueManager.find(hqlSelectIssue, plmSprint,
                                plmStep.getCode()));
            }
        }

        return "plm/kanban";
    }

    // ~
    @Resource
    public void setPlmIssueManager(PlmIssueManager plmIssueManager) {
        this.plmIssueManager = plmIssueManager;
    }

    @Resource
    public void setPlmSprintManager(PlmSprintManager plmSprintManager) {
        this.plmSprintManager = plmSprintManager;
    }

    @Resource
    public void setPlmStepManager(PlmStepManager plmStepManager) {
        this.plmStepManager = plmStepManager;
    }
}
