package com.mossle.api.tenant;

import java.util.List;

public interface TenantConnector {
    TenantDTO findById(String id);

    TenantDTO findByRef(String ref);

    TenantDTO findByCode(String code);

    List<TenantDTO> findAll();

    List<TenantDTO> findSharedTenants();
}
