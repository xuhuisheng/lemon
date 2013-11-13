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

public class RegionTag extends BodyTagSupport {
    public static final long serialVersionUID = 0L;
    private static Logger logger = LoggerFactory.getLogger(RegionTag.class);
    private transient Iterator<RegionDTO> iterator;
    private String entityType;
    private String parentPath;

    public int doStartTag() throws JspException {
        if (entityType == null) {
            logger.error("entityType cannot be null");

            return Tag.SKIP_BODY;
        }

        RegionCache regionCache = this.getBean(RegionCache.class);

        logger.debug("entityType : {}, parentPath : {}", entityType, parentPath);

        if (parentPath == null) {
            iterator = regionCache.getByType(entityType).iterator();
            logger.debug("{}", regionCache.getByType(entityType));
        } else {
            iterator = regionCache.getByType(parentPath, entityType).iterator();
            logger.debug("{}", regionCache.getByType(parentPath, entityType));
        }

        if (iterator.hasNext()) {
            processRegion();

            return Tag.EVAL_BODY_INCLUDE;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        if (iterator == null) {
            return Tag.SKIP_BODY;
        }

        if (iterator.hasNext()) {
            processRegion();

            return IterationTag.EVAL_BODY_AGAIN;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    private void processRegion() {
        RegionDTO regionDto = iterator.next();
        pageContext.setAttribute("region", regionDto);
    }

    protected <T> T getBean(Class<T> clz) {
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(pageContext.getServletContext());

        return ctx.getBean(clz);
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
}
