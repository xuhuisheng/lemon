package com.mossle.bpm.web.bpm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.delegate.DelegateHistory;
import com.mossle.bpm.delegate.DelegateInfo;
import com.mossle.bpm.delegate.DelegateService;
import com.mossle.bpm.persistence.domain.BpmDelegateHistory;
import com.mossle.bpm.persistence.domain.BpmDelegateInfo;
import com.mossle.bpm.persistence.manager.BpmDelegateHistoryManager;
import com.mossle.bpm.persistence.manager.BpmDelegateInfoManager;

import com.mossle.core.struts2.BaseAction;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 自动委托
 * 
 * @author LuZhao
 */
@Results({ @Result(name = DelegateAction.RELOAD, location = "delegate!listMyDelegateInfos.do?operationMode=RETRIEVE", type = "redirect") })
public class DelegateAction extends BaseAction {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateAction.class);
    public static final String RELOAD = "reload";
    private ProcessEngine processEngine;
    private JdbcTemplate jdbcTemplate;
    private DelegateService delegateService;
    private BpmDelegateInfoManager bpmDelegateInfoManager;
    private BpmDelegateHistoryManager bpmDelegateHistoryManager;
    private UserConnector userConnector;
    private String attorney;
    private Long id;
    private String startTime;
    private String endTime;
    private String processDefinitionId;
    private List<ProcessDefinition> processDefinitions;
    private List<DelegateInfo> delegateInfos;
    private List<BpmDelegateInfo> bpmDelegateInfos;
    private List<DelegateHistory> delegateHistories;
    private List<BpmDelegateHistory> bpmDelegateHistories;

    /**
     * 自动委托列表 TODO 可以指定多个自动委托人？
     * 
     * @return
     */
    public String listMyDelegateInfos() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        bpmDelegateInfos = bpmDelegateInfoManager.findBy("assignee", userId);
        this.processDelegateInfo();

        return "listMyDelegateInfos";
    }

    /**
     * 删除自动委托
     * 
     * @return
     */
    public String removeDelegateInfo() {
        delegateService.removeRecord(id);

        return RELOAD;
    }

    // ~ ======================================================================
    /**
     * 自动委托页面
     * 
     * @return
     */
    public String prepareAutoDelegate() {
        processDefinitions = processEngine.getRepositoryService()
                .createProcessDefinitionQuery().list();

        return "prepareAutoDelegate";
    }

    /**
     * 自动委托
     * 
     * @return
     */
    public String autoDelegate() throws Exception {
        String userId = SpringSecurityUtils.getCurrentUserId();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date startDate = null;

        try {
            startDate = dateFormat.parse(startTime);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        Date endDate = null;

        try {
            endDate = dateFormat.parse(endTime);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        if ((processDefinitionId != null)
                && "".equals(processDefinitionId.trim())) {
            processDefinitionId = null;
        }

        delegateService.addDelegateInfo(userId, attorney, startDate, endDate,
                processDefinitionId);

        return RELOAD;
    }

    // ~ ======================================================================
    /**
     * 自动委派
     */
    public String listDelegateInfos() {
        bpmDelegateInfos = bpmDelegateInfoManager.getAll();
        this.processDelegateInfo();

        return "listDelegateInfos";
    }

    /**
     * 自动委托历史
     * 
     * @return
     */
    public String listDelegateHistories() {
        bpmDelegateHistories = bpmDelegateHistoryManager.getAll();
        this.processDelegateHistory();

        return "listDelegateHistories";
    }

    public void processDelegateInfo() {
        delegateInfos = new ArrayList<DelegateInfo>();

        for (BpmDelegateInfo bpmDelegateInfo : bpmDelegateInfos) {
            DelegateInfo delegateInfo = new DelegateInfo();
            delegateInfos.add(delegateInfo);
            delegateInfo.setId(bpmDelegateInfo.getId());
            delegateInfo.setAssignee(bpmDelegateInfo.getAssignee());
            delegateInfo.setAttorney(bpmDelegateInfo.getAttorney());
            delegateInfo.setStartTime(bpmDelegateInfo.getStartTime());
            delegateInfo.setEndTime(bpmDelegateInfo.getEndTime());
            delegateInfo.setProcessDefinitionId(bpmDelegateInfo
                    .getProcessDefinitionId());
            delegateInfo.setStatus(bpmDelegateInfo.getStatus());
            delegateInfo.setAssigneeDisplayName(userConnector.findById(
                    delegateInfo.getAssignee()).getDisplayName());
            delegateInfo.setAttorneyDisplayName(userConnector.findById(
                    delegateInfo.getAttorney()).getDisplayName());
        }
    }

    public void processDelegateHistory() {
        delegateHistories = new ArrayList<DelegateHistory>();

        for (BpmDelegateHistory bpmDelegateHistory : bpmDelegateHistories) {
            DelegateHistory delegateHistory = new DelegateHistory();
            delegateHistories.add(delegateHistory);
            delegateHistory.setId(bpmDelegateHistory.getId());
            delegateHistory.setAssignee(bpmDelegateHistory.getAssignee());
            delegateHistory.setAttorney(bpmDelegateHistory.getAttorney());
            delegateHistory.setDelegateTime(bpmDelegateHistory
                    .getDelegateTime());
            delegateHistory.setTaskId(bpmDelegateHistory.getTaskId());
            delegateHistory.setStatus(bpmDelegateHistory.getStatus());
            delegateHistory.setAssigneeDisplayName(userConnector.findById(
                    bpmDelegateHistory.getAssignee()).getDisplayName());
            delegateHistory.setAttorneyDisplayName(userConnector.findById(
                    bpmDelegateHistory.getAttorney()).getDisplayName());
        }
    }

    // ~ ==================================================
    public void setDelegateService(DelegateService delegateService) {
        this.delegateService = delegateService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setBpmDelegateInfoManager(
            BpmDelegateInfoManager bpmDelegateInfoManager) {
        this.bpmDelegateInfoManager = bpmDelegateInfoManager;
    }

    public void setBpmDelegateHistoryManager(
            BpmDelegateHistoryManager bpmDelegateHistoryManager) {
        this.bpmDelegateHistoryManager = bpmDelegateHistoryManager;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    // ~ ==================================================
    public void setAttorney(String attorney) {
        this.attorney = attorney;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public List<ProcessDefinition> getProcessDefinitions() {
        return processDefinitions;
    }

    public List<DelegateInfo> getDelegateInfos() {
        return delegateInfos;
    }

    public List<DelegateHistory> getDelegateHistories() {
        return delegateHistories;
    }
}
