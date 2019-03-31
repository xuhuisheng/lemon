package com.mossle.user.web.api;

import java.io.InputStream;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.service.UserAvatarService;
import com.mossle.user.service.UserService;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("avatar/api")
public class AvatarApiController {
    private static Logger logger = LoggerFactory
            .getLogger(AvatarApiController.class);
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private UserAvatarService userAvatarService;

    @RequestMapping(value = "{username}", produces = MediaType.IMAGE_PNG_VALUE)
    public void avatar(
            @PathVariable("username") String username,
            @RequestParam(value = "width", required = false, defaultValue = "16") int width,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        InputStream is = userAvatarService.viewAvatarByUsername(username,
                width, tenantId).getInputStream();

        response.setContentType("image/png");
        IOUtils.copy(is, response.getOutputStream());
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserAvatarService(UserAvatarService userAvatarService) {
        this.userAvatarService = userAvatarService;
    }
}
