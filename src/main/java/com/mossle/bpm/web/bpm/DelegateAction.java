package com.mossle.bpm.web.bpm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.bpm.delegate.DelegateService;

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
 * 
 */
@Results({ @Result(name = DelegateAction.RELOAD, location = "delegate!listMyDelegateInfos.do?operationMode=RETRIEVE", type = "redirect") })
public class DelegateAction extends BaseAction {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateAction.class);
    public static final String RELOAD = "reload";
    private ProcessEngine processEngine;
    private JdbcTemplate jdbcTemplate;
    private DelegateService delegateService;
    private String attorney;
    private Long id;
    private List<Map<String, Object>> delegateInfos;
    private List<Map<String, Object>> delegateHistories;
    private String startTime;
    private String endTime;
    private String processDefinitionId;
    private List<ProcessDefinition> processDefinitions;

    /**
     * 自动委托列表 TODO 可以指定多个自动委托人？
     * 
     * @return
     */
    public String listMyDelegateInfos() {
        delegateInfos = jdbcTemplate.queryForList(
                "select * from bpm_delegate_info where assignee=?",
                SpringSecurityUtils.getCurrentUsername());

        return "listMyDelegateInfos";
    }

    /**
     * 删除自动委托
     * 
     * @return
     */
    public String removeDelegateInfo() {
        jdbcTemplate.update("delete from bpm_delegate_info where id=?", id);

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
        String username = SpringSecurityUtils.getCurrentUsername();
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

        delegateService.addDelegateInfo(username, attorney, startDate, endDate,
                processDefinitionId);

        return RELOAD;
    }

    // ~ ======================================================================
    /**
     * 自动委派
     */
    public String listDelegateInfos() {
        delegateInfos = jdbcTemplate
                .queryForList("select * from bpm_delegate_info");

        return "listDelegateInfos";
    }

    /**
     * 自动委托历史
     * 
     * @return
     */
    public String listDelegateHistories() {
        delegateHistories = jdbcTemplate
                .queryForList("select * from bpm_delegate_history");

        return "listDelegateHistories";
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

    public void setAttorney(String attorney) {
        this.attorney = attorney;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Map<String, Object>> getDelegateInfos() {
        return delegateInfos;
    }

    public List<Map<String, Object>> getDelegateHistories() {
        return delegateHistories;
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
}
