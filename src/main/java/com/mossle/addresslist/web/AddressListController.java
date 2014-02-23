package com.mossle.addresslist.web;

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
@RequestMapping("addresslist")
public class AddressListController {
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("address-list-list")
    public String list(
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        String sql = "select ub.username as username,ub.display_name as displayName,ub.email as email,ub.mobile as mobile"
                + " from user_base ub" + " where ub.username like ?";
        List list = jdbcTemplate.queryForList(sql, "%" + username + "%");
        model.addAttribute("list", list);

        return "addresslist/address-list-list";
    }

    // ~ ======================================================================
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
