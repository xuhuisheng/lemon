package com.mossle.auth.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.UserStatusManager;
import com.mossle.auth.support.CheckUserStatusException;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.core.auth.CustomPasswordEncoder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class UserStatusController {
    private static Logger logger = LoggerFactory
            .getLogger(UserStatusController.class);
    private UserStatusManager userStatusManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserStatusConverter userStatusConverter;
    private UserStatusChecker userStatusChecker;
    private CustomPasswordEncoder customPasswordEncoder;
    private TenantHolder tenantHolder;

    @RequestMapping("user-status-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantHolder
                .getTenantId()));
        page = userStatusManager.pagedQuery(page, propertyFilters);

        List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
        List<UserStatusDTO> userStatusDtos = userStatusConverter
                .createUserStatusDtos(userStatuses,
                        tenantHolder.getUserRepoRef(),
                        tenantHolder.getTenantId());
        page.setResult(userStatusDtos);
        model.addAttribute("page", page);

        return "auth/user-status-list";
    }

    @RequestMapping("user-status-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            UserStatus userStatus = userStatusManager.get(id);
            model.addAttribute("model", userStatus);
        }

        return "auth/user-status-input";
    }

    @RequestMapping("user-status-save")
    public String save(@ModelAttribute UserStatus userStatus,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            userStatusChecker.check(userStatus);

            if (userStatus.getPassword() != null) {
                if (!userStatus.getPassword().equals(confirmPassword)) {
                    messageHelper.addFlashMessage(redirectAttributes,
                            "user.user.input.passwordnotequals", "两次输入密码不符");

                    return "auth/user-status-input";
                }

                if (customPasswordEncoder != null) {
                    userStatus.setPassword(customPasswordEncoder
                            .encode(userStatus.getPassword()));
                }
            }

            UserStatus dest = null;
            Long id = userStatus.getId();

            if (id != null) {
                dest = userStatusManager.get(id);
                beanMapper.copy(userStatus, dest);
            } else {
                dest = userStatus;
            }

            if (id == null) {
                dest.setUserRepoRef(tenantHolder.getUserRepoRef());
                dest.setTenantId(tenantHolder.getTenantId());
            }

            userStatusManager.save(dest);

            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckUserStatusException ex) {
            logger.warn(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return "auth/user-status-input";
        }

        return "redirect:/auth/user-status-list.do";
    }

    @RequestMapping("user-status-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        try {
            List<UserStatus> userStatuses = userStatusManager
                    .findByIds(selectedItem);

            for (UserStatus userStatus : userStatuses) {
                userStatusChecker.check(userStatus);
            }

            userStatusManager.removeAll(userStatuses);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.delete", "删除成功");
        } catch (CheckUserStatusException ex) {
            logger.warn(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());
        }

        return "redirect:/auth/user-status-list.do";
    }

    @RequestMapping("user-status-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = userStatusManager.pagedQuery(page, propertyFilters);

        List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
        List<UserStatusDTO> userStatusDtos = userStatusConverter
                .createUserStatusDtos(userStatuses,
                        tenantHolder.getUserRepoRef(),
                        tenantHolder.getTenantId());
        TableModel tableModel = new TableModel();
        tableModel.setName("user status");
        tableModel.addHeaders("id", "username", "enabled", "authorities");
        tableModel.setData(userStatusDtos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("user-status-password")
    public String password() {
        return "auth/user-status-password";
    }

    @RequestMapping("user-status-initPassword")
    public String initPassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        if ((newPassword != null) && newPassword.equals(confirmPassword)) {
            UserStatus userStatus = userStatusManager.get(id);
            userStatus.setPassword(newPassword);
            userStatusManager.save(userStatus);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "操作成功");

        return "redirect:/auth/user-status-password.do";
    }

    @RequestMapping("user-status-checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam("username") String username,
            @RequestParam(value = "id", required = false) Long id)
            throws Exception {
        String hql = "from UserStatus where username=?";
        Object[] params = { username };

        if (id != 0L) {
            hql = "from UserStatus where username=? and id<>?";
            params = new Object[] { username, id };
        }

        UserStatus userStatus = userStatusManager.findUnique(hql, params);

        boolean result = (userStatus == null);

        return result;
    }

    // ~ ======================================================================
    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setUserStatusConverter(UserStatusConverter userStatusConverter) {
        this.userStatusConverter = userStatusConverter;
    }

    @Resource
    public void setUserStatusChecker(UserStatusChecker userStatusChecker) {
        this.userStatusChecker = userStatusChecker;
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
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
