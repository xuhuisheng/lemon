package com.mossle.addresslist.web.addresslist;

import java.util.List;

import com.mossle.core.struts2.BaseAction;

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
