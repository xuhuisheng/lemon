package com.mossle.api.audit;

public class AuditHolder {
    private static ThreadLocal<AuditDTO> threadLocal = new ThreadLocal<AuditDTO>();

    public static void init(String username, String clientIp, String description) {
        AuditDTO auditDto = new AuditDTO();
        auditDto.setUser(username);
        auditDto.setClient(clientIp);
        auditDto.setDescription(description);
        threadLocal.set(auditDto);
    }

    public static AuditDTO getAuditDto() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
