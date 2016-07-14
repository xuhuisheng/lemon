package com.mossle.audit.support;

import javax.annotation.Resource;

import com.mossle.api.audit.AuditConnector;
import com.mossle.api.audit.AuditDTO;

import com.mossle.audit.persistence.domain.AuditBase;
import com.mossle.audit.service.AuditService;

import com.mossle.core.mapper.BeanMapper;

public class AuditConnectorImpl implements AuditConnector {
    private AuditService auditService;
    private BeanMapper beanMapper = new BeanMapper();

    public void log(AuditDTO auditDto) {
        AuditBase auditBase = new AuditBase();
        beanMapper.copy(auditDto, auditBase);
        auditService.log(auditBase);
    }

    @Resource
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }
}
