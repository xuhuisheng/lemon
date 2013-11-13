package com.mossle.core.scope;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ScopeHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private HttpServletRequest request;
    private String contextPath;
    private String prefix;
    private String absolutePrefix;

    public ScopeHttpServletResponseWrapper(HttpServletRequest request,
            HttpServletResponse httpServletResponse, String globalCode,
            String localCode) {
        super(httpServletResponse);
        this.request = request;
        this.contextPath = request.getContextPath();
        this.prefix = contextPath + "/" + globalCode + "/" + localCode;
        this.absolutePrefix = request.getScheme() + "://"
                + request.getServerName();

        if (request.getServerPort() != 80) {
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
