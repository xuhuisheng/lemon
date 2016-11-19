package com.mossle.humantask.rule;

import java.util.Collections;
import java.util.List;

import com.mossle.core.spring.ApplicationContextHelper;

import com.mossle.spi.process.InternalProcessConnector;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对应环节的负责人.
 * 
 */
public class ActivityAssigneeRule implements AssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(ActivityAssigneeRule.class);

    // TODO: initiator这个在这里当做processInstanceId使用的。以后改。
    public List<String> process(String value, String initiator) {
        if (StringUtils.isBlank(value)) {
            logger.info("value is blank");

            return Collections.emptyList();
        }

        String[] array = value.split(":");

        if (array.length < 2) {
            logger.info("value is invalid : {}", value);

            return Collections.emptyList();
        }

        String processInstanceId = initiator;
        String activityId = array[1];

        String userId = ApplicationContextHelper.getBean(
                InternalProcessConnector.class).findAssigneeByActivityId(
                processInstanceId, activityId);

        return Collections.singletonList(userId);
    }

    public String process(String initiator) {
        return null;
    }
}
