package com.mossle.plm.service;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.plm.persistence.domain.PlmComment;
import com.mossle.plm.persistence.domain.PlmIssue;
import com.mossle.plm.persistence.domain.PlmLog;
import com.mossle.plm.persistence.manager.PlmLogManager;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlmLogService {
    private PlmLogManager plmLogManager;

    public void issueCreated(PlmIssue plmIssue) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("create");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(plmIssue.getReporterId());
        plmLog.setPlmIssue(plmIssue);
        plmLogManager.save(plmLog);
    }

    public void issueUpdated(PlmIssue oldIssue, PlmIssue newIssue, String userId) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("update");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(userId);
        plmLog.setPlmIssue(newIssue);
        plmLogManager.save(plmLog);
    }

    public void commentCreated(PlmComment plmComment) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("create comment");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(plmComment.getUserId());
        plmLog.setPlmIssue(plmComment.getPlmIssue());
        plmLog.setContent(plmComment.getContent());
        plmLogManager.save(plmLog);
    }

    public void commentUpdated(PlmComment plmComment) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("update comment");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(plmComment.getUserId());
        plmLog.setPlmIssue(plmComment.getPlmIssue());
        plmLog.setContent(plmComment.getContent());
        plmLogManager.save(plmLog);
    }

    public void issueCompleted(PlmIssue plmIssue, String userId) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("complete issue");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(userId);
        plmLog.setPlmIssue(plmIssue);
        plmLogManager.save(plmLog);
    }

    public void issueReopened(PlmIssue plmIssue, String userId) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("reopen issue");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(userId);
        plmLog.setPlmIssue(plmIssue);
        plmLogManager.save(plmLog);
    }

    public void issueClaimed(PlmIssue plmIssue, String userId) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("claim issue");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(userId);
        plmLog.setPlmIssue(plmIssue);
        plmLogManager.save(plmLog);
    }

    public void issueAssigned(PlmIssue oldIssue, PlmIssue newIssue,
            String userId) {
        PlmLog plmLog = new PlmLog();
        plmLog.setType("assign issue");
        plmLog.setLogTime(new Date());
        plmLog.setUserId(userId);
        plmLog.setPlmIssue(newIssue);
        plmLog.setContent(oldIssue.getAssigneeId() + " to "
                + newIssue.getAssigneeId());
        plmLogManager.save(plmLog);
    }

    @Resource
    public void setPlmLogManager(PlmLogManager plmLogManager) {
        this.plmLogManager = plmLogManager;
    }
}
