package com.mossle.report.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.report.persistence.domain.ReportInfo;
import com.mossle.report.persistence.domain.ReportSubject;
import com.mossle.report.persistence.manager.ReportInfoManager;
import com.mossle.report.persistence.manager.ReportSubjectManager;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("com.mossle.report.web.IndexController")
@RequestMapping("report")
public class IndexController {
    private ReportSubjectManager reportSubjectManager;
    private ReportInfoManager reportInfoManager;
    private JdbcTemplate jdbcTemplate;
    private TenantHolder tenantHolder;
    private JsonMapper jsonMapper = new JsonMapper();

    @RequestMapping("index")
    public String index(Model model) {
        List<Map<String, Object>> list = this.init();
        model.addAttribute("list", list);

        return "report/index";
    }

    @RequestMapping("view")
    public String view(@RequestParam("code") String code, Model model)
            throws Exception {
        List<Map<String, Object>> list = this.init();
        model.addAttribute("list", list);

        ReportInfo reportInfo = reportInfoManager.findUniqueBy("code", code);
        model.addAttribute("reportInfo", reportInfo);

        String content = reportInfo.getContent();
        Map<String, Object> metaData = jsonMapper.fromJson(content, Map.class);
        model.addAttribute("metaData", metaData);

        String tenantId = tenantHolder.getTenantId();
        List<Map<String, Object>> data = jdbcTemplate.queryForList(reportInfo
                .getReportQuery().getContent(), tenantId);
        model.addAttribute("data", data);

        return "report/view";
    }

    public List<Map<String, Object>> init() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        List<ReportSubject> reportSubjects = reportSubjectManager.getAll();

        for (ReportSubject reportSubject : reportSubjects) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("reportSubject", reportSubject);

            String hql = "from ReportInfo where reportQuery.reportSubject=?";
            List<ReportInfo> reportInfos = reportInfoManager.find(hql,
                    reportSubject);
            map.put("reportInfos", reportInfos);
            list.add(map);
        }

        return list;
    }

    // ~ ======================================================================
    @Resource
    public void setReportSubjectManager(
            ReportSubjectManager reportSubjectManager) {
        this.reportSubjectManager = reportSubjectManager;
    }

    @Resource
    public void setReportInfoManager(ReportInfoManager reportInfoManager) {
        this.reportInfoManager = reportInfoManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
