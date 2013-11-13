package com.mossle.security.perm;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class PermissionTag extends BodyTagSupport {
    private static final long serialVersionUID = 0L;
    private String permission;

    public int doStartTag() throws JspException {
        boolean authorized = getPermissionChecker().isAuthorized(permission);

        if (!authorized) {
            return Tag.SKIP_BODY;
        }

        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public PermissionChecker getPermissionChecker() {
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(pageContext.getServletContext());

        return (PermissionChecker) ctx.getBean("permissionChecker");
    }
}
