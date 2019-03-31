package com.mossle.bpm.data;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessListenerCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessListenerCallback.class);
    private BpmConfBaseManager bpmConfBaseManager;
    private BpmConfListenerManager bpmConfListenerManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String processDefinitionKey = list.get(0);
        String activityId = list.get(1);
        String type = list.get(2);
        String value = list.get(3);

        BpmConfNode bpmConfNode = this.findBpmConfNode(processDefinitionKey,
                activityId);

        if (bpmConfNode == null) {
            logger.info("cannot find bpm conf node : {} {}, skip",
                    processDefinitionKey, activityId);

            return;
        }

        Integer typeValue = this.findType(type);

        // 1 enable 2 disable
        int status = 1;

        BpmConfListener bpmConfListener = new BpmConfListener();
        bpmConfListener.setValue(value);
        bpmConfListener.setType(typeValue);
        bpmConfListener.setStatus(status);
        bpmConfListener.setPriority(0);
        bpmConfListener.setBpmConfNode(bpmConfNode);
        bpmConfListenerManager.save(bpmConfListener);
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

    public Integer findType(String code) {
        if ("start".equals(code)) {
            return 0;
        } else if ("end".equals(code)) {
            return 1;
        } else if ("take".equals(code)) {
            return 2;
        } else if ("create".equals(code)) {
            return 3;
        } else if ("assign".equals(code)) {
            return 4;
        } else if ("complete".equals(code)) {
            return 5;
        } else if ("delete".equals(code)) {
            return 6;
        } else if ("approve".equals(code)) {
            return 11;
        } else if ("reject".equals(code)) {
            return 12;
        } else if ("process-draft".equals(code)) {
            return 21;
        } else if ("process-start".equals(code)) {
            return 22;
        } else if ("process-close".equals(code)) {
            return 23;
        } else if ("process-end".equals(code)) {
            return 24;
        }

        return -1;
    }

    // ~
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfListenerManager(
            BpmConfListenerManager bpmConfListenerManager) {
        this.bpmConfListenerManager = bpmConfListenerManager;
    }
}
