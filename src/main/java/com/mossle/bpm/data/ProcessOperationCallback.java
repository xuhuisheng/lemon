package com.mossle.bpm.data;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;

import com.mossle.core.csv.CsvCallback;

import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessOperationCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessOperationCallback.class);
    private BpmConfBaseManager bpmConfBaseManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private String defaultTenantId = "1";
    private TaskDefinitionConnector taskDefinitionConnector;

    public void process(List<String> list, int lineNo) throws Exception {
        String processDefinitionKey = list.get(0);
        String activityId = list.get(1);
        String value = list.get(2);

        BpmConfNode bpmConfNode = this.findBpmConfNode(processDefinitionKey,
                activityId);

        if (bpmConfNode == null) {
            logger.info("cannot find bpm conf node : {} {}, skip",
                    processDefinitionKey, activityId);

            return;
        }

        String processDefinitionId = this.findBpmConfBase(processDefinitionKey)
                .getProcessDefinitionId();

        for (String operation : value.split(" ")) {
            operation = operation.trim();

            BpmConfOperation bpmConfOperation = new BpmConfOperation();
            bpmConfOperation.setValue(operation);
            bpmConfOperation.setPriority(0);
            bpmConfOperation.setBpmConfNode(bpmConfNode);
            bpmConfOperationManager.save(bpmConfOperation);
            //
            taskDefinitionConnector.addOperation(activityId,
                    processDefinitionId, operation);
        }
    }

    public BpmConfNode findBpmConfNode(String processDefinitionKey,
            String activityId) {
        BpmConfBase bpmConfBase = this.findBpmConfBase(processDefinitionKey);

        if (bpmConfBase == null) {
            logger.info("cannot find bpm conf base : {}, skip",
                    processDefinitionKey);

            return null;
        }

        String hql = "from BpmConfNode where code=? and bpmConfBase=?";
        BpmConfNode bpmConfNode = bpmConfNodeManager.findUnique(hql,
                activityId, bpmConfBase);

        return bpmConfNode;
    }

    public BpmConfBase findBpmConfBase(String processDefinitionKey) {
        String hql = "from BpmConfBase where processDefinitionKey=? order by processDefinitionVersion desc";
        BpmConfBase bpmConfBase = bpmConfBaseManager.findUnique(hql,
                processDefinitionKey);

        return bpmConfBase;
    }

    // ~
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
    }

    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
