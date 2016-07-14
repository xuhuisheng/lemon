package com.mossle.bpm.support;

import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class JumpInfo {
    /**
     * source task id.
     */
    private String sourceTaskId;
    private TaskEntity sourceTask;

    /**
     * source activity id.
     */
    private String sourceActivityId;
    private ActivityImpl sourceActivity;

    /**
     * source history task instance id.
     */
    private String sourceHistoryTaskId;
    private HistoricTaskInstanceEntity sourceHistoryTask;

    /**
     * source history activity instance id.
     */
    private String sourceHistoryActivityId;
    private HistoricActivityInstanceEntity sourceHistoryActivity;

    /**
     * target activity id.
     */
    private String targetActivityId;
    private ActivityImpl targetActivity;

    /**
     * target history task instance id.
     */
    private String targetHistoryTaskId;
    private HistoricTaskInstanceEntity targetHistoryTask;

    /**
     * target history activity instance id.
     */
    private String targetHistoryActivityId;
    private HistoricActivityInstanceEntity targetHistoryActivity;

    /**
     * source task id.
     */
    public String getSourceTaskId() {
        return sourceTaskId;
    }

    public void setSourceTaskId(String sourceTaskId) {
        this.sourceTaskId = sourceTaskId;
    }

    public TaskEntity getSourceTask() {
        return sourceTask;
    }

    public void setSourceTask(TaskEntity sourceTask) {
        this.sourceTask = sourceTask;
    }

    /**
     * source activity id.
     */
    public String getSourceActivityId() {
        return sourceActivityId;
    }

    public void setSourceActivityId(String sourceActivityId) {
        this.sourceActivityId = sourceActivityId;
    }

    public ActivityImpl getSourceActivity() {
        return sourceActivity;
    }

    public void setSourceActivity(ActivityImpl sourceActivity) {
        this.sourceActivity = sourceActivity;
    }

    /**
     * source history task instance id.
     */
    public String getSourceHistoryTaskId() {
        return sourceHistoryTaskId;
    }

    public void setSourceHistoryTaskId(String sourceHistoryTaskId) {
        this.sourceHistoryTaskId = sourceHistoryTaskId;
    }

    public HistoricTaskInstanceEntity getSourceHistoryTask() {
        return sourceHistoryTask;
    }

    public void setSourceHistoryTask(
            HistoricTaskInstanceEntity sourceHistoryTask) {
        this.sourceHistoryTask = sourceHistoryTask;
    }

    /**
     * source history activity instance id.
     */
    public String getSourceHistoryActivityId() {
        return sourceHistoryActivityId;
    }

    public void setSourceHistoryActivityId(String sourceHistoryActivityId) {
        this.sourceHistoryActivityId = sourceHistoryActivityId;
    }

    public HistoricActivityInstanceEntity getSourceHistoryActivity() {
        return sourceHistoryActivity;
    }

    public void setSourceHistoryActivity(
            HistoricActivityInstanceEntity sourceHistoryActivity) {
        this.sourceHistoryActivity = sourceHistoryActivity;
    }

    /**
     * target activity id.
     */
    public String getTargetActivityId() {
        return targetActivityId;
    }

    public void setTargetActivityId(String targetActivityId) {
        this.targetActivityId = targetActivityId;
    }

    public ActivityImpl getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(ActivityImpl targetActivity) {
        this.targetActivity = targetActivity;
    }

    /**
     * target history task instance id.
     */
    public String getTargetHistoryTaskId() {
        return targetHistoryTaskId;
    }

    public void setTargetHistoryTaskId(String targetHistoryTaskId) {
        this.targetHistoryTaskId = targetHistoryTaskId;
    }

    public HistoricTaskInstanceEntity getTargetHistoryTask() {
        return targetHistoryTask;
    }

    public void setTargetHistoryTask(
            HistoricTaskInstanceEntity targetHistoryTask) {
        this.targetHistoryTask = targetHistoryTask;
    }

    /**
     * target history activity instance id.
     */
    public String getTargetHistoryActivityId() {
        return targetHistoryActivityId;
    }

    public void setTargetHistoryActivityId(String targetHistoryActivityId) {
        this.targetHistoryActivityId = targetHistoryActivityId;
    }

    public HistoricActivityInstanceEntity getTargetHistoryActivity() {
        return targetHistoryActivity;
    }

    public void setTargetHistoryActivity(
            HistoricActivityInstanceEntity targetHistoryActivity) {
        this.targetHistoryActivity = targetHistoryActivity;
    }
}
