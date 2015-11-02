package com.mossle.audit.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.api.audit.AuditDTO;

import com.mossle.audit.service.AuditService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class AuditConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AuditConsumer.class);
    public static final int DEFAULT_BATCH_SIZE = 64;
    private AuditQueue auditQueue;
    private AuditService auditService;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private boolean active = true;
    private Thread thread;

    @PostConstruct
    public void init() {
        thread = new Thread(this);
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
        List<AuditDTO> auditDtos = new ArrayList<AuditDTO>();

        try {
            int size = 0;

            while (size < batchSize) {
                AuditDTO auditDto = auditQueue.poll();

                if (auditDto == null) {
                    break;
                }

                auditDtos.add(auditDto);
                size++;
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
        }

        if (!auditDtos.isEmpty()) {
            auditService.batchLog(auditDtos);
        }
    }

    @Resource
    public void setAuditQueue(AuditQueue auditQueue) {
        this.auditQueue = auditQueue;
    }

    @Resource
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
