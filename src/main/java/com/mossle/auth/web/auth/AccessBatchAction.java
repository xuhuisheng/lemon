package com.mossle.auth.web.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.domain.Access;
import com.mossle.auth.manager.AccessManager;
import com.mossle.auth.support.AccessDTO;

import com.mossle.core.struts2.BaseAction;

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
    private MessageSourceAccessor messages;
    private String type;
    private String perm;
    private List<AccessDTO> accessDtos;
    private List<String> ids;
    private List<String> values;
    private List<String> perms;

    // ~ ======================================================================
    public String execute() {
        return SUCCESS;
    }

    public String input() {
        String hql = "from Access where type=? and scopeId=? order by priority";
        List<Access> accesses = accessManager.find(hql, type,
                ScopeHolder.getScopeId());
        accessDtos = new ArrayList<AccessDTO>();

        for (Access access : accesses) {
            String value = access.getValue();
            String permStr = "";

            if (access.getPerm() != null) {
                permStr = access.getPerm().getName();
            }

            accessDtos.add(new AccessDTO(value, permStr));
        }

        return INPUT;
    }

    public String edit() {
        String hql = "from Access where type=? and scopeId=? order by priority";
        List<Access> accesses = accessManager.find(hql, type,
                ScopeHolder.getScopeId());

        accessDtos = new ArrayList<AccessDTO>();

        Set<String> noDuplicatedValues = new HashSet<String>();

        for (Access access : accesses) {
            Long id = access.getId();
            String value = access.getValue();

            String permStr = "";

            if (access.getPerm() != null) {
                permStr = access.getPerm().getName();
            }

            accessDtos.add(new AccessDTO(id, value, permStr));
            noDuplicatedValues.add(value);
        }

        if (perm != null) {
            for (String str : perm.split("\n")) {
                str = str.trim();

                if (str.length() == 0) {
                    continue;
                }

                if (noDuplicatedValues.contains(str)) {
                    addActionMessage(str + " is duplicated.");
                } else {
                    accessDtos.add(new AccessDTO(str, str));

                    noDuplicatedValues.add(str);
                }
            }
        }

        return "edit";
    }

    public String save() {
        accessManager.batchSave(ScopeHolder.getScopeId(), type, ids, values,
                perms);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

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

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
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
}
