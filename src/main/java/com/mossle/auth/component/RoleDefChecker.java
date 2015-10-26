package com.mossle.auth.component;

import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.support.CheckRoleException;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Component;

@Component
public class RoleDefChecker implements MessageSourceAware {
    private MessageSourceAccessor messages;

    public void check(RoleDef roleDef) {
        if ((roleDef.getId() != null) && (roleDef.getId() == 1)) {
            throw new CheckRoleException(messages.getMessage(
                    "auth.superuser.edit", "不允许修改超级管理员角色"));
        }
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
