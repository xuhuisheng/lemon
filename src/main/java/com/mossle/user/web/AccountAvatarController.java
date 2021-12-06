package com.mossle.user.web;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import javax.imageio.ImageIO;

import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.store.MultipartFileDataSource;

import com.mossle.user.ImageUtils;
import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("user")
public class AccountAvatarController {
    private static Logger logger = LoggerFactory
            .getLogger(AccountAvatarController.class);
    private AccountInfoManager accountInfoManager;
    private AccountAvatarManager accountAvatarManager;
    private StoreClient storeClient;
    private TenantHolder tenantHolder;

    @RequestMapping("account-avatar-input")
    public String input(@RequestParam("id") Long id, Model model) {
        AccountInfo accountInfo = accountInfoManager.get(id);
        String hql = "from AccountAvatar where accountInfo=? and type='default'";
        AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                accountInfo);
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("accountAvatar", accountAvatar);

        return "user/account-avatar-input";
    }

    /**
     * 上传.
     * 
     * @param id
     *            Long
     * @param avatar
     *            MultipartFile
     * @return String
     * @throws Exception
     *             ex
     */
    @RequestMapping("account-avatar-upload")
    @ResponseBody
    public String upload(@RequestParam("id") Long id,
            @RequestParam("avatar") MultipartFile avatar) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        StoreDTO storeDto = storeClient.saveStore("avatar",
                new MultipartFileDataSource(avatar), tenantId);

        AccountInfo accountInfo = accountInfoManager.get(id);
        String hql = "from AccountAvatar where accountInfo=? and type='default'";
        AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                accountInfo);

        if (accountAvatar == null) {
            accountAvatar = new AccountAvatar();
            accountAvatar.setAccountInfo(accountInfo);
            accountAvatar.setType("default");
        }

        accountAvatar.setCode(storeDto.getKey());
        accountAvatarManager.save(accountAvatar);

        return "{\"success\":true,\"id\":\"" + id + "\"}";
    }

    /**
     * 显示.
     * 
     * @param id
     *            Long
     * @param os
     *            OutputStream
     * @throws Exception
     *             ex
     */
    @RequestMapping("account-avatar-view")
    @ResponseBody
    public void avatar(@RequestParam("id") Long id, OutputStream os)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        AccountInfo accountInfo = accountInfoManager.get(id);
        String hql = "from AccountAvatar where accountInfo=? and type='default'";
        AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                accountInfo);

        if (accountAvatar == null) {
            return;
        }

        StoreDTO storeDto = storeClient.getStore("avatar",
                accountAvatar.getCode(), tenantId);

        IOUtils.copy(storeDto.getDataSource().getInputStream(), os);
    }

    @RequestMapping("account-avatar-crop")
    public String crop(@RequestParam("id") Long id, Model model)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        AccountInfo accountInfo = accountInfoManager.get(id);
        String hql = "from AccountAvatar where accountInfo=? and type='default'";
        AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                accountInfo);
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("accountAvatar", accountAvatar);

        if (accountAvatar == null) {
            return "user/account-avatar-crop";
        }

        StoreDTO storeDto = storeClient.getStore("avatar",
                accountAvatar.getCode(), tenantId);
        BufferedImage bufferedImage = ImageIO.read(storeDto.getDataSource()
                .getInputStream());
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        if (height > width) {
            int h = 512;
            int w = (512 * width) / height;
            int min = w;
            model.addAttribute("h", h);
            model.addAttribute("w", w);
            model.addAttribute("min", min);
        } else {
            int w = 512;
            int h = (512 * height) / width;
            int min = h;
            model.addAttribute("h", h);
            model.addAttribute("w", w);
            model.addAttribute("min", min);
        }

        return "user/account-avatar-crop";
    }

    @RequestMapping("account-avatar-save")
    public String save(@RequestParam("id") Long id, @RequestParam("x1") int x1,
            @RequestParam("x2") int x2, @RequestParam("y1") int y1,
            @RequestParam("y2") int y2, @RequestParam("w") int w, Model model)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        AccountInfo accountInfo = accountInfoManager.get(id);
        String hql = "from AccountAvatar where accountInfo=? and type='default'";
        AccountAvatar accountAvatar = accountAvatarManager.findUnique(hql,
                accountInfo);
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("accountAvatar", accountAvatar);

        if (accountAvatar != null) {
            StoreDTO storeDto = storeClient.getStore("avatar",
                    accountAvatar.getCode(), tenantId);
            InputStream is = this.cropImage(storeDto.getDataSource()
                    .getInputStream(), x1, y1, x2, y2);

            storeDto = storeClient.saveStore("avatar",
                    new InputStreamDataSource(w + ".png", is), tenantId);
            accountAvatar.setCode(storeDto.getKey());
            accountAvatarManager.save(accountAvatar);
        }

        return "redirect:/user/account-avatar-preview.do";
    }

    @RequestMapping("account-avatar-preview")
    public String accountAvatarPreview() {
        return "user/account-avatar-preview";
    }

    // 先把图片变成，上下左右居中，最大长宽350的图片，背景白色
    // 然后再截取x1, y1, x2, y2
    public InputStream cropImage(InputStream inputStream, int x1, int y1,
            int x2, int y2) throws Exception {
        logger.info("crop image {} {} {} {}", x1, y1, x2, y2);

        int theWidth = 350;
        int theHeight = 350;
        BufferedImage src = ImageIO.read(inputStream);
        BufferedImage bufferedImage = new BufferedImage(theWidth, theHeight,
                BufferedImage.TYPE_INT_RGB);
        float scale = this.getScale(src, theWidth, theHeight);
        int h = (int) (src.getHeight() * scale);
        int w = (int) (src.getWidth() * scale);
        logger.info("scale {} {} {}", scale, w, h);

        int x = 0;
        int y = 0;

        if (w < theWidth) {
            x = (theWidth - w) / 2;
        }

        if (h < theHeight) {
            y = (theHeight - h) / 2;
        }

        Image scaledImage = src.getScaledInstance((int) w, (int) h,
                Image.SCALE_SMOOTH);
        bufferedImage.getGraphics().drawImage(scaledImage, x, y, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtils.zoomImage(bufferedImage, baos, x1, y1, x2, y2);

        return new ByteArrayInputStream(baos.toByteArray());
    }

    public float getScale(BufferedImage src, int theWidth, int theHeight)
            throws IOException {
        int width = src.getWidth();
        int height = src.getHeight();

        float scale = 1f;

        if (width > height) {
            scale = (1f * theWidth) / width;
        } else {
            scale = (1f * theHeight) / height;
        }

        return scale;
    }

    // ~ ======================================================================
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountAvatarManager(
            AccountAvatarManager accountAvatarManager) {
        this.accountAvatarManager = accountAvatarManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
