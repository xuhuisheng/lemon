package com.mossle.bpm.rule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mossle.core.spring.ApplicationContextHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 获得指定用户的上级领导.
 * 
 */
public class SuperiorAssigneeRule extends AbstractAssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(SuperiorAssigneeRule.class);
    private JdbcTemplate jdbcTemplate;

    public List<String> process(String value, String initiator) {
        return Collections.singletonList(this.process(initiator));
    }

    /**
     * 获得员工的直接上级.
     */
    public String process(String initiator) {
        if (jdbcTemplate == null) {
            jdbcTemplate = ApplicationContextHelper.getBean(JdbcTemplate.class);
        }

        String userEntityId = getUserEntityId(initiator);
        String managerEntityId = getManagerEntityIdByUserEntityId(userEntityId);

        if (managerEntityId == null) {
            logger.debug("cannot find directorId for userEntityId : {}",
                    userEntityId);

            return null;
        }

        return getUserId(managerEntityId);
    }
}
