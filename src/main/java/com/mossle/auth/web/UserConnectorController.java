package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.core.page.Page;
import com.mossle.core.util.ServletUtils;

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
public class UserConnectorController {
    private static Logger logger = LoggerFactory
            .getLogger(UserConnectorController.class);
    private UserStatusManager userStatusManager;
    private UserStatusConverter userStatusConverter;
    private UserConnector userConnector;
    private AuthService authService;

    @RequestMapping("user-connector-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        Map<String, Object> parameters = ServletUtils
                .getParametersStartingWith(parameterMap, "filter_");

        // 缩小显示范围，把所有用户都显示出来也没什么用途
        if (parameters.isEmpty()) {
            // 如果没有查询条件，就只返回配置了权限的用户
            String hql = "from UserStatus where scopeId=?";
            page = userStatusManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), ScopeHolder.getScopeId());

            List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
            List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

            for (UserStatus userStatus : userStatuses) {
                userStatusDtos.add(userStatusConverter.createUserStatusDto(
                        userStatus, ScopeHolder.getUserRepoRef(),
                        ScopeHolder.getScopeId()));
            }

            page.setResult(userStatusDtos);
            model.addAttribute("page", page);
        } else {
            // 如果设置了查询条件，就根据条件查询
            page = userConnector.pagedQuery(page, parameterMap);

            List<UserDTO> userDtos = (List<UserDTO>) page.getResult();
            List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

            for (UserDTO userDto : userDtos) {
                String usernameStr = userDto.getUsername();
                String hql = "from UserStatus where username=? and userRepoRef=?";
                UserStatus userStatus = userStatusManager.findUnique(hql,
                        usernameStr, ScopeHolder.getUserRepoRef());

                if (userStatus == null) {
                    UserStatusDTO userStatusDto = new UserStatusDTO();
                    userStatusDto.setUsername(usernameStr);
                    userStatusDto.setEnabled(true);
                    userStatusDto.setRef(userDto.getId());
                    userStatusDtos.add(userStatusDto);
                } else {
                    userStatusDtos.add(userStatusConverter.createUserStatusDto(
                            userStatus, ScopeHolder.getUserRepoRef(),
                            ScopeHolder.getScopeId()));
                }
            }

            page.setResult(userStatusDtos);
            model.addAttribute("page", page);
        }

        return "auth/user-connector-list";
    }

    @RequestMapping("user-connector-configRole")
    public String configRole(@RequestParam("ref") String ref) {
        logger.debug("ref : {}", ref);

        UserDTO userDto = userConnector.findByRef(ref,
                ScopeHolder.getUserRepoRef());
        Long id = null;

        if (userDto != null) {
            String username = userDto.getUsername();

            UserStatus userStatus = authService.createOrGetUserStatus(username,
                    userDto.getId(), ScopeHolder.getUserRepoRef(),
                    ScopeHolder.getScopeId());

            id = userStatus.getId();
        }

        return "redirect:/auth/user-role-input.do?id=" + id;
    }

    // ~ ======================================================================
    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setUserStatusConverter(UserStatusConverter userStatusConverter) {
        this.userStatusConverter = userStatusConverter;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
