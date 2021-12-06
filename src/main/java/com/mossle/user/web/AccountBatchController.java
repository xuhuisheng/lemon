package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("user")
public class AccountBatchController {
    private AccountInfoManager accountInfoManager;
    private TenantHolder tenantHolder;

    @RequestMapping("account-batch-input")
    public String input(@RequestParam("selectedItem") List<Long> selectedItem,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<AccountInfo> accountInfos = accountInfoManager
                .findByIds(selectedItem);

        model.addAttribute("accountInfos", accountInfos);

        return "user/account-batch-input";
    }

    @RequestMapping("account-batch-save")
    public String save(@RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("type") List<String> types,
            @RequestParam("value") List<String> values) {
        List<AccountInfo> accountInfos = accountInfoManager
                .findByIds(selectedItem);
        List<String> usernames = new ArrayList<String>();

        for (AccountInfo accountInfo : accountInfos) {
            usernames.add(accountInfo.getUsername());
        }

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            String value = values.get(i);

            if ("status".equals(type)) {
                for (AccountInfo accountInfo : accountInfos) {
                    accountInfo.setStatus(value);
                    accountInfoManager.save(accountInfo);
                }
            } else if ("type".equals(type)) {
                for (AccountInfo accountInfo : accountInfos) {
                    accountInfo.setType(value);
                    accountInfoManager.save(accountInfo);
                }
            }
        }

        return "redirect:/user/account-info-list.do?filter_INS_username="
                + StringUtils.join(usernames, ",");
    }

    // ~ ======================================================================
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
