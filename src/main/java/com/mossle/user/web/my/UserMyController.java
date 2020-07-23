package com.mossle.user.web.my;

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
 * 管理个人信息.
 */
@Controller
@RequestMapping("user/my")
public class UserMyController {
    private static Logger logger = LoggerFactory
            .getLogger(UserMyController.class);
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

    public AccountInfo findCurrentAccount() {
        String userId = currentUserHolder.getUserId();
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                userId);

        if (accountInfo == null) {
            throw new IllegalStateException("cannot find account info : "
                    + userId);
        }

        return accountInfo;
    }

    /**
     * 显示个人信息.
     * 
     * @param model
     *            Model
     * @return String
     */
    @RequestMapping("my-info-input")
    public String infoInput(Model model) {
        String userId = currentUserHolder.getUserId();
        AccountInfo accountInfo = this.findCurrentAccount();
        PersonInfo personInfo = personInfoManager.findUniqueBy("code", userId);
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("personInfo", personInfo);

        return "user/my/my-info-input";
    }

    /**
     * 保存个人信息.
     * 
     * @param personInfo
     *            PersonInfo
     * @param redirectAttributes
     *            RedirectAttributes
     * @return String
     * @throws Exception
     *             ex
     */
    @RequestMapping("my-info-save")
    public String infoSave(@ModelAttribute PersonInfo personInfo,
            RedirectAttributes redirectAttributes) throws Exception {
        AccountInfo accountInfo = this.findCurrentAccount();

        PersonInfo dest = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());
        logger.debug("code : {}", accountInfo.getCode());

        if (dest != null) {
            beanMapper.copy(personInfo, dest);
        } else {
            dest = new PersonInfo();
            dest.setCode(accountInfo.getCode());
            beanMapper.copy(personInfo, dest);
        }

        personInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/my/my-info-input.do";
    }

    /**
     * 准备修改密码.
     * 
     * @return String
     */
    @RequestMapping("my-change-password-input")
    public String changepasswordInput() {
        return "user/my/my-change-password-input";
    }

    /**
     * 修改密码.
     * 
     * @param oldPassword
     *            String
     * @param newPassword
     *            String
     * @param confirmPassword
     *            String
     * @param redirectAttributes
     *            RedirectAttributes
     * @return String
     */
    @RequestMapping("my-change-password-save")
    public String changePasswordSave(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        AccountInfo accountInfo = this.findCurrentAccount();
        Long accountId = accountInfo.getId();
        ChangePasswordResult changePasswordResult = changePasswordService
                .changePassword(accountId, oldPassword, newPassword,
                        confirmPassword);

        if (changePasswordResult.isSuccess()) {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/user/my/my-change-password-input.do";
        } else {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/user/my/my-change-password-input.do";
        }
    }

    /**
     * 显示头像.
     * 
     * @param model
     *            model
     * @return String
     */
    @RequestMapping("my-avatar-input")
    public String avatarInput(Model model) {
        String userId = currentUserHolder.getUserId();
        AccountInfo accountInfo = this.findCurrentAccount();

        AvatarDTO avatarDto = internalUserConnector.findAvatar(userId);

        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("avatarDto", avatarDto);

        return "user/my/my-avatar-input";
    }

    /**
     * 上传头像.
     * 
     * @param avatar
     *            MultipartFile
     * @return map
     * @throws Exception
     *             ex
     */
    @RequestMapping("my-avatar-upload")
    @ResponseBody
    public Map<String, Object> avatarUpload(
            @RequestParam("avatar") MultipartFile avatar) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeClient.saveStore("avatar",
                new MultipartFileDataSource(avatar), tenantId);

        String userId = currentUserHolder.getUserId();
        internalUserConnector.saveAvatar(userId, storeDto.getKey());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("id", userId);

        return result;
    }

    /**
     * 显示头像.
     * 
     * @param os
     *            OutputStream
     * @throws Exception
     *             ex
     */
    @RequestMapping("my-avatar-view")
    @ResponseBody
    public void avatarView(OutputStream os) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        AvatarDTO avatarDto = internalUserConnector.findAvatar(userId);

        if (avatarDto == null) {
            return;
        }

        StoreDTO storeDto = storeClient.getStore("avatar", avatarDto.getCode(),
                tenantId);

        IoUtils.copyStream(storeDto.getDataSource().getInputStream(), os);
    }

    /**
     * 剪切头像.
     * 
     * @param model
     *            model
     * @return String
     * @throws Exception
     *             ex
     */
    @RequestMapping("my-avatar-crop")
    public String avatarCrop(Model model) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        AvatarDTO avatarDto = internalUserConnector.findAvatar(userId);

        AccountInfo accountInfo = this.findCurrentAccount();
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("avatarDto", avatarDto);

        if (avatarDto == null) {
            return "user/my/my-avatar-crop";
        }

        StoreDTO storeDto = storeClient.getStore("avatar", avatarDto.getCode(),
                tenantId);
        BufferedImage bufferedImage = ImageIO.read(storeDto.getDataSource()
                .getInputStream());
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        int defaultSize = Math.min(512, Math.min(height, width));

        if (height > width) {
            int h = defaultSize;
            int w = (defaultSize * width) / height;
            int min = w;
            model.addAttribute("h", h);
            model.addAttribute("w", w);
            model.addAttribute("min", min);
        } else {
            int w = defaultSize;
            int h = (defaultSize * height) / width;
            int min = h;
            model.addAttribute("h", h);
            model.addAttribute("w", w);
            model.addAttribute("min", min);
        }

        return "user/my/my-avatar-crop";
    }

    /**
     * 保存头像.
     * 
     * @param x1
     *            int
     * @param x2
     *            int
     * @param y1
     *            int
     * @param y2
     *            int
     * @param w
     *            int
     * @param model
     *            model
     * @return String
     * @throws Exception
     *             ex
     */
    @RequestMapping("my-avatar-save")
    public String avatarSave(@RequestParam("x1") int x1,
            @RequestParam("x2") int x2, @RequestParam("y1") int y1,
            @RequestParam("y2") int y2, @RequestParam("w") int w, Model model)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        AvatarDTO avatarDto = internalUserConnector.findAvatar(userId);

        AccountInfo accountInfo = this.findCurrentAccount();
        String hql = "from AccountAvatar where accountInfo=? and type='default'";
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("avatarDto", avatarDto);

        if (avatarDto != null) {
            StoreDTO storeDto = storeClient.getStore("avatar",
                    avatarDto.getCode(), tenantId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageUtils.zoomImage(storeDto.getDataSource().getInputStream(),
                    baos, x1, y1, x2, y2);

            storeDto = storeClient.saveStore("avatar",
                    new InputStreamDataSource(w + ".png",
                            new ByteArrayInputStream(baos.toByteArray())),
                    tenantId);
            internalUserConnector.saveAvatar(userId, storeDto.getKey());
        }

        return "user/my/my-avatar-save";
    }

    /**
     * 设备列表.
     * 
     * @param page
     *            page
     * @param model
     *            mode
     * @return String
     */
    @RequestMapping("my-device-list")
    public String myDeviceList(@ModelAttribute Page page, Model model) {
        AccountInfo accountInfo = this.findCurrentAccount();
        Long accountId = accountInfo.getId();
        String hql = "from AccountDevice where accountInfo.id=?";
        page = accountDeviceManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), accountId);
        model.addAttribute("page", page);

        return "user/my/my-device-list";
    }

    @RequestMapping("my-device-active")
    public String active(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        AccountDevice accountDevice = accountDeviceManager.get(id);
        accountDevice.setStatus("active");
        accountDeviceManager.save(accountDevice);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.update", "操作成功");

        return "redirect:/user/my/my-device-list.do";
    }

    @RequestMapping("my-device-disable")
    public String disable(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        AccountDevice accountDevice = accountDeviceManager.get(id);
        accountDevice.setStatus("disabled");
        accountDeviceManager.save(accountDevice);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.update", "操作成功");

        return "redirect:/user/my/my-device-list.do";
    }

    @RequestMapping("my-device-remove")
    public String remove(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        accountDeviceManager.removeById(id);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.update", "操作成功");

        return "redirect:/user/my/my-device-list.do";
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
