package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.api.ScopeConnector;
import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;
import com.mossle.api.UserRepoConnector;
import com.mossle.api.UserRepoDTO;

import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.page.Page;
import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.ServletUtils;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

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
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private String username;
    private long id;
    private UserStatusConverter userStatusConverter;
    private UserConnector userConnector;
    private ScopeConnector scopeConnector;
    private AuthService authService;

    public String execute() {
        return list();
    }

    public String list() {
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());

        Map<String, Object> parameters = ServletUtils
                .getParametersStartingWith(ServletActionContext.getRequest(),
                        "filter_");

        // 缩小显示范围，把所有用户都显示出来也没什么用途
        if (parameters.isEmpty()) {
            // 如果没有查询条件，就只返回配置了权限的用户
            String hql = "select distinct u from UserStatus u join u.roles r where u.globalId=? and r.localId=?";
            page = userStatusManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), globalId, localId);

            List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
            List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

            for (UserStatus userStatus : userStatuses) {
                userStatusDtos.add(userStatusConverter.createUserStatusDto(
                        userStatus, globalId, localId));
            }

            page.setResult(userStatusDtos);
        } else {
            // 如果设置了查询条件，就根据条件查询
            parameters.put("EQL_GLOBAL_ID", Long.toString(globalId));
            page = userConnector.pagedQuery(page, parameters);

            List<UserDTO> userDtos = (List<UserDTO>) page.getResult();
            List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

            for (UserDTO userDto : userDtos) {
                String usernameStr = userDto.getUsername();
                String hql = "from UserStatus where username=? and globalId=?";
                UserStatus userStatus = userStatusManager.findUnique(hql,
                        usernameStr, globalId);

                if (userStatus == null) {
                    UserStatusDTO userStatusDto = new UserStatusDTO();
                    userStatusDto.setUsername(usernameStr);
                    userStatusDto.setEnabled(true);
                    userStatusDtos.add(userStatusDto);
                } else {
                    userStatusDtos.add(userStatusConverter.createUserStatusDto(
                            userStatus, globalId, localId));
                }
            }

            page.setResult(userStatusDtos);
        }

        return SUCCESS;
    }

    public String configRole() {
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());
        UserDTO userDto = userConnector.findByUsername(username, globalId);

        if (userDto != null) {
            UserStatus userStatus = authService.createOrGetUserStatus(username,
                    userDto.getId(), globalId, localId);

            id = userStatus.getId();
        }

        return RELOAD_ROLE;
    }

    // ~ ======================================================================
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
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

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
