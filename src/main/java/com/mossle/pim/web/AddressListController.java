package com.mossle.pim.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class AddressListController {
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("address-list-list")
    public String list(
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        String sql = "select ai.id as id,ai.username as username,ai.display_name as displayName,pi.email as email,pi.cellphone as mobile"
                + " from ACCOUNT_INFO ai left join PERSON_INFO pi on ai.code=pi.code"
                + " where ai.username like ?";
        List list = jdbcTemplate.queryForList(sql, "%" + username + "%");
        model.addAttribute("list", list);

        return "pim/address-list-list";
    }

    // ~ ======================================================================
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
