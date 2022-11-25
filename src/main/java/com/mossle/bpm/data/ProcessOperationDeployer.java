package com.mossle.bpm.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessOperationDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessOperationDeployer.class);
    private BpmConfBaseManager bpmConfBaseManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private String defaultTenantId = "1";
    private TaskDefinitionConnector taskDefinitionConnector;

    @PostConstruct
    public void init() throws Exception {
        String processOperationDataFilePath = "data/bpm/process-operation.csv";
        String processOperationDataEncoding = "UTF-8";

        ProcessOperationCallback processOperationCallback = new ProcessOperationCallback();
        processOperationCallback.setBpmConfBaseManager(bpmConfBaseManager);
        processOperationCallback.setBpmConfNodeManager(bpmConfNodeManager);
        processOperationCallback
                .setBpmConfOperationManager(bpmConfOperationManager);
        processOperationCallback
                .setTaskDefinitionConnector(taskDefinitionConnector);
        new CsvProcessor().process(processOperationDataFilePath,
                processOperationDataEncoding, processOperationCallback);
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
