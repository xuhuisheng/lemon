package com.mossle.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class CustomGroupEntityManager extends GroupEntityManager {
    private static Logger logger = LoggerFactory
            .getLogger(CustomGroupEntityManager.class);
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Group> findGroupsByUser(String userId) {
        logger.debug("findGroupsByUser : {}", userId);

        String sql = "select parent.name as name from party_entity parent, party_struct ps, party_entity child"
                + " where parent.id=ps.parent_entity_id and child.id=ps.child_entity_id and child.name=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        List<Group> groups = new ArrayList<Group>();

        for (Map<String, Object> map : list) {
            String name = (String) map.get("name");
            GroupEntity groupEntity = new GroupEntity(name);
            groups.add(groupEntity);
        }

        return groups;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
