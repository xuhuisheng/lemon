package com.mossle.bridge;

import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.GroupProcessor;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DatabaseGroupProcessor implements GroupProcessor {
    private JdbcTemplate jdbcTemplate;

    /**
     * 添加群组.
     * <p>
     * 向party_entity中添加一条记录
     * </p>
     */
    public void insertGroup(String id, String name) {
        String insertPartyEntitySql = "insert into party_entity(type_id,name,reference) values(2,?,?)";
        jdbcTemplate.update(insertPartyEntitySql, name, id);
    }

    /**
     * 修改用户.
     * <p>
     * 只修改party_entity对应的name
     * </p>
     */
    public void updateGroup(String id, String name) {
        String updatePartyEntitySql = "update party_entity set name=? where type_id=2 and reference=?";
        jdbcTemplate.update(updatePartyEntitySql, name, id);
    }

    /**
     * 删除用户.
     * <ul>
     * <li>删除所有包含关系，将对应值为parent_entity_id的party_struct删除</li>
     * <li>删除所有包含关系，将对应值为child_entity_id的party_struct删除</li>
     * <li>删除对应的party_entity</li>
     * </ul>
     */
    public void removeGroup(String id) {
        String selectPartyEntitySql = "select id from party_entity where type_id=2 and reference=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(
                selectPartyEntitySql, id);

        String removePartyStructParentSql = "delete from party_struct where struct_type_id=1 and parent_entity_id=?";
        jdbcTemplate.update(removePartyStructParentSql, map.get("id"));

        String removePartyStructChildSql = "delete from party_struct where struct_type_id=1 and child_entity_id=?";
        jdbcTemplate.update(removePartyStructChildSql, map.get("id"));

        String removePartyEntitySql = "delete from party_entity where id=?";
        jdbcTemplate.update(removePartyEntitySql, map.get("id"));
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
