package com.mossle.api.audit;

import java.net.InetAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuditClient {
    private ExecutorService executorService;
    private String serverIp;
    private String app = "vpn";

    public void init() {
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(10);
    }

    public void close() {
        executorService.shutdown();
    }

    public void log(String result, String username, String clientIp,
            String description) {
        AuditWorker auditWorker = new AuditWorker();
        auditWorker.setApp(app);
        auditWorker.setUsername(username);
        auditWorker.setClientIp(clientIp);
        auditWorker.setServerIp(serverIp);
        auditWorker.setResult(result);
        auditWorker.setDescription(description);
        executorService.execute(auditWorker);
    }

    public void log(String result) {
        try {
            AuditDTO auditDto = AuditHolder.getAuditDto();
            AuditWorker auditWorker = new AuditWorker();
            auditWorker.setApp(app);
            auditWorker.setUsername(auditDto.getUser());
            auditWorker.setClientIp(auditDto.getClient());
            auditWorker.setServerIp(serverIp);
            auditWorker.setResult(result);
            auditWorker.setDescription(auditDto.getDescription());
            executorService.execute(auditWorker);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
