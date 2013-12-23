package com.mossle.bpm.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.mossle.bpm.cmd.SendNoticeCmd;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskDefNotice;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefNoticeManager;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTimeoutJob {
    private static Logger logger = LoggerFactory
            .getLogger(TaskTimeoutJob.class);
    public static final int TYPE_ARRIVAL = 0;
    public static final int TYPE_COMPLETE = 1;
    public static final int TYPE_TIMEOUT = 2;
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmTaskDefNoticeManager bpmTaskDefNoticeManager;

    public void execute() throws Exception {
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .list();

        for (Task task : tasks) {
            if (task.getDueDate() != null) {
                processTimeout(task);
            }
        }
    }

    public void processTimeout(Task task) throws Exception {
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        BpmProcess bpmProcess = bpmProcessManager
                .findUnique(
                        "from BpmProcess where processDefinitionKey=? and processDefinitionVersion=?",
                        processDefinition.getKey(),
                        processDefinition.getVersion());
        List<BpmTaskDefNotice> bpmTaskDefNotices = bpmTaskDefNoticeManager
                .find("from BpmTaskDefNotice where taskDefinitionKey=? and bpmProcess=?",
                        taskDefinitionKey, bpmProcess);

        for (BpmTaskDefNotice bpmTaskDefNotice : bpmTaskDefNotices) {
            if (TYPE_TIMEOUT == bpmTaskDefNotice.getType()) {
                processTimeout(task, bpmTaskDefNotice);
            }
        }
    }

    public void processTimeout(Task task, BpmTaskDefNotice bpmTaskDefNotice)
            throws Exception {
        Date dueDate = task.getDueDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDate);

        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        Duration duration = datatypeFactory.newDuration("-"
                + bpmTaskDefNotice.getDueDate());
        duration.addTo(calendar);

        Date noticeDate = calendar.getTime();
        Date now = new Date();

        if ((now.getTime() < noticeDate.getTime())
                && ((noticeDate.getTime() - now.getTime()) < (60 * 1000))) {
            SendNoticeCmd sendNoticeCmd = new SendNoticeCmd(task.getId(),
                    bpmTaskDefNotice.getReceiver(),
                    bpmTaskDefNotice.getTemplate());
            processEngine.getManagementService().executeCommand(sendNoticeCmd);
        }
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmTaskDefNoticeManager(
            BpmTaskDefNoticeManager bpmTaskDefNoticeManager) {
        this.bpmTaskDefNoticeManager = bpmTaskDefNoticeManager;
    }
}
