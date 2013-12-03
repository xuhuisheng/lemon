package com.mossle.report.web.report;

import java.util.List;

import com.mossle.core.struts2.BaseAction;

import org.springframework.jdbc.core.JdbcTemplate;

public class ChartAction extends BaseAction {
    private JdbcTemplate jdbcTemplate;
    private List list;

    public String mostActiveProcess() {
        String sql = "select pd.name_ as name,count(pd.name_) as c"
                + " from act_hi_procinst pi,act_re_procdef pd where pi.proc_def_id_ =pd.id_ group by pd.name_";
        list = jdbcTemplate.queryForList(sql);

        return "mostActiveProcess";
    }

    // ~ ======================================================================
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List getList() {
        return list;
    }
}
