package com.mossle.security.client;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CaptchaFilter.class);
    public static final String TYPE_ENABLE = "TYPE_ENABLE";
    public static final String TYPE_SESSION = "TYPE_SESSION";
    public static final String TYPE_SKIP = "TYPE_SKIP";
    private String loginProcessUrl = "/j_spring_security_check";
    private String captchaParameterName = "captcha";
    private String captchaSessionAttributeName = "captcha";
    private String captchaSessionTokenName = "captchaSessionToken";
    private String type = TYPE_SESSION;
    private String loginUrl = "/common/login.jsp?error=true";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (TYPE_SKIP.equals(type)) {
            logger.debug("skip");
            chain.doFilter(request, response);

            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);

        if (session == null) {
            logger.debug("session is null, skip");
            chain.doFilter(request, response);

            return;
        }

        if (!req.getRequestURI().equals(req.getContextPath() + loginProcessUrl)) {
            logger.debug("url : {} not match, skip", req.getRequestURI());
            chain.doFilter(request, response);

            return;
        }

        if (TYPE_SESSION.equals(type)) {
            Boolean captchaSessionToken = (Boolean) session
                    .getAttribute(captchaSessionTokenName);

            if ((captchaSessionToken == null)
                    || Boolean.FALSE.equals(captchaSessionToken)) {
                logger.debug("captchaSessionToken is null or false : {}",
                        captchaSessionToken);
                chain.doFilter(request, response);

                return;
            }
        }

        // TYPE_SESSION true or TYPE_ENABLE
        String captchaParameterValue = request
                .getParameter(captchaParameterName);
        String captchaSessionValue = (String) session
                .getAttribute(captchaSessionAttributeName);

        if ((captchaParameterValue != null)
                && captchaParameterValue.equals(captchaSessionValue)) {
            logger.debug("captcha match, pass");
            session.removeAttribute(captchaSessionTokenName);
            chain.doFilter(request, response);
        } else {
            logger.info("captcha not match");
            session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION",
                    new RuntimeException("验证码不匹配"));

            HttpServletResponse res = (HttpServletResponse) response;
            res.sendRedirect(req.getContextPath() + loginUrl);
        }
    }

    public void destroy() {
    }
}
