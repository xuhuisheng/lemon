package com.mossle.workcal.web;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.workcal.domain.WorkcalPart;
import com.mossle.workcal.domain.WorkcalRule;
import com.mossle.workcal.manager.WorkcalPartManager;
import com.mossle.workcal.manager.WorkcalRuleManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workcal")
public class WorkcalController {
    private static Logger logger = LoggerFactory
            .getLogger(WorkcalController.class);
    public static final int STATUS_WEEK = 0;
    public static final int STATUS_HOLIDAY = 1;
    public static final int STATUS_HOLIDAY_TO_WORKDAY = 2;
    public static final int STATUS_WORKDAY_TO_HOLIDAY = 3;
    private WorkcalPartManager workcalPartManager;
    private WorkcalRuleManager workcalRuleManager;
    private JsonMapper jsonMapper = new JsonMapper();

    @RequestMapping("workcal-view")
    public String list(Model model) {
        // 每周的工作规则
        List<WorkcalRule> workcalRules = workcalRuleManager.findBy("status",
                STATUS_WEEK);
        Set<Integer> weeks = new HashSet<Integer>();

        for (WorkcalRule workcalRule : workcalRules) {
            weeks.add(Integer.valueOf(workcalRule.getWeek() - 1));
        }

        try {
            model.addAttribute("weeks", jsonMapper.toJson(weeks));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        // 特殊日期
        List<WorkcalRule> extraWorkcalRules = workcalRuleManager.find(
                "from WorkcalRule where status<>?", STATUS_WEEK);

        List<Map<String, String>> holidays = new ArrayList<Map<String, String>>();
        List<Map<String, String>> workdays = new ArrayList<Map<String, String>>();
        List<Map<String, String>> extrdays = new ArrayList<Map<String, String>>();

        for (WorkcalRule workcalRule : extraWorkcalRules) {
            Map<String, String> day = new HashMap<String, String>();
            day.put("date", dateFormat.format(workcalRule.getWorkDate()));
            day.put("name", workcalRule.getName());

            if (workcalRule.getStatus() == STATUS_HOLIDAY) {
                holidays.add(day);
            } else if (workcalRule.getStatus() == STATUS_HOLIDAY_TO_WORKDAY) {
                workdays.add(day);
            } else {
                extrdays.add(day);
            }
        }

        try {
            model.addAttribute("holidays", jsonMapper.toJson(holidays));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        try {
            model.addAttribute("workdays", jsonMapper.toJson(workdays));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        try {
            model.addAttribute("extrdays", jsonMapper.toJson(extrdays));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return "workcal/workcal-view";
    }

    // ~ ======================================================================
    @Resource
    public void setWorkcalPartManager(WorkcalPartManager workcalPartManager) {
        this.workcalPartManager = workcalPartManager;
    }

    @Resource
    public void setWorkcalRuleManager(WorkcalRuleManager workcalRuleManager) {
        this.workcalRuleManager = workcalRuleManager;
    }
}
