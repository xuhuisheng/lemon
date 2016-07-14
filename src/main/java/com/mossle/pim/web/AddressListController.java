package com.mossle.pim.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("pim")
public class AddressListController {
    private JdbcTemplate jdbcTemplate;
    private TenantHolder tenantHolder;

    @RequestMapping("address-list-list")
    public String list(
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        String sql = "select ai.id as id,ai.username as username,ai.display_name as displayName,pi.email as email,pi.cellphone as mobile"
                + " from ACCOUNT_INFO ai left join PERSON_INFO pi on ai.code=pi.code"
                + " where ai.tenant_ID=? and ai.username like ?";
        List list = jdbcTemplate.queryForList(sql, tenantId, "%" + username
                + "%");
        model.addAttribute("list", list);

        return "pim/address-list-list";
    }

    // ~ ======================================================================
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
