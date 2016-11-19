package com.mossle.user.scheduler;

import javax.annotation.Resource;

import com.mossle.user.service.AccountLockService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

@Component
public class AccountLockJob {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLockJob.class);
    private boolean running;
    private boolean enabled = true;
    private AccountLockService accountLockService;

    // every 1 minute
    @Scheduled(cron = "0 * * * * ?")
    public void unlockFiveMinute() {
        if (!enabled) {
            return;
        }

        try {
            accountLockService.doUnlock();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    // every 1 day
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanEveryNight() {
        if (!enabled) {
            return;
        }

        try {
            accountLockService.doClean();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setAccountLockService(AccountLockService accountLockService) {
        this.accountLockService = accountLockService;
    }

    @Value("${account.unlock.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
