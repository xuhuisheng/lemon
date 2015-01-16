package com.mossle.bridge.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseFormConnector implements FormConnector {
    private JdbcTemplate jdbcTemplate;

    public List<FormDTO> getAll(String scopeId) {
        String sql = "select id,code,name,content from FORM_TEMPLATE";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<FormDTO> formDtos = new ArrayList<FormDTO>();

        for (Map<String, Object> map : list) {
            FormDTO formDto = new FormDTO();
            formDtos.add(formDto);
            formDto.setId(map.get("id").toString());
            formDto.setCode(map.get("code").toString());
            formDto.setName(map.get("name").toString());
        }

        return formDtos;
    }

    public FormDTO findForm(String code) {
        String sql = "select id,code,name,content from FORM_TEMPLATE where code=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, code);

        FormDTO formDto = new FormDTO();
        formDto.setId(map.get("id").toString());
        formDto.setCode(map.get("code").toString());
        formDto.setName(map.get("name").toString());
        formDto.setContent(map.get("content").toString());

        return formDto;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
