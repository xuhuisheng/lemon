package com.mossle.user.component;

import java.net.InetAddress;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.user.AccountLogDTO;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.auth.LoginEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationListener;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import org.springframework.stereotype.Component;

@Component
public class AccountLogListener implements ApplicationListener<LoginEvent> {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLogListener.class);
    private AccountLogQueue accountLogQueue;

    public void onApplicationEvent(LoginEvent loginEvent) {
        logger.debug("login : {}", loginEvent);

        Authentication authentication = (Authentication) loginEvent.getSource();
        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof UserAuthDTO) {
            username = ((UserAuthDTO) principal).getUsername();
        } else {
            username = authentication.getName();
        }

        AccountLogDTO accountLogDto = new AccountLogDTO();
        accountLogDto.setUsername(username);
        accountLogDto.setReason("login");
        accountLogDto.setResult(loginEvent.getResult());
        accountLogDto.setApplication("lemon");
        accountLogDto.setLogTime(new Date());
        accountLogDto.setClient(this.getUserIp(authentication));

        try {
            accountLogDto
                    .setServer(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        accountLogQueue.add(accountLogDto);
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

    @Resource
    public void setAccountLogQueue(AccountLogQueue accountLogQueue) {
        this.accountLogQueue = accountLogQueue;
    }
}
