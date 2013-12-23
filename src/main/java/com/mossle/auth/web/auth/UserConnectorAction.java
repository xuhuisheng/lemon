package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.ServletUtils;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Results({
        @Result(name = UserConnectorAction.RELOAD, location = "user-connector.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = UserConnectorAction.RELOAD_PASSWORD, location = "user-connector!password.do?operationMode=RETRIEVE&id=${id}", type = "redirect"),
        @Result(name = UserConnectorAction.RELOAD_ROLE, location = "user-role.do?id=${id}", type = "redirect") })
public class UserConnectorAction extends BaseAction {
    private static Logger logger = LoggerFactory
            .getLogger(UserConnectorAction.class);
    public static final String RELOAD = "reload";
    public static final String RELOAD_PASSWORD = "reload-password";
    public static final String RELOAD_ROLE = "reload-role";
    private UserStatusManager userStatusManager;
    private Page page = new Page();
    private String username;
    private long id;
    private UserStatusConverter userStatusConverter;
    private UserConnector userConnector;
    private AuthService authService;
    private String reference;

    public String execute() {
        return list();
    }

    public String list() {
        Map<String, Object> parameters = ServletUtils
                .getParametersStartingWith(ServletActionContext.getRequest(),
                        "filter_");

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
        } else {
            // 如果设置了查询条件，就根据条件查询
            page = userConnector.pagedQuery(page, parameters);

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
                    userStatusDtos.add(userStatusDto);
                } else {
                    userStatusDtos.add(userStatusConverter.createUserStatusDto(
                            userStatus, ScopeHolder.getUserRepoRef(),
                            ScopeHolder.getScopeId()));
                }
            }

            page.setResult(userStatusDtos);
        }

        return SUCCESS;
    }

    public String configRole() {
        logger.info("reference : {}", reference);

        UserDTO userDto = userConnector.findById(reference);

        if (userDto != null) {
            username = userDto.getUsername();

            UserStatus userStatus = authService.createOrGetUserStatus(username,
                    userDto.getId(), ScopeHolder.getUserRepoRef(),
                    ScopeHolder.getScopeId());

            id = userStatus.getId();
        }

        return RELOAD_ROLE;
    }

    // ~ ======================================================================
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    public void setUserStatusConverter(UserStatusConverter userStatusConverter) {
        this.userStatusConverter = userStatusConverter;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    // ~ ======================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
