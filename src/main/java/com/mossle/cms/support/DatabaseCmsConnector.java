package com.mossle.cms.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.api.cms.ArticleDTO;
import com.mossle.api.cms.CatalogDTO;
import com.mossle.api.cms.CmsConnector;
import com.mossle.api.cms.CommentDTO;

import com.mossle.core.page.Page;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

public class DatabaseCmsConnector implements CmsConnector {
    private JdbcTemplate jdbcTemplate;

    public CatalogDTO findCatalogByCode(String code, String tenantId) {
        String sql = "select id,code,name,description from CMS_CATALOG where code=? and tenant_id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, code, tenantId);
        CatalogDTO catalogDto = new CatalogDTO();
        catalogDto.setId(map.get("id").toString());
        catalogDto.setCode((String) map.get("code"));
        catalogDto.setName((String) map.get("name"));
        catalogDto.setDescription((String) map.get("description"));

        return catalogDto;
    }

    public Page findArticles(String code, String tenantId, int pageNo,
            int pageSize) {
        Page page = this.findArticlePageInfo(code, tenantId, pageNo, pageSize);

        int start = (pageNo - 1) * pageSize;

        String selectSql = "select a.id as id,a.title as title,a.summary as summary,a.content as content,"
                + "a.create_time as createTime,a.user_id as userId,a.hit_count as hitCount,"
                + "a.comment_count as commentCount,c.id as catalogId,c.code as catalogCode "
                + "from CMS_ARTICLE a,CMS_CATALOG c "
                + "where a.catalog_id=c.ID and c.code=? and c.tenant_id=? order by a.create_time desc limit "
                + start + "," + pageSize;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql,
                code, tenantId);
        List<ArticleDTO> articleDtos = new ArrayList<ArticleDTO>();

        for (Map<String, Object> map : list) {
            ArticleDTO articleDto = this.convertArticleDto(map);
            articleDtos.add(articleDto);
        }

        page.setResult(articleDtos);

        return page;
    }

    public Page findArticlePageInfo(String code, String tenantId, int pageNo,
            int pageSize) {
        int start = (pageNo - 1) * pageSize;

        String countSql = "select count(*) from CMS_ARTICLE a,CMS_CATALOG c "
                + "where a.catalog_id=c.ID and c.code=? and c.tenant_id=?";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class,
                code, tenantId);

        Page page = new Page(null, totalCount);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return page;
    }

    public ArticleDTO findArticleById(String id) {
        String sql = "select a.id as id,a.title as title,a.summary as summary,a.content as content,"
                + "a.create_time as createTime,a.user_id as userId,a.hit_count as hitCount,"
                + "a.comment_count as commentCount,c.id as catalogId,c.code as catalogCode "
                + "from CMS_ARTICLE a,CMS_CATALOG c "
                + "where a.catalog_id=c.id and a.id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);

        ArticleDTO articleDto = this.convertArticleDto(map);

        return articleDto;
    }

    public ArticleDTO convertArticleDto(Map<String, Object> map) {
        ArticleDTO articleDto = new ArticleDTO();
        articleDto.setId(map.get("id").toString());
        articleDto.setTitle((String) map.get("title"));
        articleDto.setSummary((String) map.get("summary"));
        articleDto.setContent((String) map.get("content"));
        articleDto.setCreateTime((Date) map.get("createTime"));
        articleDto.setUserId((String) map.get("userId"));
        articleDto.setHitCount((Integer) map.get("hitCount"));
        articleDto.setCommentCount((Integer) map.get("commentCount"));
        articleDto.setCatalogId(map.get("catalogId").toString());
        articleDto.setCatalogCode((String) map.get("catalogCode"));

        return articleDto;
    }

    public Page findComments(String id, int pageNo, int pageSize) {
        Page page = this.findCommentPageInfo(id, pageNo, pageSize);

        int start = (pageNo - 1) * pageSize;

        String selectSql = "select c.id as id,c.content as content,"
                + "c.create_time as createTime,c.user_id as userId,"
                + "c.article_id as articleId " + "from CMS_COMMENT c "
                + "where c.article_id=? order by c.create_time desc limit "
                + start + "," + pageSize;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql,
                Long.parseLong(id));
        List<CommentDTO> commentDtos = new ArrayList<CommentDTO>();

        for (Map<String, Object> map : list) {
            CommentDTO commentDto = this.convertCommentDto(map);
            commentDtos.add(commentDto);
        }

        page.setResult(commentDtos);

        return page;
    }

    public Page findCommentPageInfo(String id, int pageNo, int pageSize) {
        int start = (pageNo - 1) * pageSize;

        String countSql = "select count(*) from CMS_COMMENT c "
                + "where c.article_id=?";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class,
                Long.parseLong(id));

        Page page = new Page(null, totalCount);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);

        return page;
    }

    public CommentDTO convertCommentDto(Map<String, Object> map) {
        CommentDTO commentDto = new CommentDTO();
        commentDto.setId(map.get("id").toString());
        commentDto.setContent((String) map.get("content"));
        commentDto.setCreateTime((Date) map.get("createTime"));
        commentDto.setUserId((String) map.get("userId"));
        commentDto.setArticleId(map.get("articleId").toString());

        return commentDto;
    }

    @Transactional
    public void addComment(String articleId, String content, String userId) {
        String sql = "insert into CMS_COMMENT(content,create_time,user_id,article_id) VALUES(?,?,?,?)";
        jdbcTemplate.update(sql, content, new Date(), userId,
                Long.parseLong(articleId));
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
