package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.domain.Access;
import com.mossle.auth.manager.AccessManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.AccessDTO;

import com.mossle.core.struts2.BaseAction;

import com.mossle.security.client.ResourcePublisher;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = AccessBatchAction.RELOAD, location = "access.do?operationMode=RETRIEVE", type = "redirect") })
public class AccessBatchAction extends BaseAction implements
        ModelDriven<Access>, Preparable {
    public static final String RELOAD = "reload";
    private AccessManager accessManager;
    private AuthService authService;
    private MessageSourceAccessor messages;
    private String type;
    private String perm;
    private List<AccessDTO> accessDtos;
    private List<String> ids;
    private List<String> values;
    private List<String> perms;
    private String text;
    private ResourcePublisher resourcePublisher;

    // ~ ======================================================================
    public String execute() {
        return SUCCESS;
    }

    public String input() {
        String hql = "from Access where type=? and scopeId=? order by priority";
        List<Access> accesses = accessManager.find(hql, type,
                ScopeHolder.getScopeId());
        StringBuilder buff = new StringBuilder();

        for (Access access : accesses) {
            String value = access.getValue();
            String permStr = "";

            if (access.getPerm() != null) {
                permStr = access.getPerm().getCode();
            }

            buff.append(value).append(",").append(permStr).append("\n");
        }

        text = buff.toString();

        return INPUT;
    }

    public String save() {
        authService.batchSaveAccess(text, type, ScopeHolder.getScopeId());

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        resourcePublisher.publish();

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public Access getModel() {
        return null;
    }

    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setResourcePublisher(ResourcePublisher resourcePublisher) {
        this.resourcePublisher = resourcePublisher;
    }

    // ~ ======================================================================
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    public List<AccessDTO> getAccessDtos() {
        return accessDtos;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
