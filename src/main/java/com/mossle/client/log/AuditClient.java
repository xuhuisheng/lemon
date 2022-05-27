package com.mossle.client.log;

import com.mossle.api.audit.AuditDTO;

public interface AuditClient {
    void log(AuditDTO auditDto);
}
