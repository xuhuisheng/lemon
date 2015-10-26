package com.mossle.audit.service;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.audit.AuditDTO;

import com.mossle.audit.persistence.domain.AuditBase;
import com.mossle.audit.persistence.manager.AuditBaseManager;

import com.mossle.core.mapper.BeanMapper;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditService {
    private BeanMapper beanMapper = new BeanMapper();
    private AuditBaseManager auditBaseManager;

    public void log(AuditBase auditBase) {
        auditBaseManager.save(auditBase);
    }

    public void batchLog(List<AuditDTO> auditDtos) {
        for (AuditDTO auditDto : auditDtos) {
            AuditBase auditBase = new AuditBase();
            beanMapper.copy(auditDto, auditBase);

            auditBaseManager.save(auditBase);
        }
    }

    @Resource
    public void setAuditBaseManager(AuditBaseManager auditBaseManager) {
        this.auditBaseManager = auditBaseManager;
    }
}
