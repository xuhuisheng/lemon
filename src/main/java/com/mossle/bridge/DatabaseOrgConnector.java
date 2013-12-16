package com.mossle.bridge;

import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.OrgConnector;
import com.mossle.api.OrgDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseOrgConnector implements OrgConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseOrgConnector.class);
    private JdbcTemplate jdbcTemplate;
    private String sql = "select e.id as id,e.name as name,e.reference as reference "
            + " from org_entity e,org_type t "
            + " where e.type_id=t.id and reference=? and t.name=?";

    public OrgDTO findByType(String reference, String type) {
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, reference,
                    type);

            return convertOrgDTO(map);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("org[{},{}] is not exists.", reference, type);

            return null;
        }
    }

    protected OrgDTO convertOrgDTO(Map<String, Object> map) {
        if ((map == null) || map.isEmpty()) {
            logger.info("org[{}] is null.", map);

            return null;
        }

        OrgDTO orgDTO = new OrgDTO();
        orgDTO.setId(map.get("id").toString());
        orgDTO.setName(map.get("name").toString());
        orgDTO.setReference(map.get("reference").toString());

        return orgDTO;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
