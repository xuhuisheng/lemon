package com.mossle.cms.web.sys;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cms/sys")
public class CmsSysController {
    private static Logger logger = LoggerFactory
            .getLogger(CmsSysController.class);
    private OpenClient openClient;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("")
    public String index(Model model) throws Exception {
        logger.debug("index");

        String userId = currentUserHolder.getUserId();
        List<OpenAppDTO> openAppDtos = openClient.findUserApps(userId);

        if (openAppDtos.isEmpty()) {
            return "cms/sys/index";
        }

        OpenAppDTO defaultOpenAppDto = openAppDtos.get(0);

        // model.addAttribute("defaultOpenAppDto", defaultOpenAppDto);
        // model.addAttribute("openAppDtos", openAppDtos);
        return "redirect:/cms/sys/" + defaultOpenAppDto.getCode() + "/index.do";
    }

    @RequestMapping("{sysCode}/index")
    public String sysIndex(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        return "redirect:/cms/sys/" + sysCode + "/list.do";
    }

    @RequestMapping("{sysCode}/list")
    public String list(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        return "cms/sys/list";
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
}
