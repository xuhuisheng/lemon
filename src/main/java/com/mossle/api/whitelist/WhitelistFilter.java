package com.mossle.api.whitelist;

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

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhitelistFilter implements Filter {
    private static Logger logger = LoggerFactory
            .getLogger(WhitelistFilter.class);
    private WhitelistConnector whitelistConnector;
    private String code;
    private WhitelistDTO whitelistDto;
    private long timestamp;
    private String defaultTenantCode = "default";
    private TenantConnector tenantConnector;

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        this.checkAndReload();

        if (whitelistDto.notValidIp(request.getRemoteAddr())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        filterChain.doFilter(req, res);
    }

    public void checkAndReload() {
        if ((whitelistDto != null) && (System.currentTimeMillis() > timestamp)) {
            return;
        }

        TenantDTO tenantDto = tenantConnector.findByCode(defaultTenantCode);
        whitelistDto = whitelistConnector.getWhitelist(code, tenantDto.getId());
        timestamp = System.currentTimeMillis() + (1000 * 60 * 2);
    }

    public void destroy() {
    }

    public void setWhitelistConnector(WhitelistConnector whitelistConnector) {
        this.whitelistConnector = whitelistConnector;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDefaultTenantCode(String defaultTenantCode) {
        this.defaultTenantCode = defaultTenantCode;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
