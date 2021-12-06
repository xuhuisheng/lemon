package com.mossle.user.web.sys;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;

import com.mossle.user.persistence.manager.AccountInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("user/sys")
public class UserSysController {
    private static Logger logger = LoggerFactory
            .getLogger(UserSysController.class);
    private AccountInfoManager accountInfoManager;
    private OpenClient openClient;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("")
    public String index(Model model) throws Exception {
        logger.debug("index");

        String userId = currentUserHolder.getUserId();
        List<OpenAppDTO> openAppDtos = openClient.findUserApps(userId);

        if (openAppDtos.isEmpty()) {
            return "user/sys/index";
        }

        OpenAppDTO defaultOpenAppDto = openAppDtos.get(0);

        // model.addAttribute("defaultOpenAppDto", defaultOpenAppDto);
        // model.addAttribute("openAppDtos", openAppDtos);
        return "redirect:/user/sys/" + defaultOpenAppDto.getCode()
                + "/index.do";
    }

    @RequestMapping("{sysCode}/index")
    public String sysIndex(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        return "redirect:/user/sys/" + sysCode + "/user.do";
    }

    @RequestMapping("{sysCode}/user")
    public String user(
            Page page,
            @RequestParam(value = "username", required = false) String username,
            @PathVariable("sysCode") String sysCode,
            @RequestParam Map<String, Object> parameterMap, Model model)
            throws Exception {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        // user
        String tenantId = sysCode;

        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        if (username != null) {
            String text = StringUtils.join(username, ',');
            propertyFilters.add(new PropertyFilter("INS_username", text));
        }

        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = accountInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);
        model.addAttribute("now", new Date());

        return "user/sys/user";
    }

    // ~ ======================================================================
    @Resource
    public void setOpenClient(OpenClient openClient) {
        this.openClient = openClient;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
