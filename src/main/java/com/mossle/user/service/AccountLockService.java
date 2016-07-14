package com.mossle.user.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.domain.AccountLockLog;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.AccountLockInfoManager;
import com.mossle.user.persistence.manager.AccountLockLogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

// 5分钟内连续输入5次错误密码就锁定
// 10分钟后自动解锁
@Service
@Transactional
public class AccountLockService {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLockService.class);
    public static final int DEFAULT_THRESHOLD = 5;
    public static final int FIVE_MINUTES = 5;
    public static final int TEN_MINUTES = 10;
    private int threshold = DEFAULT_THRESHOLD;
    private AccountInfoManager accountInfoManager;
    private AccountLockInfoManager accountLockInfoManager;
    private AccountLockLogManager accountLockLogManager;

    public void addLockLog(String username, String application, Date logDate) {
        AccountLockLog accountLockLog = new AccountLockLog();
        accountLockLog.setType(application);
        accountLockLog.setLockTime(logDate);

        accountLockLog.setUsername(username);
        accountLockLogManager.save(accountLockLog);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1 * FIVE_MINUTES);

        String logHql = "from AccountLockLog where type=? and username=? and lockTime<?";
        List<AccountLockLog> accountLockLogs = accountLockLogManager.find(
                logHql, application, username, calendar.getTime());

        if (accountLockLogs.size() > threshold) {
            calendar.setTime(logDate);
            calendar.add(Calendar.MINUTE, FIVE_MINUTES);

            AccountLockInfo accountLockInfo = new AccountLockInfo();
            accountLockInfo.setType(application);
            accountLockInfo.setLockTime(logDate);
            accountLockInfo.setReleaseTime(calendar.getTime());
            accountLockInfo.setUsername(username);
            accountLockInfoManager.save(accountLockInfo);
        }
    }

    public void unlock(String username, String application) {
        String logHql = "from AccountLockLog where type=? and username=?";
        List<AccountLockLog> accountLockLogs = accountLockLogManager.find(
                logHql, application, username);
        accountLockLogManager.removeAll(accountLockLogs);

        String infoHql = "from AccountLockInfo where type=? and username=?";
        List<AccountLockInfo> accountLockInfos = accountLockInfoManager.find(
                infoHql, application, username);
        accountLockInfoManager.removeAll(accountLockInfos);
    }

    public void doUnlock() throws Exception {
        logger.debug("do unlock start");

        String hqlAccountLockInfo = "from AccountLockInfo where releaseTime<?";
        List<AccountLockInfo> accountLockInfos = accountLockInfoManager.find(
                hqlAccountLockInfo, new Date());

        for (AccountLockInfo accountLockInfo : accountLockInfos) {
            logger.info("unlock info : {}", accountLockInfo.getUsername());
            accountLockInfoManager.remove(accountLockInfo);

            String hqlAccountLockLog = "from AccountLockLog where type=? and username=?";

            List<AccountLockLog> accountLockLogs = accountLockLogManager.find(
                    hqlAccountLockLog, accountLockInfo.getType(),
                    accountLockInfo.getUsername());
            accountLockLogManager.removeAll(accountLockLogs);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1 * FIVE_MINUTES);

        String hqlAccountLockLog = "from AccountLockLog where lockTime<?";
        List<AccountLockLog> accountLockLogs = accountLockLogManager.find(
                hqlAccountLockLog, calendar.getTime());

        for (AccountLockLog accountLockLog : accountLockLogs) {
            logger.info("unlock log : {}", accountLockLog.getUsername());
            accountLockLogManager.remove(accountLockLog);
        }

        logger.debug("do unlock end");
    }

    public void doClean() throws Exception {
        logger.info("do clean start");

        List<AccountLockInfo> accountLockInfos = accountLockInfoManager
                .getAll();

        for (AccountLockInfo accountLockInfo : accountLockInfos) {
            logger.info("unlock : {}", accountLockInfo.getUsername());
            accountLockInfoManager.remove(accountLockInfo);
        }

        List<AccountLockLog> accountLockLogs = accountLockLogManager.getAll();
        accountLockLogManager.removeAll(accountLockLogs);
        logger.info("do clean end");
    }

    @Resource
    public void setAccountLockInfoManager(
            AccountLockInfoManager accountLockInfoManager) {
        this.accountLockInfoManager = accountLockInfoManager;
    }

    @Resource
    public void setAccountLockLogManager(
            AccountLockLogManager accountLockLogManager) {
        this.accountLockLogManager = accountLockLogManager;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }
}
