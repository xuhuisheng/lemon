package com.mossle.report.data;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.report.persistence.domain.ReportDim;
import com.mossle.report.persistence.domain.ReportInfo;
import com.mossle.report.persistence.domain.ReportQuery;
import com.mossle.report.persistence.domain.ReportSubject;
import com.mossle.report.persistence.manager.ReportDimManager;
import com.mossle.report.persistence.manager.ReportInfoManager;
import com.mossle.report.persistence.manager.ReportQueryManager;
import com.mossle.report.persistence.manager.ReportSubjectManager;

public class ReportDeployer {
    private JsonMapper jsonMapper = new JsonMapper();
    private String defaultTenantId = "1";
    private String dataFilePath = "data/report.json";
    private String dataEncoding = "UTF-8";
    private ReportSubjectManager reportSubjectManager;
    private ReportQueryManager reportQueryManager;
    private ReportDimManager reportDimManager;
    private ReportInfoManager reportInfoManager;

    @PostConstruct
    public void process() throws Exception {
        List<Map<String, Object>> list = new JsonParser().parseList(
                dataFilePath, dataEncoding);

        for (Map<String, Object> map : list) {
            this.processQuery(map);
        }
    }

    public void processQuery(Map<String, Object> map) throws Exception {
        String code = (String) map.get("code");
        String name = (String) map.get("name");
        Map<String, Object> chart = (Map<String, Object>) map.get("chart");
        String chartType = (String) chart.get("type");
        String chartConfig = (String) chart.get("config");

        ReportInfo reportInfo = reportInfoManager.findUniqueBy("code", code);

        if (reportInfo != null) {
            return;
        }

        ReportQuery reportQuery = this.createOrGetQuery(map);
        reportInfo = new ReportInfo();
        reportInfo.setCode(code);
        reportInfo.setName(name);
        reportInfo.setType(chartType);
        reportInfo.setContent(chartConfig);
        reportInfo.setReportQuery(reportQuery);
        reportInfoManager.save(reportInfo);
    }

    public ReportQuery createOrGetQuery(Map<String, Object> map) {
        String name = (String) map.get("name");
        String query = (String) map.get("query");
        List<Map<String, Object>> dimensions = (List<Map<String, Object>>) map
                .get("dimensions");

        ReportSubject reportSubject = this.createOrGetSubject(name);
        ReportQuery reportQuery = this.reportQueryManager.findUnique(
                "from ReportQuery where content=? and reportSubject=?", query,
                reportSubject);

        if (reportQuery == null) {
            reportQuery = new ReportQuery();
            reportQuery.setName(name);
            reportQuery.setContent(query);
            reportQuery.setReportSubject(reportSubject);
            reportQueryManager.save(reportQuery);
        }

        for (Map<String, Object> dimension : dimensions) {
            String dimCode = (String) dimension.get("code");
            String dimName = (String) dimension.get("name");
            ReportDim reportDim = this.reportDimManager.findUnique(
                    "from ReportDim where code=? and reportQuery=?", dimCode,
                    reportQuery);

            if (reportDim != null) {
                continue;
            }

            reportDim = new ReportDim();
            reportDim.setCode(dimCode);
            reportDim.setName(dimName);
            reportDim.setReportQuery(reportQuery);
            reportDimManager.save(reportDim);
        }

        return reportQuery;
    }

    public ReportSubject createOrGetSubject(String name) {
        ReportSubject reportSubject = reportSubjectManager.findUniqueBy("name",
                "name");

        if (reportSubject != null) {
            return reportSubject;
        }

        reportSubject = new ReportSubject();
        reportSubject.setName(name);
        reportSubjectManager.save(reportSubject);

        return reportSubject;
    }

    @Resource
    public void setReportSubjectManager(
            ReportSubjectManager reportSubjectManager) {
        this.reportSubjectManager = reportSubjectManager;
    }

    @Resource
    public void setReportQueryManager(ReportQueryManager reportQueryManager) {
        this.reportQueryManager = reportQueryManager;
    }

    @Resource
    public void setReportDimManager(ReportDimManager reportDimManager) {
        this.reportDimManager = reportDimManager;
    }

    @Resource
    public void setReportInfoManager(ReportInfoManager reportInfoManager) {
        this.reportInfoManager = reportInfoManager;
    }
}
