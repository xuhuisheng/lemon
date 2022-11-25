package com.mossle.report.web.sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;
import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.spi.auth.ResourcePublisher;

// import com.mossle.bpm.persistence.domain.AccountInfo;
// import com.mossle.bpm.persistence.manager.AccountInfoManager;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("report/sys")
public class ReportSysController {
    private static Logger logger = LoggerFactory
            .getLogger(ReportSysController.class);
    private OpenClient openClient;
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;
    private TenantHolder tenantHolder;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("")
    public String index(Model model) throws Exception {
        String userId = currentUserHolder.getUserId();
        List<OpenAppDTO> openAppDtos = openClient.findUserApps(userId);

        if (openAppDtos.isEmpty()) {
            return "report/sys/index";
        }

        OpenAppDTO defaultOpenAppDto = openAppDtos.get(0);

        // model.addAttribute("defaultOpenAppDto", defaultOpenAppDto);
        // model.addAttribute("openAppDtos", openAppDtos);
        return "redirect:/report/sys/" + defaultOpenAppDto.getCode()
                + "/index.do";
    }

    @RequestMapping("{sysCode}/index")
    public String sysIndex(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        return "redirect:/report/sys/" + sysCode + "/list.do";
    }

    @RequestMapping("{sysCode}/list")
    public String list(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        return "report/sys/list";
    }

    // ~ ======================================================================
    @Resource
    public void setOpenClient(OpenClient openClient) {
        this.openClient = openClient;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
