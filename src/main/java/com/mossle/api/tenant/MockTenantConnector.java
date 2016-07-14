package com.mossle.api.tenant;

import java.util.Collections;
import java.util.List;

public class MockTenantConnector implements TenantConnector {
    private TenantDTO tenantDto;

    public MockTenantConnector() {
        tenantDto = new TenantDTO();
        tenantDto.setId("1");
        tenantDto.setRef("1");
        tenantDto.setCode("default");
        tenantDto.setUserRepoRef("1");
    }

    public TenantDTO findById(String id) {
        return tenantDto;
    }

    public TenantDTO findByRef(String ref) {
        return tenantDto;
    }

    public TenantDTO findByCode(String code) {
        return tenantDto;
    }

    public List<TenantDTO> findAll() {
        return Collections.singletonList(tenantDto);
    }

    public List<TenantDTO> findSharedTenants() {
        return Collections.singletonList(tenantDto);
    }
}
