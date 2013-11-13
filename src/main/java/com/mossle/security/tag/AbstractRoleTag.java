package com.mossle.security.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

public abstract class AbstractRoleTag extends BodyTagSupport {
    private static final long serialVersionUID = 0L;
    private String role;

    public int doStartTag() throws JspException {
        if (role == null) {
            return Tag.SKIP_BODY;
        }

        String[] roles = role.split(",");
        boolean authorized = this.checkRoles(roles);

        if (!authorized) {
            return Tag.SKIP_BODY;
        }

        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setRole(String role) {
        this.role = role;
    }

    protected abstract boolean checkRoles(String[] roles);
}
