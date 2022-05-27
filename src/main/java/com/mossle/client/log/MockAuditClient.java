package com.mossle.client.log;

import com.mossle.api.audit.AuditDTO;

public class MockAuditClient implements AuditClient {
    public void log(AuditDTO auditDto) {
    }
}
