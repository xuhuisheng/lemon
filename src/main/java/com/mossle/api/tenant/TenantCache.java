package com.mossle.api.tenant;

public interface TenantCache {
    TenantDTO findById(String id);

    TenantDTO findByRef(String ref);

    TenantDTO findByCode(String code);

    void updateTenant(TenantDTO tenantDto);

    void removeTenant(TenantDTO tenantDto);
}
