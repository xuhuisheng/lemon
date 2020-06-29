package com.mossle.user.web.user;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.imageio.ImageIO;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.avatar.AvatarDTO;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.IoUtils;

import com.mossle.spi.user.InternalUserConnector;

import com.mossle.user.ImageUtils;
import com.mossle.user.persistence.domain.AccountDevice;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountDeviceManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.service.ChangePasswordService;
import com.mossle.user.support.ChangePasswordResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 用户站.
 */
@Controller
@RequestMapping("user/user")
public class UserUserController {
    private static Logger logger = LoggerFactory
            .getLogger(UserUserController.class);
    private AccountInfoManager accountInfoManager;
    private PersonInfoManager personInfoManager;
    private AccountDeviceManager accountDeviceManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private ChangePasswordService changePasswordService;
    private StoreClient storeClient;
    private TenantHolder tenantHolder;
    private InternalUserConnector internalUserConnector;

    /**
     * 显示个人信息.
     * 
     * @param model
     *            Model
     * @return String
     */
    @RequestMapping("view")
    public String view(@RequestParam("username") String username, Model model) {
        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy(
                "username", username);
        PersonInfo personInfo = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("personInfo", personInfo);

        return "user/user/view";
    }

    // ~ ======================================================================
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    @Resource
    public void setAccountDeviceManager(
            AccountDeviceManager accountDeviceManager) {
        this.accountDeviceManager = accountDeviceManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setChangePasswordService(
            ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setInternalUserConnector(
            InternalUserConnector internalUserConnector) {
        this.internalUserConnector = internalUserConnector;
    }
}
