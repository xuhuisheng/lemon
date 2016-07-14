package com.mossle.api.audit;

public interface AuditConnector {
    void log(AuditDTO auditDto);
}
