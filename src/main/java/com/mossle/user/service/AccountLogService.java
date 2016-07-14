package com.mossle.user.service;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.AccountAliasConverter;
import com.mossle.api.user.AccountLogDTO;
import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.ApplicationAliasConverter;
import com.mossle.api.user.MockAccountAliasConverter;
import com.mossle.api.user.MockApplicationAliasConverter;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.user.persistence.domain.AccountLog;
import com.mossle.user.persistence.manager.AccountLogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountLogService {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLogService.class);
    private BeanMapper beanMapper = new BeanMapper();
    private AccountLogManager accountLogManager;
    private AccountLockService accountLockService;
    private AccountAliasConverter accountAliasConverter = new MockAccountAliasConverter();
    private ApplicationAliasConverter applicationAliasConverter = new MockApplicationAliasConverter();

    /**
     * 批量记录认证日志.
     */
    public void batchLog(List<AccountLogDTO> accountLogDtos) {
        for (AccountLogDTO accountLogDto : accountLogDtos) {
            AccountLog accountLog = new AccountLog();
            beanMapper.copy(accountLogDto, accountLog);

            if (accountAliasConverter != null) {
                String username = accountLog.getUsername();
                String realUsername = accountAliasConverter
                        .convertAlias(username);
                accountLog.setUsername(realUsername);
            }

            String username = accountLog.getUsername();

            if (username.length() > 64) {
                logger.info("username : {}", username);
                username = username.substring(0, 64);
                accountLog.setUsername(username);
            }

            String reason = accountLog.getReason();

            if (reason.length() > 200) {
                logger.info("reason : {}", reason);
                reason = reason.substring(0, 200);
                accountLog.setReason(reason);
            }

            this.log(accountLog);
        }
    }

    /**
     * 记录一条日志.
     */
    public void log(AccountLog accountLog) {
        accountLogManager.save(accountLog);

        // if ("success".equals(accountLog.getResult())) {
        // accountLockService.unlock(accountLog.getUsername(),
        // accountLog.getApplication());
        // } else
        if (AccountStatus.BAD_CREDENTIALS.equals(accountLog.getReason())) {
            String application = accountLog.getApplication();

            if (applicationAliasConverter != null) {
                application = applicationAliasConverter.convertAlias(
                        application, accountLog.getClient());
            }

            accountLockService.addLockLog(accountLog.getUsername(),
                    application, accountLog.getLogTime());
        }
    }

    @Resource
    public void setAccountLogManager(AccountLogManager accountLogManager) {
        this.accountLogManager = accountLogManager;
    }

    @Resource
    public void setAccountLockService(AccountLockService accountLockService) {
        this.accountLockService = accountLockService;
    }

    @Autowired(required = false)
    public void setAccountAliasConverter(
            AccountAliasConverter accountAliasConverter) {
        this.accountAliasConverter = accountAliasConverter;
    }

    @Autowired(required = false)
    public void setApplicationAliasConverter(
            ApplicationAliasConverter applicationAliasConverter) {
        this.applicationAliasConverter = applicationAliasConverter;
    }
}
