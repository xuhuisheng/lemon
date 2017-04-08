package com.mossle.api.tenant;

public class MockTenantCache implements TenantCache {
    public TenantDTO findById(String id) {
        return null;
    }

    public TenantDTO findByRef(String ref) {
        return null;
    }

    public TenantDTO findByCode(String code) {
        return null;
    }

    public void updateTenant(TenantDTO tenantDto) {
    }

    public void removeTenant(TenantDTO tenantDto) {
    }
}
