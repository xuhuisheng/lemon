package com.mossle.guest.web;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSession;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.auth.CustomPasswordEncoder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.id.IdGenerator;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.user.ImageUtils;
import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.service.ChangePasswordService;
import com.mossle.user.support.ChangePasswordResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.InputStreamResource;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("guest")
public class RegisterController {
    private static Logger logger = LoggerFactory
            .getLogger(RegisterController.class);
    private MessageHelper messageHelper;
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private CustomPasswordEncoder customPasswordEncoder;
    private IdGenerator idGenerator;

    @RequestMapping("register-view")
    public String registerView() {
        return "guest/register-view";
    }

    @RequestMapping("register-save")
    public String registerSave(
            @ModelAttribute AccountInfo accountInfo,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) throws Exception {
        // 先进行校验
        if (password != null) {
            if (!password.equals(confirmPassword)) {
                messageHelper.addFlashMessage(redirectAttributes,
                        "user.user.input.passwordnotequals", "两次输入密码不符");

                // TODO: 还要填充schema
                return "guest/register-view";
            }
        }

        // 再进行数据复制
        AccountInfo dest = accountInfo;
        dest.setId(idGenerator.generateId());
        dest.setCreateTime(new Date());

        accountInfoManager.insert(dest);

        if (dest.getCode() == null) {
            dest.setCode(Long.toString(dest.getId()));
            accountInfoManager.save(dest);
        }

        if (password != null) {
            String hql = "from AccountCredential where accountInfo=? and catalog='default'";
            AccountCredential accountCredential = accountCredentialManager
                    .findUnique(hql, accountInfo);

            if (accountCredential == null) {
                accountCredential = new AccountCredential();
                accountCredential.setAccountInfo(accountInfo);
                accountCredential.setType("normal");
                accountCredential.setCatalog("default");
            }

            if (customPasswordEncoder != null) {
                accountCredential.setPassword(customPasswordEncoder
                        .encode(password));
            } else {
                accountCredential.setPassword(password);
            }

            accountCredentialManager.save(accountCredential);
        }

        accountInfo.setStatus("disabled");
        accountInfo.setType("register");
        accountInfoManager.save(accountInfo);

        return "redirect:/guest/register-success.do";
    }

    @RequestMapping("register-success")
    public String registerSuccess() {
        return "guest/register-success";
    }

    @RequestMapping("register-checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from AccountInfo where username=?";
        Object[] params = { username };

        if (id != null) {
            hql = "from AccountInfo where username=? and id<>?";
            params = new Object[] { username, id };
        }

        boolean result = accountInfoManager.findUnique(hql, params) == null;

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }

    @Resource
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }

    @Resource
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
