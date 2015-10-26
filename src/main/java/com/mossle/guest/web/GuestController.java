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

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.auth.CustomPasswordEncoder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
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
public class GuestController {
    private static Logger logger = LoggerFactory
            .getLogger(GuestController.class);
    private MessageHelper messageHelper;
    private ChangePasswordService changePasswordService;

    @RequestMapping("change-password-input")
    public String changePasswordInput(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute(
                "SPRING_SECURITY_LAST_USERNAME");

        if (username == null) {
            logger.info("username is null");

            return "redirect:/common/login.jsp";
        }

        return "guest/change-password-input";
    }

    @RequestMapping("change-password-save")
    public String changePasswordSave(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpServletRequest request, RedirectAttributes redirectAttributes)
            throws Exception {
        String username = (String) request.getSession().getAttribute(
                "SPRING_SECURITY_LAST_USERNAME");

        if (username == null) {
            logger.info("username is null");

            return "redirect:/common/login.jsp";
        }

        ChangePasswordResult changePasswordResult = changePasswordService
                .changePassword(username, oldPassword, newPassword,
                        confirmPassword);

        if (changePasswordResult.isSuccess()) {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/guest/change-password-success.do";
        } else {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/guest/change-password-input.do";
        }
    }

    @RequestMapping("change-password-success")
    public String changePasswordSuccess() {
        return "guest/change-password-success";
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setChangePasswordService(
            ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }
}
