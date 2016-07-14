package com.mossle.auth.support;

import java.util.Map;

import com.mossle.api.tenant.TenantHolder;

import org.springframework.jdbc.core.JdbcTemplate;

public class Exporter {
    private JdbcTemplate jdbcTemplate;
    private TenantHolder tenantHolder;

    public String execute() {
        StringBuilder buff = new StringBuilder();
        buff.append("ref:\n").append(tenantHolder.getTenantDto().getRef())
                .append("\n\n");
        buff.append("code:\n").append(tenantHolder.getTenantCode())
                .append("\n\n");
        buff.append("userRepoRef:\n").append(tenantHolder.getUserRepoRef())
                .append("\n\n");
        buff.append("permType:\n");

        for (Map<String, Object> map : jdbcTemplate.queryForList(
                "select name,type from auth_perm_type where tenant_id=?",
                tenantHolder.getTenantId())) {
            buff.append(map.get("name")).append(",").append(map.get("type"))
                    .append("\n");
        }

        buff.append("\n");
        buff.append("perm:\n");

        for (Map<String, Object> map : jdbcTemplate.queryForList(
                "select ap.code as code,apt.name as type from auth_perm ap,auth_perm_type apt"
                        + " where ap.perm_type_id=apt.id and ap.tenant_id=?",
                tenantHolder.getTenantId())) {
            buff.append(map.get("code")).append(",").append(map.get("type"))
                    .append("\n");
        }

        buff.append("\n");

        buff.append("method:\n");

        for (Map<String, Object> map : jdbcTemplate
                .queryForList(
                        "select aa.value as resc,ap.code as perm from auth_access aa,auth_perm ap "
                                + "where aa.perm_id=ap.id and aa.type='METHOD' and aa.tenant_id=?",
                        tenantHolder.getTenantId())) {
            buff.append(map.get("resc")).append(",").append(map.get("perm"))
                    .append("\n");
        }

        buff.append("\n");
        buff.append("url:\n");

        for (Map<String, Object> map : jdbcTemplate
                .queryForList(
                        "select aa.value as resc,ap.code as perm from auth_access aa,auth_perm ap "
                                + "where aa.perm_id=ap.id and aa.type='URL' and aa.tenant_id=?",
                        tenantHolder.getTenantId())) {
            buff.append(map.get("resc")).append(",").append(map.get("perm"))
                    .append("\n");
        }

        buff.append("\n");
        buff.append("role:\n");

        for (Map<String, Object> map : jdbcTemplate
                .queryForList(
                        "select ar.name as name,ap.code as perm,sc.ref as tenant "
                                + "from auth_role ar,auth_role_def ard,auth_perm_role_def aprd,auth_perm ap,tenant_info sc "
                                + "where ar.role_def_id=ard.id and ard.id=aprd.role_def_id and aprd.perm_id=ap.id "
                                + "and ar.tenant_id=? and sc.id=ard.tenant_id",
                        tenantHolder.getTenantId())) {
            buff.append(map.get("name")).append(",").append(map.get("perm"))
                    .append(",").append(map.get("tenant")).append("\n");
        }

        buff.append("\n");
        buff.append("user:\n");

        for (Map<String, Object> map : jdbcTemplate
                .queryForList(
                        "select aus.username as username,aus.reference as ref,ar.name as role"
                                + " from auth_role ar,auth_user_status aus,auth_user_role aur "
                                + "where ar.id=aur.role_id and aur.user_status_id=aus.id and aus.tenant_id=?",
                        tenantHolder.getTenantId())) {
            buff.append(map.get("username")).append(",").append(map.get("ref"))
                    .append(",").append(map.get("role")).append("\n");
        }

        buff.append("\n");

        return buff.toString();
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
