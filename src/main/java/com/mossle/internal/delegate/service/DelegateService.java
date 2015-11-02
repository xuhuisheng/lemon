package com.mossle.internal.delegate.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.internal.delegate.persistence.domain.DelegateHistory;
import com.mossle.internal.delegate.persistence.domain.DelegateInfo;
import com.mossle.internal.delegate.persistence.manager.DelegateHistoryManager;
import com.mossle.internal.delegate.persistence.manager.DelegateInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class DelegateService {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateService.class);
    private DelegateInfoManager delegateInfoManager;
    private DelegateHistoryManager delegateHistoryManager;

    public DelegateInfo getDelegateInfo(String targetAssignee,
            String targetProcessDefinitionId, String targetTaskDefinitionKey,
            String tenantId) {
        String hql = "from DelegateInfo where assignee=? and status=1 and tenantId=? order by id desc";
        List<DelegateInfo> list = delegateInfoManager.find(hql, targetAssignee,
                tenantId);

        for (DelegateInfo delegateInfo : list) {
            logger.debug("delegateInfo : {}", delegateInfo);

            Long id = delegateInfo.getId();
            String assignee = delegateInfo.getAssignee();
            String attorney = delegateInfo.getAttorney();
            String processDefinitionId = delegateInfo.getProcessDefinitionId();
            String taskDefinitionKey = delegateInfo.getTaskDefinitionKey();
            Date startTime = delegateInfo.getStartTime();
            Date endTime = delegateInfo.getEndTime();
            Integer status = delegateInfo.getStatus();

            if (timeNotBetweenNow(startTime, endTime)) {
                logger.info("timeNotBetweenNow");

                continue;
            }

            if ((processDefinitionId != null)
                    && (!processDefinitionId.equals(targetProcessDefinitionId))) {
                logger.info("processDefinitionId not matches");

                continue;
            }

            if ((taskDefinitionKey != null)
                    && (!taskDefinitionKey.equals(targetTaskDefinitionKey))) {
                logger.info("taskDefinitionKey not matches");

                continue;
            }

            logger.info("delegate to {}", attorney);

            return delegateInfo;
        }

        return null;
    }

    public void saveRecord(String assignee, String attorney, String taskId,
            String tenantId) {
        DelegateHistory delegateHistory = new DelegateHistory();
        delegateHistory.setAssignee(assignee);
        delegateHistory.setAttorney(attorney);
        delegateHistory.setDelegateTime(new Date());
        delegateHistory.setTaskId(taskId);
        delegateHistory.setStatus(1);
        delegateHistory.setTenantId(tenantId);
        delegateHistoryManager.save(delegateHistory);
    }

    public void removeRecord(Long id) {
        delegateInfoManager.removeById(id);
    }

    public void addDelegateInfo(String assignee, String attorney,
            Date startTime, Date endTime, String processDefinitionId,
            String taskDefinitionKey, String tenantId) {
        DelegateInfo delegateInfo = new DelegateInfo();
        delegateInfo.setAssignee(assignee);
        delegateInfo.setAttorney(attorney);
        delegateInfo.setStartTime(startTime);
        delegateInfo.setEndTime(endTime);
        delegateInfo.setProcessDefinitionId(processDefinitionId);
        delegateInfo.setTaskDefinitionKey(taskDefinitionKey);
        delegateInfo.setStatus(1);
        delegateInfo.setTenantId(tenantId);
        delegateInfoManager.save(delegateInfo);
    }

    private boolean timeNotBetweenNow(Date startTime, Date endTime) {
        Date now = new Date();

        if (startTime != null) {
            return now.before(startTime);
        }

        if (endTime != null) {
            return now.after(endTime);
        }

        return false;
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
}
