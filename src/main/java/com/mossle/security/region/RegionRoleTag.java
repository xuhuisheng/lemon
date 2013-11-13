package com.mossle.security.region;

import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class RegionRoleTag extends BodyTagSupport {
    public static final long serialVersionUID = 0L;
    private static Logger logger = LoggerFactory.getLogger(RegionRoleTag.class);
    private transient Iterator<RoleDTO> iterator;
    private String region = RegionConstants.SYSTEM_REGION;
    private String regionPath = null;

    public int doStartTag() throws JspException {
        RegionRoleCache regionRoleCache = this.getBean(RegionRoleCache.class);

        if (regionPath != null) {
            logger.debug("regionPath : {}", regionPath);
            iterator = regionRoleCache.getByRegionPath(regionPath).iterator();
        } else {
            logger.debug("region : {}", region);
            iterator = regionRoleCache.getByRegion(region).iterator();
        }

        if (iterator.hasNext()) {
            processRole();

            return Tag.EVAL_BODY_INCLUDE;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        if (iterator.hasNext()) {
            processRole();

            return IterationTag.EVAL_BODY_AGAIN;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    private void processRole() {
        RoleDTO roleDto = iterator.next();
        pageContext.setAttribute("role", roleDto);
    }

    protected <T> T getBean(Class<T> clz) {
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(pageContext.getServletContext());

        return ctx.getBean(clz);
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setRegionPath(String regionPath) {
        this.regionPath = regionPath;
    }
}
