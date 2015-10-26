package com.mossle.spi.attachment;

import javax.annotation.Resource;

import javax.servlet.ServletContext;

public class LocalAttachmentConnector implements AttachmentConnector {
    private ServletContext servletContext;

    public String getPrefix() {
        return servletContext.getContextPath();
    }

    @Resource
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
