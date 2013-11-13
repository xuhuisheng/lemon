package com.mossle.security.region;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class RegionPermissionTag extends BodyTagSupport {
    public static final long serialVersionUID = 0L;
    private static Logger logger = LoggerFactory
            .getLogger(RegionPermissionTag.class);
    private String permission = null;
    private String region = null;

    public int doStartTag() throws JspException {
        if (permission == null) {
            logger.error("permission should not be null");

            return Tag.SKIP_BODY;
        }

        if (region == null) {
            region = pageContext.getRequest().getParameter("region");
            logger.debug("try to fetch region parameter from request : {}",
                    region);
        }

        String text = getBean(PermissionDecorator.class).decorate(permission,
                region);

        logger.debug("decoded text : {}", text);

        boolean authorized = getBean(PermissionChecker.class)
                .isAuthorized(text);

        if (!authorized) {
            return Tag.SKIP_BODY;
        }

        return Tag.EVAL_BODY_INCLUDE;
    }

    protected <T> T getBean(Class<T> clz) {
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(pageContext.getServletContext());

        return ctx.getBean(clz);
    }

    // ~ ======================================================================
    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
