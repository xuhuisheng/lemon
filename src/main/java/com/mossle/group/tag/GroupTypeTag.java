package com.mossle.group.tag;

import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

import com.mossle.group.component.GroupTypeCache;
import com.mossle.group.component.GroupTypeDTO;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class GroupTypeTag extends BodyTagSupport {
    private static final long serialVersionUID = 0L;
    private transient Iterator<GroupTypeDTO> iterator;
    private String globalCode;

    public int doStartTag() throws JspException {
        GroupTypeCache groupTypeCache = this.getBean(GroupTypeCache.class);

        if (groupTypeCache.getGroupTypeDtos(globalCode) == null) {
            return Tag.SKIP_BODY;
        }

        iterator = groupTypeCache.getGroupTypeDtos(globalCode).iterator();

        if (iterator.hasNext()) {
            processGroupType();

            return Tag.EVAL_BODY_INCLUDE;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        if (iterator.hasNext()) {
            processGroupType();

            return IterationTag.EVAL_BODY_AGAIN;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    private void processGroupType() {
        GroupTypeDTO groupTypeDto = iterator.next();
        pageContext.setAttribute("groupType", groupTypeDto);
    }

    protected <T> T getBean(Class<T> clz) {
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(pageContext.getServletContext());

        return ctx.getBean(clz);
    }

    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
    }
}
