package com.mossle.security.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class AbstractDatabaseSourceFetcher {
    private String query;
    private JdbcTemplate jdbcTemplate;

    public Map<String, String> getSource(String type) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);

        Map<String, String> resourceMap = new LinkedHashMap<String, String>();

        for (Map<String, Object> map : list) {
            String name = (String) map.get("acce");
            String role = (String) map.get("perm");

            if (resourceMap.containsKey(name)) {
                String value = resourceMap.get(name);
                resourceMap.put(name, value + "," + role);
            } else {
                resourceMap.put(name, role);
            }
        }

        return resourceMap;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
