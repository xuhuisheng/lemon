package com.mossle.bpm.web.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bpm/rs")
public class BpmRestController {
    private static Logger logger = LoggerFactory
            .getLogger(BpmRestController.class);
    private ProcessEngine processEngine;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private HumanTaskConnector humanTaskConnector;
    private ModelConnector modelConnector;

    @RequestMapping("bpm-counts")
    public BaseDTO bpmCounts() {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Map<String, Long> countMap = new HashMap<String, Long>();
        BaseDTO baseDto = new BaseDTO();
        baseDto.setData(countMap);
        countMap.put("active", this.findActiveCount(userId, tenantId));
        countMap.put("draft", this.findDraftCount(userId, tenantId));
        countMap.put("personal", this.findPersonalCount(userId, tenantId));
        countMap.put("group", this.findGroupCount(userId, tenantId));

        return baseDto;
    }

    public long findActiveCount(String userId, String tenantId) {
        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .unfinished().count();
    }

    public long findDraftCount(String userId, String tenantId) {
        return modelConnector.findDraftCount(userId);
    }

    public long findPersonalCount(String userId, String tenantId) {
        return humanTaskConnector.findPersonalTaskCount(userId, tenantId);
    }

    public long findGroupCount(String userId, String tenantId) {
        return humanTaskConnector.findGroupTaskCount(userId, tenantId);
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setModelConnector(ModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }
}
