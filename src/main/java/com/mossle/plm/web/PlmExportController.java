package com.mossle.plm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("plm")
public class PlmExportController {
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("export")
    public String export(Model model) {
        StringBuilder buff = new StringBuilder();
        buff.append(this.exportTable("PLM_CATEGORY", "ID", "NAME", "PRIORITY",
                "STATUS", "CREATE_TIME", "USER_ID"));
        buff.append(this.exportTable("PLM_PROJECT", "ID", "CODE", "NAME",
                "LOGO", "SUMMARY", "WIKI_URL", "SOURCE_URL", "URL",
                "LEADER_ID", "PRIORITY", "STATUS", "CREATE_TIME", "USER_ID",
                "CATEGORY_ID"));
        buff.append(this.exportTable("PLM_VERSION", "ID", "NAME", "STATUS",
                "CREATE_TIME", "USER_ID", "PRIORITY", "PROJECT_ID"));
        buff.append(this.exportTable("PLM_CONFIG", "ID", "CODE", "NAME"));
        buff.append(this.exportTable("PLM_STEP", "ID", "CODE", "NAME",
                "PRIORITY", "ACTION", "CONFIG_ID"));
        buff.append(this.exportTable("PLM_SPRINT", "ID", "CODE", "NAME",
                "PRIORITY", "START_TIME", "END_TIME", "STATUS", "CONFIG_ID",
                "PROJECT_ID"));
        buff.append(this.exportTable("PLM_ISSUE", "ID", "TYPE", "NAME",
                "CONTENT", "SEVERITY", "CREATE_TIME", "START_TIME",
                "COMPLETE_TIME", "REPORTER_ID", "ASSIGNEE_ID", "STATUS",
                "STEP", "PROJECT_ID", "SPRINT_ID"));
        buff.append(this.exportTable("PLM_LOG", "ID", "TYPE", "USER_ID",
                "LOG_TIME", "CONTENT", "ISSUE_ID"));

        model.addAttribute("sql", buff.toString());

        return "plm/export";
    }

    String exportTable(String tableName, String... columnNames) {
        StringBuilder selectBuffer = new StringBuilder();
        selectBuffer.append("SELECT ");

        StringBuilder columnBuffer = new StringBuilder();
        int index = 0;

        for (String columnName : columnNames) {
            columnBuffer.append(columnName);

            if (index < (columnNames.length - 1)) {
                columnBuffer.append(",");
            }

            index++;
        }

        selectBuffer.append(columnBuffer).append(" FROM ").append(tableName);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectBuffer
                .toString());
        StringBuilder buff = new StringBuilder();

        for (Map<String, Object> map : list) {
            buff.append("INSERT INTO ").append(tableName).append("(")
                    .append(columnBuffer).append(") VALUES(");

            index = 0;

            for (String columnName : columnNames) {
                String value = (map.get(columnName) == null) ? "NULL"
                        : ("'"
                                + map.get(columnName).toString()
                                        .replace("'", "''") + "'");
                buff.append(value);

                if (index < (columnNames.length - 1)) {
                    buff.append(",");
                }

                index++;
            }

            buff.append(");\n");
        }

        return buff.toString();
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
