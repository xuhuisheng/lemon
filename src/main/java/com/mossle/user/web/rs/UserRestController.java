package com.mossle.user.web.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;
import com.mossle.core.util.Select2Info;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/rs")
public class UserRestController {
    private static Logger logger = LoggerFactory
            .getLogger(UserRestController.class);
    private AccountInfoManager accountInfoManager;
    private TenantHolder tenantHolder;

    @RequestMapping(value = "checkUsername", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkUsername(@RequestParam("username") String username,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        logger.debug("check username {} {}", username, id);

        String tenantId = tenantHolder.getTenantId();
        String hql = "from AccountInfo where username=? and tenantId=?";
        Object[] params = { username, tenantId };

        if (id != null) {
            hql = "from AccountInfo where username=? and tenantId=? and id<>?";
            params = new Object[] { username, tenantId, id };
        }

        boolean result = accountInfoManager.findUnique(hql, params) == null;

        return result;
    }

    @RequestMapping(value = "search/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> search(
            @PathVariable("username") String username) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        Page page = accountInfoManager.pagedQuery(
                "from AccountInfo where username like ?", 1, 5, "%" + username
                        + "%");
        List<AccountInfo> accountInfos = (List<AccountInfo>) page.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (AccountInfo accountInfo : accountInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", accountInfo.getCode());
            map.put("username", accountInfo.getUsername());
            map.put("displayName", accountInfo.getDisplayName());
            list.add(map);
        }

        return list;
    }

    @RequestMapping(value = "search/select2", produces = MediaType.APPLICATION_JSON_VALUE)
    public Select2Info searchSelect2(@RequestParam("q") String username)
            throws Exception {
        if (StringUtils.isBlank(username)) {
            return new Select2Info();
        }

        String tenantId = tenantHolder.getTenantId();
        Page page = accountInfoManager.pagedQuery(
                "from AccountInfo where username like ?", 1, 5, "%" + username
                        + "%");
        List<AccountInfo> accountInfos = (List<AccountInfo>) page.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Select2Info select2Info = new Select2Info();

        for (AccountInfo accountInfo : accountInfos) {
            select2Info.addItem(
                    accountInfo.getUsername(),
                    accountInfo.getDisplayName() + "("
                            + accountInfo.getUsername() + ")");
        }

        return select2Info;
    }

    @RequestMapping(value = "s", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> searchUserPicker(
            @RequestParam("username") String username) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        Page page = accountInfoManager.pagedQuery(
                "from AccountInfo where username like ?", 1, 5, "%" + username
                        + "%");
        List<AccountInfo> accountInfos = (List<AccountInfo>) page.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (AccountInfo accountInfo : accountInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", accountInfo.getCode());
            map.put("username", accountInfo.getUsername());
            map.put("displayName", accountInfo.getDisplayName());
            list.add(map);
        }

        return list;
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
