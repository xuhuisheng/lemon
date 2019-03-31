package com.mossle.pim.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.mapper.BeanMapper;

import com.mossle.pim.persistence.domain.PimNote;
import com.mossle.pim.persistence.domain.PimPlan;
import com.mossle.pim.persistence.domain.PimRemind;
import com.mossle.pim.persistence.domain.PimSchedule;
import com.mossle.pim.persistence.domain.PimTask;
import com.mossle.pim.persistence.manager.PimNoteManager;
import com.mossle.pim.persistence.manager.PimPlanManager;
import com.mossle.pim.persistence.manager.PimRemindManager;
import com.mossle.pim.persistence.manager.PimScheduleManager;
import com.mossle.pim.persistence.manager.PimTaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("pim")
public class PimController {
    private static Logger logger = LoggerFactory.getLogger(PimController.class);
    private PimScheduleManager pimScheduleManager;
    private PimTaskManager pimTaskManager;
    private PimNoteManager pimNoteManager;
    private PimRemindManager pimRemindManager;
    private PimPlanManager pimPlanManager;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("index")
    public String index(Model model) {
        String userId = currentUserHolder.getUserId();

        Date today = this.findToday();
        Date tomorrow = this.findTomorrow();
        Date thirdDay = this.findThirdDay();
        Date nextWeek = this.findNextWeek();

        model.addAttribute("today", today);
        model.addAttribute("tomorrow", tomorrow);

        this.findSchedules(model, "todaySchedules", today, tomorrow);
        this.findSchedules(model, "tomorrowSchedules", tomorrow, thirdDay);
        this.findSchedules(model, "nextWeekSchedules", thirdDay, nextWeek);

        this.findTasks(model, "tasks");
        this.findPlans(model, "plans");
        this.findNotes(model, "notes");
        this.findReminds(model, "reminds");

        return "pim/index";
    }

    public void findSchedules(Model model, String key, Date startTime,
            Date endTime) {
        logger.debug("start : {}, end : {}", startTime, endTime);

        String userId = currentUserHolder.getUserId();
        String hql = "from PimSchedule where userId=? and startTime>=? and startTime<=?";
        List<PimSchedule> pimSchedules = this.pimScheduleManager.find(hql,
                userId, startTime, endTime);
        model.addAttribute(key, pimSchedules);
    }

    public void findTasks(Model model, String key) {
        String userId = currentUserHolder.getUserId();
        String hql = "from PimTask where userId=? and status='active'";
        List<PimTask> pimTasks = this.pimTaskManager.find(hql, userId);
        model.addAttribute(key, pimTasks);
    }

    public void findPlans(Model model, String key) {
        String userId = currentUserHolder.getUserId();
        String hql = "from PimPlan where userId=? and status='active'";

        List<PimPlan> pimPlans = this.pimPlanManager.find(hql, userId);
        model.addAttribute(key, pimPlans);
    }

    public void findNotes(Model model, String key) {
        String userId = currentUserHolder.getUserId();
        String hql = "from PimNote where userId=? and status='active'";
        List<PimNote> pimNotes = this.pimNoteManager.find(hql, userId);
        model.addAttribute(key, pimNotes);
    }

    public void findReminds(Model model, String key) {
        String userId = currentUserHolder.getUserId();
        String hql = "from PimRemind where userId=? and status='active'";
        List<PimRemind> pimReminds = this.pimRemindManager.find(hql, userId);
        model.addAttribute(key, pimReminds);
    }

    public Date findToday() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public Date findTomorrow() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }

    public Date findThirdDay() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DATE, 2);

        return calendar.getTime();
    }

    public Date findNextWeek() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DATE, 7);

        return calendar.getTime();
    }

    // ~ ======================================================================
    @Resource
    public void setPimScheduleManager(PimScheduleManager pimScheduleManager) {
        this.pimScheduleManager = pimScheduleManager;
    }

    @Resource
    public void setPimTaskManager(PimTaskManager pimTaskManager) {
        this.pimTaskManager = pimTaskManager;
    }

    @Resource
    public void setPimNoteManager(PimNoteManager pimNoteManager) {
        this.pimNoteManager = pimNoteManager;
    }

    @Resource
    public void setPimRemindManager(PimRemindManager pimRemindManager) {
        this.pimRemindManager = pimRemindManager;
    }

    @Resource
    public void setPimPlanManager(PimPlanManager pimPlanManager) {
        this.pimPlanManager = pimPlanManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
