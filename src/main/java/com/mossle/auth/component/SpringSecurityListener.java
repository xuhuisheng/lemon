package com.mossle.auth.component;

import java.net.InetAddress;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.audit.AuditConnector;
import com.mossle.api.audit.AuditDTO;

import com.mossle.ext.auth.LoginEvent;
import com.mossle.ext.auth.LogoutEvent;

import com.mossle.security.impl.SpringSecurityUserAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import org.springframework.stereotype.Component;

import org.springframework.util.ClassUtils;

@Component
public class SpringSecurityListener implements ApplicationListener,
        ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(SpringSecurityListener.class);
    private AuditConnector auditConnector;
    private ApplicationContext ctx;

    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (event instanceof InteractiveAuthenticationSuccessEvent) {
                InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent = (InteractiveAuthenticationSuccessEvent) event;
                Authentication authentication = interactiveAuthenticationSuccessEvent
                        .getAuthentication();

                Object principal = authentication.getPrincipal();
                String user = null;

                if (principal instanceof SpringSecurityUserAuth) {
                    user = ((SpringSecurityUserAuth) principal).getId();
                } else {
                    user = authentication.getName();
                }

                AuditDTO auditDto = new AuditDTO();
                auditDto.setUser(user);
                auditDto.setAuditTime(new Date());
                auditDto.setAction("login");
                auditDto.setResult("success");
                auditDto.setApplication("lemon");
                auditDto.setClient(getUserIp(authentication));
                auditDto.setServer(InetAddress.getLocalHost().getHostAddress());
                auditConnector.log(auditDto);

                // 登录成功，再发送一个消息，以后这里的功能都要改成listener，不用直接写接口了。解耦更好一些。
                ctx.publishEvent(new LoginEvent(authentication, user, this
                        .getSessionId(authentication)));
            }

            if (event instanceof AuthenticationFailureBadCredentialsEvent) {
                AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent = (AuthenticationFailureBadCredentialsEvent) event;
                Authentication authentication = authenticationFailureBadCredentialsEvent
                        .getAuthentication();
                logger.info("{}", authentication);

                Object principal = authentication.getPrincipal();
                String user = null;

                if (principal instanceof SpringSecurityUserAuth) {
                    user = ((SpringSecurityUserAuth) principal).getId();
                } else {
                    user = authentication.getName();
                }

                AuditDTO auditDto = new AuditDTO();
                auditDto.setUser(user);
                auditDto.setAuditTime(new Date());
                auditDto.setAction("login");
                auditDto.setResult("failure");
                auditDto.setApplication("lemon");
                auditDto.setClient(getUserIp(authentication));
                auditDto.setServer(InetAddress.getLocalHost().getHostAddress());
                auditDto.setDescription(authenticationFailureBadCredentialsEvent
                        .getException().getMessage());
                auditConnector.log(auditDto);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String getUserIp(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        Object details = authentication.getDetails();

        if (!(details instanceof WebAuthenticationDetails)) {
            return "";
        }

        WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;

        return webDetails.getRemoteAddress();
    }

    public String getSessionId(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        Object details = authentication.getDetails();

        if (!(details instanceof WebAuthenticationDetails)) {
            return "";
        }

        WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;

        return webDetails.getSessionId();
    }

    @Resource
    public void setAuditConnector(AuditConnector auditConnector) {
        this.auditConnector = auditConnector;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }
}
