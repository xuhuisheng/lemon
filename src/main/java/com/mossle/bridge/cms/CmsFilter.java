package com.mossle.bridge.cms;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeDTO;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.util.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CmsFilter.class);
    private String baseDir;

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {
        ScopeDTO scopeDto = ScopeHolder.getScopeDto();
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        logger.debug("requestURI : {}", request.getRequestURI());
        logger.debug("requestURL : {}", request.getRequestURL());
        logger.debug("servletPath : {}", request.getServletPath());
        logger.debug("pathInfo : {}", request.getPathInfo());

        String servletPath = request.getServletPath();

        if (scopeDto.getType() != ScopeDTO.TYPE_CMS) {
            if (servletPath.startsWith("/cms/r/")) {
                String path = baseDir + servletPath.substring("/cms".length());
                logger.debug("path : {}", path);
                IoUtils.copyFileToOutputStream(path, response.getOutputStream());
            } else {
                filterChain.doFilter(req, res);
            }

            return;
        }

        if ("/dashboard/dashboard.do".equals(servletPath)) {
            response.sendRedirect(request.getContextPath() + "/index.html");

            return;
        }

        IoUtils.copyFileToOutputStream(baseDir + servletPath,
                response.getOutputStream());
    }

    public void destroy() {
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
