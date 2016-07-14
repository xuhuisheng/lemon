package com.mossle.spi.device;

import java.io.IOException;

import javax.annotation.Resource;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeviceFilter implements Filter {
    private DeviceConnector deviceConnector;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String deviceId = this.getCookie((HttpServletRequest) request,
                "SECURITY_DEVICE_ID");

        if (deviceId == null) {
            filterChain.doFilter(request, response);

            return;
        }

        DeviceDTO deviceDto = deviceConnector.findDevice(deviceId);

        if ((deviceDto != null) && "disabled".equals(deviceDto.getStatus())) {
            HttpServletResponse res = (HttpServletResponse) response;

            res.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        filterChain.doFilter(request, response);
    }

    public String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ((cookie == null) || (cookie.getName() == null)) {
                continue;
            }

            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    @Resource
    public void setDeviceConnector(DeviceConnector deviceConnector) {
        this.deviceConnector = deviceConnector;
    }
}
