package com.mossle.humantask.rule;

import java.util.List;

import com.mossle.api.org.OrgConnector;

import com.mossle.core.spring.ApplicationContextHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 获得部门最接近的对应的岗位的人的信息.
 * 
 */
public class PositionAssigneeRule implements AssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(PositionAssigneeRule.class);
    private JdbcTemplate jdbcTemplate;
    private OrgConnector orgConnector;

    public List<String> process(String value, String initiator) {
        return ApplicationContextHelper.getBean(OrgConnector.class)
                .getPositionUserIds(initiator, value);
    }

    public String process(String initiator) {
        return null;
    }
}
