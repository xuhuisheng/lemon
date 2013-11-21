package com.mossle.acl.web.acl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.acl.domain.AclObjectIdentity;
import com.mossle.acl.service.AclService;

import com.mossle.api.UserConnector;
import com.mossle.api.UserDTO;
import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.scope.ScopeInfo;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;
import com.mossle.core.util.ServletUtils;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = UserConnectorBatchAction.RELOAD, location = "acl-entry.do?operationMode=RETRIEVE", type = "redirect") })
public class UserConnectorBatchAction extends BaseAction {
    private static Logger logger = LoggerFactory
            .getLogger(UserConnectorBatchAction.class);
    public static final String RELOAD = "reload";
    private String userText;
    private List<Long> userIds = new ArrayList<Long>();
    private UserConnector userConnector;
    private ScopeConnector scopeConnector;
    private AclService aclService;
    private String resourceType;
    private Long resourceId;
    private List<UserDTO> userDtos = new ArrayList<UserDTO>();

    public String execute() {
        return SUCCESS;
    }

    public String input() {
        if (userText != null) {
            for (String str : userText.split("\n")) {
                str = str.trim();

                if (str.length() == 0) {
                    continue;
                }

                String username = str;
                UserDTO userDto = userConnector.findByUsername(username,
                        ScopeHolder.getUserRepoRef());

                if (userDto == null) {
                    addActionMessage(str + " is not exists.");
                } else {
                    userDtos.add(userDto);
                }
            }
        }

        return INPUT;
    }

    public String save() {
        logger.debug("userIds: {}", userIds);

        for (Long userId : userIds) {
            Long sidId = aclService.getSidId(String.valueOf(userId),
                    String.valueOf(1));
            AclObjectIdentity aclObjectIdentity = aclService
                    .createOrFindAclObjectIdentity(String.valueOf(resourceId),
                            resourceType);
            aclService.createOrFindAclEntry(sidId, aclObjectIdentity, 1);
        }

        return RELOAD;
    }

    // ~ ======================================================================
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public void setAclService(AclService aclService) {
        this.aclService = aclService;
    }

    // ~ ======================================================================
    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<UserDTO> getUserDtos() {
        return userDtos;
    }
}
