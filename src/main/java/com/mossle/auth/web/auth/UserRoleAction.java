package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.LocalScopeDTO;
import com.mossle.api.ScopeConnector;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckUserStatusException;

import com.mossle.core.scope.ScopeHolder;
import com.mossle.core.struts2.BaseAction;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = UserRoleAction.RELOAD, location = "user-role.do?id=${id}&operationMode=RETRIEVE", type = "redirect") })
public class UserRoleAction extends BaseAction {
    public static final String RELOAD = "reload";
    private static Logger logger = LoggerFactory
            .getLogger(UserRoleAction.class);
    private UserStatusManager userStatusManager;
    private RoleManager roleManager;
    private MessageSourceAccessor messages;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private UserStatusChecker userStatusChecker;
    private ScopeConnector scopeConnector;
    private List<Role> roles;
    private AuthService authService;
    private Map<String, List<Role>> sharedRoleMap = new HashMap<String, List<Role>>();

    public String execute() throws Exception {
        return input();
    }

    public String save() {
        Long globalId = scopeConnector
                .findGlobalId(ScopeHolder.getGlobalCode());
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());

        try {
            authService.configUserRole(id, selectedItem, globalId, localId,
                    true);
            addActionMessage(messages.getMessage("core.success.save", "保存成功"));
        } catch (CheckUserStatusException ex) {
            addActionMessage(ex.getMessage());

            return input();
        }

        return RELOAD;
    }

    public String input() {
        // Long globalId = scopeConnector
        // .findGlobalId(ScopeHolder.getGlobalCode());
        Long localId = scopeConnector.findLocalId(ScopeHolder.getGlobalCode(),
                ScopeHolder.getLocalCode());
        // local roles
        roles = authService.findRoles(localId);

        /*
         * // shared role map List<LocalScopeDTO> sharedLocalScopes = scopeConnector .findSharedLocalScopes();
         * 
         * for (LocalScopeDTO localScopeDto : sharedLocalScopes) { sharedRoleMap.put(localScopeDto.getName(),
         * authService .findRoles(localScopeDto.getId())); }
         */
        return INPUT;
    }

    // ~ ======================================================================
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public void setUserStatusChecker(UserStatusChecker userStatusChecker) {
        this.userStatusChecker = userStatusChecker;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    // ~ ======================================================================
    public boolean containsRole(Long roleId) {
        try {
            String hql = "from Role r join r.userStatuses u where r.id=? and u.id=?";

            return roleManager.findUnique(hql, roleId, id) != null;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return false;
        }
    }

    // ~ ======================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Long> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Map<String, List<Role>> getSharedRoleMap() {
        return sharedRoleMap;
    }
}
