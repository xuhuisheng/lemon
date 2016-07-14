package com.mossle.cms.export;

import java.io.File;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class SiteExporter {
    public void doExport(File baseDir, JdbcTemplate jdbcTemplate,
            String siteCode) throws Exception {
        File dir = new File(baseDir, siteCode);
        dir.mkdirs();

        List<Map<String, Object>> list = jdbcTemplate
                .queryForList("select id from CMS_CATALOG");

        for (Map<String, Object> map : list) {
            String catalogId = map.get("id").toString();
            new CatalogExporter().doExport(dir, jdbcTemplate, catalogId);
        }
    }
}
