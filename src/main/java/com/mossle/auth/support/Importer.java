package com.mossle.auth.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class Importer {
    private static Logger logger = LoggerFactory.getLogger(Importer.class);
    private JdbcTemplate jdbcTemplate;
    private Map<String, Object> tenantMap = new HashMap<String, Object>();
    private List<Map<String, Object>> permTypeList = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> permList = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> methodList = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> urlList = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

    public void execute(String text) {
        parse(text);
        executeSql();
    }

    public void parse(String text) {
        String step = "start";

        String[] array = text.split("\n");

        for (int i = 0; i < array.length; i++) {
            String line = array[i].trim();

            if ("".equals(line)) {
                step = "start";

                continue;
            }

            if ("start".equals(step)) {
                if ("ref:".equals(line)) {
                    step = "ref";
                }

                if ("code:".equals(line)) {
                    step = "code";
                }

                if ("userRepoRef:".equals(line)) {
                    step = "userRepoRef";
                }

                if ("permType:".equals(line)) {
                    step = "permType";
                }

                if ("perm:".equals(line)) {
                    step = "perm";
                }

                if ("method:".equals(line)) {
                    step = "method";
                }

                if ("url:".equals(line)) {
                    step = "url";
                }

                if ("role:".equals(line)) {
                    step = "role";
                }

                if ("user:".equals(line)) {
                    step = "user";
                }

                continue;
            }

            if ("ref".equals(step)) {
                tenantMap.put("ref", line);
            }

            if ("code".equals(step)) {
                tenantMap.put("code", line);
            }

            if ("userRepoRef".equals(step)) {
                tenantMap.put("userRepoRef", line);
            }

            if ("permType".equals(step)) {
                String[] tmp = line.split(",");
                Map<String, Object> permType = new HashMap<String, Object>();
                permType.put("name", tmp[0]);
                permType.put("type", tmp[1]);
                permTypeList.add(permType);
            }

            if ("perm".equals(step)) {
                String[] tmp = line.split(",");
                Map<String, Object> perm = new HashMap<String, Object>();
                perm.put("code", tmp[0]);
                perm.put("type", tmp[1]);
                permList.add(perm);
            }

            if ("method".equals(step)) {
                String[] tmp = line.split(",");
                Map<String, Object> method = new HashMap<String, Object>();
                method.put("resc", tmp[0]);
                method.put("perm", tmp[1]);
                methodList.add(method);
            }

            if ("url".equals(step)) {
                String[] tmp = line.split(",");
                Map<String, Object> url = new HashMap<String, Object>();
                url.put("resc", tmp[0]);
                url.put("perm", tmp[1]);
                urlList.add(url);
            }

            if ("role".equals(step)) {
                String[] tmp = line.split(",");
                Map<String, Object> role = new HashMap<String, Object>();
                role.put("name", tmp[0]);
                role.put("perm", tmp[1]);
                role.put("tenant", tmp[2]);
                roleList.add(role);
            }

            if ("user".equals(step)) {
                String[] tmp = line.split(",");
                Map<String, Object> user = new HashMap<String, Object>();
                user.put("username", tmp[0]);
                user.put("ref", tmp[1]);
                user.put("role", tmp[2]);
                userList.add(user);
            }
        }
    }

    public void executeSql() {
        String tenantId = createOrGetTenantId();

        for (Map<String, Object> permType : permTypeList) {
            if (jdbcTemplate
                    .queryForObject(
                            "select count(*) from auth_perm_type where name=? and tenant_id=?",
                            Integer.class, permType.get("name"), tenantId) == 0) {
                jdbcTemplate
                        .update("insert into auth_perm_type(name,type,tenant_id) values(?,?,?)",
                                permType.get("name"), permType.get("type"),
                                tenantId);
            }
        }

        for (Map<String, Object> perm : permList) {
            Long permTypeId = jdbcTemplate
                    .queryForObject(
                            "select id from auth_perm_type where name=? and tenant_id=?",
                            Long.class, perm.get("type"), tenantId);
            jdbcTemplate
                    .update("insert into auth_perm(code,name,perm_type_id,tenant_id) values(?,?,?,?)",
                            perm.get("code"), perm.get("code"), permTypeId,
                            tenantId);
        }

        Map<String, Long> permCache = buildPermCache(tenantId);

        for (Map<String, Object> method : methodList) {
            jdbcTemplate
                    .update("insert into auth_access(value,perm_id,type,tenant_id) values(?,?,'METHOD',?)",
                            method.get("resc"),
                            permCache.get(method.get("perm")), tenantId);
        }

        for (Map<String, Object> url : urlList) {
            jdbcTemplate
                    .update("insert into auth_access(value,perm_id,type,tenant_id) values(?,?,'URL',?)",
                            url.get("resc"), permCache.get(url.get("perm")),
                            tenantId);
        }

        for (Map<String, Object> role : roleList) {
            Long roleDefId = createOrGetRoleDefId((String) role.get("name"),
                    (String) role.get("tenant"), tenantId);

            if (tenantMap.get("ref").equals(role.get("tenant"))) {
                Long permId = permCache.get(role.get("perm"));

                if (permId == null) {
                    logger.info("permId is null - {}", role);
                } else {
                    jdbcTemplate
                            .update("insert into auth_perm_role_def(perm_id,role_def_id) values(?,?)",
                                    permId, roleDefId);
                }
            }
        }

        Map<String, Long> roleCache = buildRoleCache(tenantId);

        for (Map<String, Object> user : userList) {
            Long userId = createOrGetUserId((String) user.get("username"),
                    (String) user.get("ref"), tenantId);

            try {
                jdbcTemplate
                        .update("insert into auth_user_role(user_status_id,role_id) values(?,?)",
                                userId, roleCache.get(user.get("role")));
            } catch (Exception ex) {
                logger.warn("{} - {}", user, ex.getMessage(), ex);
            }
        }
    }

    public Map<String, Long> buildPermCache(String tenantId) {
        Map<String, Long> permCache = new HashMap<String, Long>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,code from auth_perm where tenant_id=?", tenantId);

        for (Map<String, Object> map : list) {
            permCache.put((String) map.get("code"), (Long) map.get("id"));
        }

        return permCache;
    }

    public Map<String, Long> buildRoleCache(String tenantId) {
        Map<String, Long> permCache = new HashMap<String, Long>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,name from auth_role where tenant_id=?", tenantId);

        for (Map<String, Object> map : list) {
            permCache.put((String) map.get("name"), (Long) map.get("id"));
        }

        return permCache;
    }

    public String createOrGetTenantId() {
        if (this.notExists("select count(*) from tenant_info where ref=?",
                tenantMap.get("ref"))) {
            jdbcTemplate
                    .update("insert into tenant_info(NAME,CODE,REF,SHARED,USER_REPO_REF) values(?,?,?,0,?)",
                            tenantMap.get("code"), tenantMap.get("code"),
                            tenantMap.get("ref"), tenantMap.get("userRepoRef"));
        }

        return jdbcTemplate.queryForObject(
                "select id from tenant_info where ref=?", String.class,
                tenantMap.get("ref"));
    }

    public Long createOrGetRoleDefId(String name, String tenantRef,
            String tenantId) {
        String roleDefTenantId = jdbcTemplate.queryForObject(
                "select id from tenant_info where ref=?", String.class,
                tenantRef);
        Long roleDefId = null;

        if (this.notExists(
                "select count(*) from auth_role_def where name=? and tenant_id=?",
                name, roleDefTenantId)) {
            jdbcTemplate.update(
                    "insert into auth_role_def(name,tenant_id) values(?,?)",
                    name, roleDefTenantId);
        }

        roleDefId = jdbcTemplate.queryForObject(
                "select id from auth_role_def where name=? and tenant_id=?",
                Long.class, name, roleDefTenantId);

        if (this.notExists(
                "select count(*) from auth_role where role_def_id=? and tenant_id=?",
                roleDefId, tenantId)) {
            jdbcTemplate
                    .update("insert into auth_role(name,role_def_id,tenant_id) values(?,?,?)",
                            name, roleDefId, tenantId);
        }

        return roleDefId;
    }

    public Long createOrGetUserId(String username, String reference,
            String tenantId) {
        if (this.notExists(
                "select count(*) form auth_user_status where reference=? and tenant_id=?",
                reference, tenantId)) {
            jdbcTemplate
                    .update("insert into auth_user_status(username,reference,status,tenant_id) values(?,?,1,?)",
                            username, reference, tenantId);
        }

        return jdbcTemplate
                .queryForObject(
                        "select id from auth_user_status where reference=? and tenant_id=?",
                        Long.class, reference, tenantId);
    }

    public boolean notExists(String sql, Object... values) {
        return jdbcTemplate.queryForObject(sql, Integer.class, values) == 0;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
