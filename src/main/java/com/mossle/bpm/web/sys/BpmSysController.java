package com.mossle.bpm.web.sys;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;
import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("bpm/sys")
public class BpmSysController {
    private static Logger logger = LoggerFactory
            .getLogger(BpmSysController.class);
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
            return "bpm/sys/index";
        }

        OpenAppDTO defaultOpenAppDto = openAppDtos.get(0);

        // model.addAttribute("defaultOpenAppDto", defaultOpenAppDto);
        // model.addAttribute("openAppDtos", openAppDtos);
        return "redirect:/bpm/sys/" + defaultOpenAppDto.getCode() + "/index.do";
    }

    @RequestMapping("{sysCode}/index")
    public String sysIndex(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        return "redirect:/bpm/sys/" + sysCode + "/list.do";
    }

    @RequestMapping("{sysCode}/list")
    public String list(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        return "bpm/sys/list";
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
