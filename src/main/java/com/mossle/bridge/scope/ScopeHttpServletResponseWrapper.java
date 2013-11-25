package com.mossle.bridge.scope;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ScopeHttpServletResponseWrapper extends HttpServletResponseWrapper {
    public static final int DEFAULT_HTTP_PORT = 80;
    private String contextPath;
    private String prefix;
    private String absolutePrefix;

    public ScopeHttpServletResponseWrapper(HttpServletRequest request,
            HttpServletResponse httpServletResponse, String scopeCode) {
        super(httpServletResponse);
        this.contextPath = request.getContextPath();
        this.prefix = contextPath + "/" + scopeCode;
        this.absolutePrefix = request.getScheme() + "://"
                + request.getServerName();

        if (request.getServerPort() != DEFAULT_HTTP_PORT) {
            absolutePrefix += (":" + request.getServerPort());
        }

        absolutePrefix += contextPath;
    }

    public void sendRedirect(String url) throws IOException {
        String targetUrl = url;

        if (targetUrl.startsWith("/")) {
            targetUrl = prefix + targetUrl.substring(contextPath.length());
        } else if (targetUrl.startsWith(absolutePrefix)) {
            targetUrl = prefix + targetUrl.substring(absolutePrefix.length());
        }

        super.sendRedirect(targetUrl);
    }
}
