package com.mossle.api.user;

import java.net.InetAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class AccountLogClient {
    private ExecutorService executorService;
    private String server;
    private String url;

    @PostConstruct
    public void init() {
        try {
            server = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(10);
    }

    @PreDestroy
    public void close() {
        executorService.shutdown();
    }

    public void log(String application, String result, String reason,
            String username, String client, String description) {
        try {
            AccountLogWorker accountLogWorker = new AccountLogWorker();
            accountLogWorker.setUrl(url);
            accountLogWorker.setApplication(application);
            accountLogWorker.setUsername(username);
            accountLogWorker.setClient(client);
            accountLogWorker.setServer(server);
            accountLogWorker.setResult(result);
            accountLogWorker.setReason(reason);
            accountLogWorker.setDescription(description);
            executorService.execute(accountLogWorker);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void log(String result, String reason) {
        try {
            AccountLogDTO accountLogDto = AccountLogHolder.getAccountLogDto();
            this.log(accountLogDto.getApplication(), result, reason,
                    accountLogDto.getUsername(), accountLogDto.getClient(),
                    accountLogDto.getDescription());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
