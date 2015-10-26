package com.mossle.api.tenant;

public class DefaultTenantHolder implements TenantHolder {
    public String getTenantId() {
        return this.getTenantDto().getId();
    }

    public String getTenantCode() {
        return this.getTenantDto().getCode();
    }

    public String getUserRepoRef() {
        return this.getTenantDto().getUserRepoRef();
    }

    public TenantDTO getTenantDto() {
        return TenantHelper.getTenantDto();
    }
}
