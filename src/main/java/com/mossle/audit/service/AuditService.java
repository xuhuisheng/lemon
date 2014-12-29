package com.mossle.audit.service;

import javax.annotation.Resource;

import com.mossle.audit.domain.AuditBase;
import com.mossle.audit.manager.AuditBaseManager;

import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private AuditBaseManager auditBaseManager;

    public void log(AuditBase auditBase) {
        auditBaseManager.save(auditBase);
    }

    @Resource
    public void setAuditBaseManager(AuditBaseManager auditBaseManager) {
        this.auditBaseManager = auditBaseManager;
    }
}
