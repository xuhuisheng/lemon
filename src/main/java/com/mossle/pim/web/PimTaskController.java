package com.mossle.pim.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.mapper.BeanMapper;

import com.mossle.pim.persistence.domain.PimTask;
import com.mossle.pim.persistence.manager.PimTaskManager;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("pim")
public class PimTaskController {
    private PimTaskManager pimTaskManager;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("pim-task-index")
    public String index(Model model) {
        String userId = currentUserHolder.getUserId();

        model.addAttribute("todayTasks", this.findTodayTasks(userId));
        model.addAttribute("tomorrowTasks", this.findTomorrowTasks(userId));
        model.addAttribute("fiveDayTasks", this.findFiveDayTasks(userId));

        return "pim/pim-task-index";
    }

    @RequestMapping("pim-task-create")
    public String create(@RequestParam("name") String name) {
        String userId = currentUserHolder.getUserId();
        Date now = new Date();
        PimTask pimTask = new PimTask();
        pimTask.setName(name);
        pimTask.setStartTime(now);
        pimTask.setCreateTime(now);
        pimTask.setUserId(userId);
        pimTask.setPriority(5);
        pimTask.setStatus("active");
        pimTaskManager.save(pimTask);

        return "redirect:/pim/pim-task-index.do";
    }

    @RequestMapping("pim-task-complete")
    public String complete(@RequestParam("id") Long id) {
        PimTask pimTask = pimTaskManager.get(id);
        pimTask.setStatus("completed");
        pimTask.setPriority(pimTask.getPriority() + 10);
        pimTaskManager.save(pimTask);

        return "redirect:/pim/pim-task-index.do";
    }

    @RequestMapping("pim-task-reopen")
    public String reopen(@RequestParam("id") Long id) {
        PimTask pimTask = pimTaskManager.get(id);
        pimTask.setStatus("active");
        pimTask.setPriority(pimTask.getPriority() - 10);
        pimTaskManager.save(pimTask);

        return "redirect:/pim/pim-task-index.do";
    }

    @RequestMapping("pim-task-remove")
    public String remove(@RequestParam("id") Long id) {
        pimTaskManager.removeById(id);

        return "redirect:/pim/pim-task-index.do";
    }

    @RequestMapping("pim-task-input")
    public String input(@RequestParam("id") Long id, Model model) {
        PimTask pimTask = pimTaskManager.get(id);
        model.addAttribute("model", pimTask);

        return "pim/pim-task-input";
    }

    @RequestMapping("pim-task-save")
    public String save(@ModelAttribute PimTask pimTask) {
        Long id = pimTask.getId();
        PimTask dest = pimTaskManager.get(id);
        beanMapper.copy(pimTask, dest);
        pimTaskManager.save(dest);

        return "redirect:/pim/pim-task-index.do";
    }

    public List<PimTask> findTodayTasks(String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startTime = calendar.getTime();
        calendar.add(Calendar.DATE, 1);

        Date endTime = calendar.getTime();
        String hql = "from PimTask where userId=? and startTime between ? and ? order by priority";

        return pimTaskManager.find(hql, userId, startTime, endTime);
    }

    public List<PimTask> findTomorrowTasks(String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startTime = calendar.getTime();
        calendar.add(Calendar.DATE, 1);

        Date endTime = calendar.getTime();
        String hql = "from PimTask where userId=? and startTime between ? and ? order by priority";

        return pimTaskManager.find(hql, userId, startTime, endTime);
    }

    public List<PimTask> findFiveDayTasks(String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startTime = calendar.getTime();
        calendar.add(Calendar.DATE, 5);

        Date endTime = calendar.getTime();
        String hql = "from PimTask where userId=? and startTime between ? and ? order by priority";

        return pimTaskManager.find(hql, userId, startTime, endTime);
    }

    // ~ ======================================================================
    @Resource
    public void setPimTaskManager(PimTaskManager pimTaskManager) {
        this.pimTaskManager = pimTaskManager;
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
