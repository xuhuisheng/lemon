package com.mossle.internal.delegate.web;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.page.Page;

import com.mossle.internal.delegate.persistence.domain.DelegateHistory;
import com.mossle.internal.delegate.persistence.domain.DelegateInfo;
import com.mossle.internal.delegate.persistence.manager.DelegateHistoryManager;
import com.mossle.internal.delegate.persistence.manager.DelegateInfoManager;
import com.mossle.internal.delegate.service.DelegateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("delegate")
public class DelegateController {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateController.class);
    private JdbcTemplate jdbcTemplate;
    private DelegateService delegateService;
    private DelegateInfoManager delegateInfoManager;
    private DelegateHistoryManager delegateHistoryManager;
    private UserConnector userConnector;
    private CurrentUserHolder currentUserHolder;
    private ProcessConnector processConnector;
    private TenantHolder tenantHolder;

    /**
     * 自动委托列表 TODO 可以指定多个自动委托人？
     * 
     * @return
     */
    @RequestMapping("delegate-listMyDelegateInfos")
    public String listMyDelegateInfos(Model model) {
        String userId = currentUserHolder.getUserId();
        List<DelegateInfo> delegateInfos = delegateInfoManager.findBy(
                "assignee", userId);
        model.addAttribute("delegateInfos", delegateInfos);

        return "delegate/delegate-listMyDelegateInfos";
    }

    /**
     * 删除自动委托
     * 
     * @return
     */
    @RequestMapping("delegate-removeDelegateInfo")
    public String removeDelegateInfo(@RequestParam("id") Long id) {
        delegateService.removeRecord(id);

        return "redirect:/delegate/delegate-listMyDelegateInfos.do";
    }

    // ~ ======================================================================
    /**
     * 自动委托页面
     * 
     * @return
     */
    @RequestMapping("delegate-prepareAutoDelegate")
    public String prepareAutoDelegate(Model model) {
        String tenantId = tenantHolder.getTenantId();
        Page page = processConnector.findProcessDefinitions(tenantId, new Page(
                1, 100));
        model.addAttribute("page", page);

        return "delegate/delegate-prepareAutoDelegate";
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
            @RequestParam("taskDefinitionKey") String taskDefinitionKey,
            @RequestParam("attorney") String attorney) throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        if ((processDefinitionId != null)
                && "".equals(processDefinitionId.trim())) {
            processDefinitionId = null;
        }

        if ((taskDefinitionKey != null) && "".equals(taskDefinitionKey.trim())) {
            taskDefinitionKey = null;
        }

        delegateService.addDelegateInfo(userId, attorney, startTime, endTime,
                processDefinitionId, taskDefinitionKey, tenantId);

        return "redirect:/delegate/delegate-listMyDelegateInfos.do";
    }

    // ~ ======================================================================
    /**
     * 自动委派
     */
    @RequestMapping("delegate-listDelegateInfos")
    public String listDelegateInfos(Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<DelegateInfo> delegateInfos = delegateInfoManager.findBy(
                "tenantId", tenantId);
        model.addAttribute("delegateInfos", delegateInfos);

        return "delegate/delegate-listDelegateInfos";
    }

    /**
     * 自动委托历史
     * 
     * @return
     */
    @RequestMapping("delegate-listDelegateHistories")
    public String listDelegateHistories(Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<DelegateHistory> delegateHistories = delegateHistoryManager
                .findBy("tenantId", tenantId);
        model.addAttribute("delegateHistories", delegateHistories);

        return "delegate/delegate-listDelegateHistories";
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
    public void setDelegateInfoManager(DelegateInfoManager delegateInfoManager) {
        this.delegateInfoManager = delegateInfoManager;
    }

    @Resource
    public void setDelegateHistoryManager(
            DelegateHistoryManager delegateHistoryManager) {
        this.delegateHistoryManager = delegateHistoryManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
