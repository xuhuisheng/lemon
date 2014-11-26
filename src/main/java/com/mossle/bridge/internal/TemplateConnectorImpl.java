package com.mossle.bridge.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.api.internal.TemplateConnector;
import com.mossle.api.internal.TemplateDTO;

import org.springframework.jdbc.core.JdbcTemplate;

public class TemplateConnectorImpl implements TemplateConnector {
    private JdbcTemplate jdbcTemplate;

    public TemplateDTO findByCode(String code) {
        String templateInfoSql = "select id,name,code from TEMPLATE_INFO where code=?";
        Map<String, Object> templateInfoMap = jdbcTemplate.queryForMap(
                templateInfoSql, code);

        return this.processTemplateInfo(templateInfoMap);
    }

    public List<TemplateDTO> findAll() {
        String templateInfoSql = "select id,name,code from TEMPLATE_INFO";
        List<TemplateDTO> list = new ArrayList<TemplateDTO>();

        for (Map<String, Object> templateInfoMap : jdbcTemplate
                .queryForList(templateInfoSql)) {
            list.add(this.processTemplateInfo(templateInfoMap));
        }

        return list;
    }

    public TemplateDTO processTemplateInfo(Map<String, Object> templateInfoMap) {
        TemplateDTO templateDto = new TemplateDTO();
        templateDto.setName((String) templateInfoMap.get("name"));
        templateDto.setCode((String) templateInfoMap.get("code"));

        String templateFieldSql = "select name,content from TEMPLATE_FIELD where INFO_ID=?";
        List<Map<String, Object>> templateFieldList = jdbcTemplate
                .queryForList(templateFieldSql, templateInfoMap.get("ID"));

        for (Map<String, Object> templateFieldMap : templateFieldList) {
            templateDto.getFields().put((String) templateFieldMap.get("name"),
                    (String) templateFieldMap.get("content"));
        }

        return templateDto;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
