package com.mossle.cms.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class CatalogExporter {
    public void doExport(File baseDir, JdbcTemplate jdbcTemplate,
            String catalogId) throws Exception {
        File dir = this.exportCatalog(baseDir, jdbcTemplate, catalogId);

        this.exportArticles(dir, jdbcTemplate, catalogId);
    }

    public File exportCatalog(File baseDir, JdbcTemplate jdbcTemplate,
            String catalogId) throws Exception {
        String sql = "select id, code, name from CMS_CATALOG where id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, catalogId);
        String code = (String) map.get("code");
        String name = (String) map.get("name");
        File dir = new File(baseDir, code);
        dir.mkdirs();

        File file = new File(baseDir, code + ".properties");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));
        writer.println("code=" + code);
        writer.println("name=" + name);
        writer.flush();
        writer.close();

        return dir;
    }

    public void exportArticles(File dir, JdbcTemplate jdbcTemplate,
            String catalogId) throws Exception {
        String sql = "select id from CMS_ARTICLE where CATALOG_ID=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,
                catalogId);

        for (Map<String, Object> map : list) {
            String articleId = map.get("id").toString();
            new ArticleExporter().doExport(dir, jdbcTemplate, articleId);
        }
    }
}
