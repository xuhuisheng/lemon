package com.mossle.cms.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class ArticleExporter {
    public void doExport(File baseDir, JdbcTemplate jdbcTemplate,
            String articleId) throws Exception {
        try {
            String sql = "select id,code,title,content from CMS_ARTICLE where id=?";
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, articleId);

            this.exportProperties(baseDir, map);
            this.exportContent(baseDir, map);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot find article : " + articleId);
        }
    }

    public void exportProperties(File baseDir, Map<String, Object> map)
            throws Exception {
        String code = (String) map.get("code");
        String title = (String) map.get("title");
        File file = new File(baseDir, code + ".properties");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));
        writer.println("code=" + code);
        writer.println("title=" + title);
        writer.flush();
        writer.close();
    }

    public void exportContent(File baseDir, Map<String, Object> map)
            throws Exception {
        String code = (String) map.get("code");
        String content = (String) map.get("content");
        File file = new File(baseDir, code + ".txt");

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));
        writer.println(content);
        writer.flush();
        writer.close();
    }
}
