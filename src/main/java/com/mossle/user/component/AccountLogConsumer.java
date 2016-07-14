package com.mossle.user.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.api.user.AccountLogDTO;

import com.mossle.user.service.AccountLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class AccountLogConsumer implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLogConsumer.class);
    public static final int DEFAULT_BATCH_SIZE = 64;
    private AccountLogQueue accountLogQueue;
    private AccountLogService accountLogService;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private boolean active = true;
    private Thread thread;

    @PostConstruct
    public void init() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    public void close() {
        active = false;
    }

    public void run() {
        while (active) {
            execute();
        }
    }

    public void execute() {
        List<AccountLogDTO> accountLogDtos = new ArrayList<AccountLogDTO>();

        try {
            int size = 0;

            while (size < batchSize) {
                AccountLogDTO accountLogDto = accountLogQueue.poll();

                if (accountLogDto == null) {
                    break;
                }

                accountLogDtos.add(accountLogDto);
                size++;
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
        }

        if (!accountLogDtos.isEmpty()) {
            try {
                accountLogService.batchLog(accountLogDtos);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Resource
    public void setAccountLogQueue(AccountLogQueue accountLogQueue) {
        this.accountLogQueue = accountLogQueue;
    }

    @Resource
    public void setAccountLogService(AccountLogService accountLogService) {
        this.accountLogService = accountLogService;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
