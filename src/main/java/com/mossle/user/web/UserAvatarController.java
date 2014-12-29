package com.mossle.user.web;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mossle.api.internal.StoreConnector;
import com.mossle.api.internal.StoreDTO;
import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.InputStreamDataSource;
import com.mossle.ext.store.MultipartFileDataSource;

import com.mossle.user.ImageUtils;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

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
@RequestMapping("user")
public class UserAvatarController {
    private UserBaseManager userBaseManager;
    private UserRepoManager userRepoManager;
    private UserCache userCache;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserService userService;
    private StoreConnector storeConnector;

    @RequestMapping("user-avatar-input")
    public String input(@RequestParam("id") Long id, Model model) {
        UserBase userBase = userBaseManager.get(id);
        model.addAttribute("userBase", userBase);

        return "user/user-avatar-input";
    }

    /**
     * 上传.
     */
    @RequestMapping("user-avatar-upload")
    @ResponseBody
    public String upload(@RequestParam("id") Long id,
            @RequestParam("avatar") MultipartFile avatar) throws Exception {
        StoreDTO storeDto = storeConnector.saveStore("avatar",
                new MultipartFileDataSource(avatar));

        UserBase userBase = userBaseManager.get(id);
        userBase.setAvatar(storeDto.getKey());
        userBaseManager.save(userBase);

        return "{\"success\":true,\"id\":\"" + id + "\"}";
    }

    /**
     * 显示.
     */
    @RequestMapping("user-avatar-view")
    @ResponseBody
    public void avatar(@RequestParam("id") Long id, OutputStream os)
            throws Exception {
        UserBase userBase = userBaseManager.get(id);
        StoreDTO storeDto = storeConnector.getStore("avatar",
                userBase.getAvatar());

        IoUtils.copyStream(storeDto.getDataSource().getInputStream(), os);
    }

    @RequestMapping("user-avatar-crop")
    public String crop(@RequestParam("id") Long id, Model model)
            throws Exception {
        UserBase userBase = userBaseManager.get(id);
        model.addAttribute("userBase", userBase);

        StoreDTO storeDto = storeConnector.getStore("avatar",
                userBase.getAvatar());
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

        return "user/user-avatar-crop";
    }

    @RequestMapping("user-avatar-save")
    public String save(@RequestParam("id") Long id, @RequestParam("x1") int x1,
            @RequestParam("x2") int x2, @RequestParam("y1") int y1,
            @RequestParam("y2") int y2, @RequestParam("w") int w, Model model)
            throws Exception {
        UserBase userBase = userBaseManager.get(id);
        model.addAttribute("userBase", userBase);

        StoreDTO storeDto = storeConnector.getStore("avatar",
                userBase.getAvatar());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtils.zoomImage(storeDto.getDataSource().getInputStream(), baos,
                x1, y1, x2, y2);

        storeDto = storeConnector.saveStore("avatar",
                new InputStreamDataSource(w + ".png", new ByteArrayInputStream(
                        baos.toByteArray())));
        userBase.setAvatar(storeDto.getKey());
        userBaseManager.save(userBase);

        return "user/user-avatar-save";
    }

    // ~ ======================================================================
    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    @Resource
    public void setUserRepoManager(UserRepoManager userRepoManager) {
        this.userRepoManager = userRepoManager;
    }

    @Resource
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }
}
