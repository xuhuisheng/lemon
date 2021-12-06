package com.mossle.pim.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("pim")
public class AddressListController {
    private TenantHolder tenantHolder;
    private UserClient userClient;

    @RequestMapping("address-list-list")
    public String list(
            @RequestParam(value = "username", required = false) String query,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<UserDTO> userDtos = this.userClient.search(query);
        model.addAttribute("list", userDtos);

        return "pim/address-list-list";
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
