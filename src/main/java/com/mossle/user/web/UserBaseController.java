package com.mossle.user.web;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.ext.auth.CustomPasswordEncoder;
import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.MultipartFileResource;
import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.domain.UserRepo;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserRepoManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

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
public class UserBaseController {
    private UserBaseManager userBaseManager;
    private UserRepoManager userRepoManager;
    private UserCache userCache;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CustomPasswordEncoder customPasswordEncoder;
    private UserService userService;
    private StoreConnector storeConnector;

    @RequestMapping("user-base-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        UserRepo userRepo = userRepoManager.findUniqueBy("code",
                ScopeHolder.getScopeCode());
        propertyFilters.add(new PropertyFilter("EQL_userRepo.id", Long
                .toString(userRepo.getId())));
        page = userBaseManager.pagedQuery(page, propertyFilters);

        List<UserBase> userBases = (List<UserBase>) page.getResult();
        List<UserBaseWrapper> userBaseWrappers = new ArrayList<UserBaseWrapper>();

        for (UserBase userBase : userBases) {
            userBaseWrappers.add(new UserBaseWrapper(userBase));
        }

        page.setResult(userBaseWrappers);
        model.addAttribute("page", page);

        return "user/user-base-list";
    }

    @RequestMapping("user-base-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        UserBase userBase = null;

        if (id != null) {
            userBase = userBaseManager.get(id);
        } else {
            userBase = new UserBase();

            UserRepo userRepo = userRepoManager.findUniqueBy("code",
                    ScopeHolder.getScopeCode());
            userBase.setUserRepo(userRepo);
        }

        UserBaseWrapper userBaseWrapper = new UserBaseWrapper(userBase);
        model.addAttribute("model", userBase);
        model.addAttribute("userBaseWrapper", userBaseWrapper);

        return "user/user-base-input";
    }

    @RequestMapping("user-base-save")
    public String save(
            @ModelAttribute UserBase userBase,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam("userRepoId") Long userRepoId,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes, HttpSession httpSession)
            throws Exception {
        // 先进行校验
        if (userBase.getPassword() != null) {
            if (!userBase.getPassword().equals(confirmPassword)) {
                messageHelper.addFlashMessage(redirectAttributes,
                        "user.user.input.passwordnotequals", "两次输入密码不符");

                // TODO: 还要填充schema
                return "user/user-base-input";
            }

            if (customPasswordEncoder != null) {
                userBase.setPassword(customPasswordEncoder.encode(userBase
                        .getPassword()));
            }
        }

        Map<String, Object> parameters = ServletUtils
                .getParametersStartingWith(parameterMap, "_user_attr_");

        // 再进行数据复制
        UserBase dest = null;
        Long id = userBase.getId();

        if (id != null) {
            dest = userBaseManager.get(id);
            dest.setStatus(0);
            beanMapper.copy(userBase, dest);
            userService.updateUser(dest, userRepoId, parameters);
        } else {
            dest = userBase;
            dest.setAvatar((String) httpSession.getAttribute("temporaryAvatar"));
            userService.insertUser(dest, userRepoId, parameters);
            httpSession.removeAttribute("temporaryAvatar");
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        UserDTO userDto = new UserDTO();
        userDto.setId(Long.toString(dest.getId()));
        userDto.setUsername(dest.getUsername());
        userDto.setRef(dest.getRef());
        userDto.setUserRepoRef(Long.toString(userRepoId));
        userCache.removeUser(userDto);

        return "redirect:/user/user-base-list.do";
    }

    @RequestMapping("user-base-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<UserBase> userBases = userBaseManager.findByIds(selectedItem);

        for (UserBase userBase : userBases) {
            userService.removeUser(userBase);

            UserDTO userDto = new UserDTO();
            userDto.setId(Long.toString(userBase.getId()));
            userDto.setUsername(userBase.getUsername());
            userDto.setRef(userBase.getRef());
            userDto.setUserRepoRef(Long
                    .toString(userBase.getUserRepo().getId()));
            userCache.removeUser(userDto);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/user/user-base-list.do";
    }

    @RequestMapping("user-base-search")
    public String search(@ModelAttribute Page page, Model model) {
        StringBuilder buff = new StringBuilder("select ub from UserBase ub");

        Map<String, Object> params = new HashMap<String, Object>();

        if (!params.isEmpty()) {
            page = userBaseManager.pagedQuery(buff.toString(),
                    page.getPageNo(), page.getPageSize(), params);

            List<UserBase> userBases = (List<UserBase>) page.getResult();
            List<UserBaseWrapper> userBaseWrappers = new ArrayList<UserBaseWrapper>();

            for (UserBase userBase : userBases) {
                userBaseWrappers.add(new UserBaseWrapper(userBase));
            }

            page.setResult(userBaseWrappers);
            model.addAttribute("page", page);
        }

        return "user/user-base-search";
    }

    @RequestMapping("user-base-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = userBaseManager.pagedQuery(page, propertyFilters);

        List<UserBase> userBases = (List<UserBase>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("user");
        tableModel.addHeaders("id", "username", "status");
        tableModel.setData(userBases);
        exportor.export(response, tableModel);
    }

    @RequestMapping("user-base-checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from UserBase where username=?";
        Object[] params = { username };

        if (id != null) {
            hql = "from UserBase where username=? and id<>?";
            params = new Object[] { username, id };
        }

        boolean result = userBaseManager.findUnique(hql, params) == null;

        return result;
    }

    /**
     * 上传附件.
     */
    @RequestMapping("user-base-upload")
    @ResponseBody
    public String upload(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("avatar") MultipartFile avatar,
            HttpSession httpSession) throws Exception {
        StoreDTO storeDto = storeConnector
                .save("avatar", new MultipartFileResource(avatar),
                        avatar.getOriginalFilename());

        if (id != null) {
            UserBase userBase = userBaseManager.get(id);
            userBase.setAvatar(storeDto.getKey());
            userBaseManager.save(userBase);
        } else {
            httpSession.setAttribute("temporaryAvatar", storeDto.getKey());
        }

        return "{\"success\":true,\"id\":\"" + ((id == null) ? "" : id) + "\"}";
    }

    @RequestMapping("user-base-avatar")
    @ResponseBody
    public void avatar(@RequestParam(value = "id", required = false) Long id,
            OutputStream os, HttpSession httpSession) throws Exception {
        if (id != null) {
            UserBase userBase = userBaseManager.get(id);
            StoreDTO storeDto = storeConnector.get("avatar",
                    userBase.getAvatar());

            IoUtils.copyStream(storeDto.getResource().getInputStream(), os);
        } else {
            StoreDTO storeDto = storeConnector.get("avatar",
                    (String) httpSession.getAttribute("temporaryAvatar"));

            IoUtils.copyStream(storeDto.getResource().getInputStream(), os);
        }
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
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
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
