package com.mossle.cms.export;

import java.io.File;

import org.springframework.jdbc.core.JdbcTemplate;

public class NewsExporter {
    public void doExport(File baseDir, JdbcTemplate jdbcTemplate)
            throws Exception {
        File dir = new File(baseDir, "news");
        dir.mkdirs();
        new SiteExporter().doExport(dir, jdbcTemplate, "default");
    }
}
