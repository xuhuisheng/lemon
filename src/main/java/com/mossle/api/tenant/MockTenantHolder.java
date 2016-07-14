package com.mossle.api.tenant;

public class MockTenantHolder implements TenantHolder {
    private TenantDTO tenantDto = new TenantDTO();

    public MockTenantHolder() {
        tenantDto.setId("1");
        tenantDto.setCode("default");
        tenantDto.setUserRepoRef("1");
    }

    public String getTenantId() {
        return tenantDto.getId();
    }

    public String getTenantCode() {
        return tenantDto.getCode();
    }

    public String getUserRepoRef() {
        return tenantDto.getUserRepoRef();
    }

    public TenantDTO getTenantDto() {
        return tenantDto;
    }
}
