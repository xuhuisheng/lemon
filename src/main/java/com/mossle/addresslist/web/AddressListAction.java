package com.mossle.addresslist.web.addresslist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.cmd.ProcessDefinitionDiagramCmd;

import com.mossle.core.struts2.BaseAction;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.ServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.jdbc.core.JdbcTemplate;

public class AddressListAction extends BaseAction {
    private JdbcTemplate jdbcTemplate;
    private List list;
    private String username;

    public String execute() {
        String sql = "select ub.username as username,ub.display_name as displayName,ua1.string_value as email,ua2.string_value as phone"
                + " from user_base ub,user_attr ua1,user_attr ua2"
                + " where ub.username like ? and ub.id=ua1.user_base_id and ua1.user_schema_id=1"
                + " and ub.id=ua2.user_base_id and ua2.user_schema_id=2";
        list = jdbcTemplate.queryForList(sql, "%" + username + "%");

        return SUCCESS;
    }

    // ~ ======================================================================
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List getList() {
        return list;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
