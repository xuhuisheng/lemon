package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.ServletUtils;

import com.mossle.security.util.SpringSecurityUtils;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.UserAttrManager;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserSchemaManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class ProfileController {
    private UserBaseManager userBaseManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private UserBaseWrapper userBaseWrapper;
    private UserService userService;

    @RequestMapping("profile-list")
    public String list(Model model) {
        UserBase userBase = userBaseManager.findUniqueBy("username",
                SpringSecurityUtils.getCurrentUsername());
        UserBaseWrapper userBaseWrapper = new UserBaseWrapper(userBase);
        model.addAttribute("model", userBase);
        model.addAttribute("userBaseWrapper", userBaseWrapper);

        return "user/profile-list";
    }

    @RequestMapping("profile-save")
    public String save(@ModelAttribute UserBase userBase,
            @RequestParam("userRepoId") Long userRepoId,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) throws Exception {
        Map<String, Object> parameters = ServletUtils
                .getParametersStartingWith(parameterMap, "_user_attr_");
        Long id = userBase.getId();

        // 再进行数据复制
        UserBase dest = null;

        if (id != null) {
            dest = userBaseManager.get(id);
            beanMapper.copy(userBase, dest);
            userService.updateUser(dest, userRepoId, parameters);
        } else {
            dest = userBase;
            userService.insertUser(dest, userRepoId, parameters);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/profile-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
