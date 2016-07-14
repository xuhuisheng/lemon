package com.mossle.security.util;

import java.net.InetAddress;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.audit.AuditConnector;
import com.mossle.api.audit.AuditDTO;

import com.mossle.core.auth.LoginEvent;

import com.mossle.security.impl.SpringSecurityUserAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class SpringSecurityListener implements ApplicationListener,
        ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(SpringSecurityListener.class);
    private AuditConnector auditConnector;
    private ApplicationContext ctx;

    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (event instanceof InteractiveAuthenticationSuccessEvent) {
                this.logLoginSuccess(event);
            }

            if (event instanceof AuthenticationFailureBadCredentialsEvent) {
                this.logBadCredential(event);
            }

            if (event instanceof AuthenticationFailureLockedEvent) {
                this.logLocked(event);
            }

            if (event instanceof AuthenticationFailureDisabledEvent) {
                this.logDisabled(event);
            }

            if (event instanceof AuthenticationFailureExpiredEvent) {
                this.logAccountExpired(event);
            }

            if (event instanceof AuthenticationFailureCredentialsExpiredEvent) {
                this.logCredentialExpired(event);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void logLoginSuccess(ApplicationEvent event) throws Exception {
        InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent = (InteractiveAuthenticationSuccessEvent) event;
        Authentication authentication = interactiveAuthenticationSuccessEvent
                .getAuthentication();

        String tenantId = this.getTenantId(authentication);
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
        auditDto.setTenantId(tenantId);
        auditConnector.log(auditDto);

        // 登录成功，再发送一个消息，以后这里的功能都要改成listener，不用直接写接口了。解耦更好一些。
        ctx.publishEvent(new LoginEvent(authentication, user, this
                .getSessionId(authentication), "success", "default", tenantId));
    }

    public void logBadCredential(ApplicationEvent event) throws Exception {
        AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent = (AuthenticationFailureBadCredentialsEvent) event;
        Authentication authentication = authenticationFailureBadCredentialsEvent
                .getAuthentication();
        logger.info("{}", authentication);

        String tenantId = this.getTenantId(authentication);
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
        auditDto.setTenantId(tenantId);
        auditConnector.log(auditDto);

        ctx.publishEvent(new LoginEvent(authentication, user, this
                .getSessionId(authentication), "badCredentials", "default",
                tenantId));
    }

    public void logLocked(ApplicationEvent event) throws Exception {
        AuthenticationFailureLockedEvent authenticationFailureLockedEvent = (AuthenticationFailureLockedEvent) event;
        Authentication authentication = authenticationFailureLockedEvent
                .getAuthentication();
        logger.info("{}", authentication);

        String tenantId = this.getTenantId(authentication);

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
        auditDto.setDescription(authenticationFailureLockedEvent.getException()
                .getMessage());
        auditDto.setTenantId(tenantId);
        auditConnector.log(auditDto);

        ctx.publishEvent(new LoginEvent(authentication, user, this
                .getSessionId(authentication), "locked", "default", tenantId));
    }

    public void logDisabled(ApplicationEvent event) throws Exception {
        AuthenticationFailureDisabledEvent authenticationFailureDisabledEvent = (AuthenticationFailureDisabledEvent) event;
        Authentication authentication = authenticationFailureDisabledEvent
                .getAuthentication();
        logger.info("{}", authentication);

        String tenantId = this.getTenantId(authentication);

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
        auditDto.setDescription(authenticationFailureDisabledEvent
                .getException().getMessage());
        auditDto.setTenantId(tenantId);
        auditConnector.log(auditDto);

        ctx.publishEvent(new LoginEvent(authentication, user, this
                .getSessionId(authentication), "disabled", "default", tenantId));
    }

    public void logCredentialExpired(ApplicationEvent event) throws Exception {
        AuthenticationFailureCredentialsExpiredEvent authenticationFailureCredentialsExpiredEvent = (AuthenticationFailureCredentialsExpiredEvent) event;
        Authentication authentication = authenticationFailureCredentialsExpiredEvent
                .getAuthentication();
        logger.info("{}", authentication);

        String tenantId = this.getTenantId(authentication);

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
        auditDto.setDescription(authenticationFailureCredentialsExpiredEvent
                .getException().getMessage());
        auditDto.setTenantId(tenantId);
        auditConnector.log(auditDto);

        ctx.publishEvent(new LoginEvent(authentication, user, this
                .getSessionId(authentication), "credentialExpired", "default",
                tenantId));
    }

    public void logAccountExpired(ApplicationEvent event) throws Exception {
        AuthenticationFailureExpiredEvent authenticationFailureExpiredEvent = (AuthenticationFailureExpiredEvent) event;
        Authentication authentication = authenticationFailureExpiredEvent
                .getAuthentication();
        logger.info("{}", authentication);

        String tenantId = this.getTenantId(authentication);

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
        auditDto.setDescription(authenticationFailureExpiredEvent
                .getException().getMessage());
        auditDto.setTenantId(tenantId);
        auditConnector.log(auditDto);

        ctx.publishEvent(new LoginEvent(authentication, user, this
                .getSessionId(authentication), "accountExpired", "default",
                tenantId));
    }

    // ~
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

    public String getTenantId(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof SpringSecurityUserAuth) {
            return ((SpringSecurityUserAuth) principal).getTenantId();
        } else {
            return "";
        }
    }

    @Resource
    public void setAuditConnector(AuditConnector auditConnector) {
        this.auditConnector = auditConnector;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }
}
