package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckUserStatusException;

import com.mossle.core.struts2.BaseAction;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.jdbc.core.JdbcTemplate;

@Results({ @Result(name = AuthAction.RELOAD, location = "auth.do?operationMode=RETRIEVE", type = "redirect") })
public class AuthAction extends BaseAction {
    public static final String RELOAD = "reload";
    private AuthService authService;
    private String text;

    public String execute() throws Exception {
        text = authService.doExport();

        return SUCCESS;
    }

    public String save() {
        authService.doImport(text);

        return RELOAD;
    }

    // ~ ======================================================================
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
