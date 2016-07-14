package com.mossle.user.component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mossle.api.user.AccountLogDTO;

import org.springframework.stereotype.Component;

@Component
public class AccountLogQueue {
    private BlockingQueue<AccountLogDTO> blockingQueue = new LinkedBlockingQueue<AccountLogDTO>();

    public void add(AccountLogDTO accountLogDto) {
        blockingQueue.add(accountLogDto);
    }

    public AccountLogDTO poll() throws InterruptedException {
        return blockingQueue.poll(1, TimeUnit.SECONDS);
    }
}
