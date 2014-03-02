package com.mossle.report.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("report")
public class ChartController {
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("chart-mostActiveProcess")
    public String mostActiveProcess(Model model) {
        String sql = "select pd.name_ as name,count(pd.name_) as c"
                + " from act_hi_procinst pi,act_re_procdef pd where pi.proc_def_id_ =pd.id_ group by pd.name_";
        List list = jdbcTemplate.queryForList(sql);
        model.addAttribute("list", list);

        return "report/chart-mostActiveProcess";
    }

    // ~ ======================================================================
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
