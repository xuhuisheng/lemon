package com.mossle.bpm.data;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;

import com.mossle.core.csv.CsvProcessor;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessListenerDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessListenerDeployer.class);
    private BpmConfBaseManager bpmConfBaseManager;
    private BpmConfListenerManager bpmConfListenerManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private String defaultTenantId = "1";

    @PostConstruct
    public void init() throws Exception {
        String processListenerDataFilePath = "data/process-listener.csv";
        String processListenerDataEncoding = "UTF-8";

        ProcessListenerCallback processListenerCallback = new ProcessListenerCallback();
        processListenerCallback.setBpmConfBaseManager(bpmConfBaseManager);
        processListenerCallback.setBpmConfNodeManager(bpmConfNodeManager);
        processListenerCallback
                .setBpmConfListenerManager(bpmConfListenerManager);
        new CsvProcessor().process(processListenerDataFilePath,
                processListenerDataEncoding, processListenerCallback);
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
    public void setBpmConfListenerManager(
            BpmConfListenerManager bpmConfListenerManager) {
        this.bpmConfListenerManager = bpmConfListenerManager;
    }
}
