package com.mossle.cms.support;

import java.io.IOException;

import javax.annotation.Resource;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.util.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CmsFilter.class);
    private TenantHolder tenantHolder;
    private String storeBaseDir = "mossle.store";
    private String cmsBaseDir = "/cms/html";

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {
        TenantDTO tenantDto = null;

        try {
            tenantDto = tenantHolder.getTenantDto();
        } catch (Exception ex) {
            filterChain.doFilter(req, res);

            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        logger.debug("requestURI : {}", request.getRequestURI());
        logger.debug("requestURL : {}", request.getRequestURL());
        logger.debug("servletPath : {}", request.getServletPath());
        logger.debug("pathInfo : {}", request.getPathInfo());

        String servletPath = request.getServletPath();

        if (TenantDTO.TYPE_CMS.equals(tenantDto.getType())) {
            if (servletPath.startsWith("/cms/r/")) {
                String path = storeBaseDir + "/" + tenantDto.getId()
                        + cmsBaseDir + servletPath.substring("/cms".length());
                logger.debug("path : {}", path);
                IoUtils.copyFileToOutputStream(path, response.getOutputStream());
            } else {
                filterChain.doFilter(req, res);
            }

            return;
        }

        if ("/portal/index.do".equals(servletPath)) {
            response.sendRedirect(request.getContextPath() + "/index.html");

            return;
        }

        IoUtils.copyFileToOutputStream(storeBaseDir + "/" + tenantDto.getId()
                + cmsBaseDir + servletPath, response.getOutputStream());
    }

    public void destroy() {
    }

    public void setStoreBaseDir(String storeBaseDir) {
        this.storeBaseDir = storeBaseDir;
    }

    public void setCmsBaseDir(String cmsBaseDir) {
        this.cmsBaseDir = cmsBaseDir;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
