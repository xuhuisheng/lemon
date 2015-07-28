package com.mossle.audit.component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mossle.api.audit.AuditDTO;

import org.springframework.stereotype.Component;

@Component
public class AuditQueue {
    private BlockingQueue<AuditDTO> blockingQueue = new LinkedBlockingQueue<AuditDTO>();

    public void add(AuditDTO auditDto) {
        blockingQueue.add(auditDto);
    }

    public AuditDTO poll() throws InterruptedException {
        return blockingQueue.poll(1, TimeUnit.SECONDS);
    }
}
