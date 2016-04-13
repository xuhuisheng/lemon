package com.mossle.humantask.rule;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获得流程发起人.
 * 
 */
public class InitiatorAssigneeRule implements AssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(InitiatorAssigneeRule.class);

    public String process(String initiator) {
        return initiator;
    }

    public List<String> process(String value, String initiator) {
        return Collections.singletonList(initiator);
    }
}
