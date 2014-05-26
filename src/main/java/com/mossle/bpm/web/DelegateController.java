package com.mossle.bpm.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.delegate.DelegateHistory;
import com.mossle.bpm.delegate.DelegateInfo;
import com.mossle.bpm.delegate.DelegateService;
import com.mossle.bpm.persistence.domain.BpmDelegateHistory;
import com.mossle.bpm.persistence.domain.BpmDelegateInfo;
import com.mossle.bpm.persistence.manager.BpmDelegateHistoryManager;
import com.mossle.bpm.persistence.manager.BpmDelegateInfoManager;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("bpm")
public class DelegateController {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateController.class);
    private ProcessEngine processEngine;
    private JdbcTemplate jdbcTemplate;
    private DelegateService delegateService;
    private BpmDelegateInfoManager bpmDelegateInfoManager;
    private BpmDelegateHistoryManager bpmDelegateHistoryManager;
    private UserConnector userConnector;

    /**
     * 自动委托列表 TODO 可以指定多个自动委托人？
     * 
     * @return
     */
    @RequestMapping("delegate-listMyDelegateInfos")
    public String listMyDelegateInfos(Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<BpmDelegateInfo> bpmDelegateInfos = bpmDelegateInfoManager.findBy(
                "assignee", userId);
        model.addAttribute("bpmDelegateInfos", bpmDelegateInfos);

        return "bpm/delegate-listMyDelegateInfos";
    }

    /**
     * 删除自动委托
     * 
     * @return
     */
    @RequestMapping("delegate-removeDelegateInfo")
    public String removeDelegateInfo(@RequestParam("id") Long id) {
        delegateService.removeRecord(id);

        return "redirect:/bpm/delegate-listMyDelegateInfos.do";
    }

    // ~ ======================================================================
    /**
     * 自动委托页面
     * 
     * @return
     */
    @RequestMapping("delegate-prepareAutoDelegate")
    public String prepareAutoDelegate(Model model) {
        List<ProcessDefinition> processDefinitions = processEngine
                .getRepositoryService().createProcessDefinitionQuery().list();
        model.addAttribute("processDefinitions", processDefinitions);

        return "bpm/delegate-prepareAutoDelegate";
    }

    /**
     * 自动委托
     * 
     * @return
     */
    @RequestMapping("delegate-autoDelegate")
    public String autoDelegate(
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestParam("processDefinitionId") String processDefinitionId,
            @RequestParam("attorney") String attorney) throws Exception {
        String userId = SpringSecurityUtils.getCurrentUserId();

        if ((processDefinitionId != null)
                && "".equals(processDefinitionId.trim())) {
            processDefinitionId = null;
        }

        delegateService.addDelegateInfo(userId, attorney, startTime, endTime,
                processDefinitionId);

        return "redirect:/bpm/delegate-listMyDelegateInfos.do";
    }

    // ~ ======================================================================
    /**
     * 自动委派
     */
    @RequestMapping("delegate-listDelegateInfos")
    public String listDelegateInfos(Model model) {
        List<BpmDelegateInfo> bpmDelegateInfos = bpmDelegateInfoManager
                .getAll();
        model.addAttribute("bpmDelegateInfos", bpmDelegateInfos);

        return "bpm/delegate-listDelegateInfos";
    }

    /**
     * 自动委托历史
     * 
     * @return
     */
    @RequestMapping("delegate-listDelegateHistories")
    public String listDelegateHistories(Model model) {
        List<BpmDelegateHistory> bpmDelegateHistories = bpmDelegateHistoryManager
                .getAll();
        model.addAttribute("bpmDelegateHistories", bpmDelegateHistories);

        return "bpm/delegate-listDelegateHistories";
    }

    // ~ ==================================================
    @Resource
    public void setDelegateService(DelegateService delegateService) {
        this.delegateService = delegateService;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmDelegateInfoManager(
            BpmDelegateInfoManager bpmDelegateInfoManager) {
        this.bpmDelegateInfoManager = bpmDelegateInfoManager;
    }

    @Resource
    public void setBpmDelegateHistoryManager(
            BpmDelegateHistoryManager bpmDelegateHistoryManager) {
        this.bpmDelegateHistoryManager = bpmDelegateHistoryManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
