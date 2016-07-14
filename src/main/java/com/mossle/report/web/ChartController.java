package com.mossle.report.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("report")
public class ChartController {
    private JdbcTemplate jdbcTemplate;
    private TenantHolder tenantHolder;

    @RequestMapping("chart-mostActiveProcess")
    public String mostActiveProcess(Model model) {
        String tenantId = tenantHolder.getTenantId();
        String sql = "SELECT PD.NAME_ AS NAME,COUNT(PD.NAME_) AS C"
                + " FROM ACT_HI_PROCINST PI,ACT_RE_PROCDEF PD"
                + " WHERE PI.PROC_DEF_ID_ =PD.ID_ AND PD.TENANT_ID_=?"
                + " GROUP BY PD.NAME_";
        List list = jdbcTemplate.queryForList(sql, tenantId);
        model.addAttribute("list", list);

        return "report/chart-mostActiveProcess";
    }

    // ~ ======================================================================
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
