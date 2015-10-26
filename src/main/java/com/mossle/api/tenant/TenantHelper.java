package com.mossle.api.tenant;

public class TenantHelper {
    private static ThreadLocal<TenantDTO> tenantThreadLocal = new ThreadLocal<TenantDTO>();

    protected TenantHelper() {
    }

    public static String getTenantId() {
        return getTenantDto().getId();
    }

    public static String getTenantCode() {
        return getTenantDto().getCode();
    }

    public static String getUserRepoRef() {
        return getTenantDto().getUserRepoRef();
    }

    public static TenantDTO getTenantDto() {
        TenantDTO tenantDto = tenantThreadLocal.get();

        if (tenantDto == null) {
            throw new IllegalStateException("cannot find tenant");
        }

        return tenantDto;
    }

    public static void setTenantDto(TenantDTO tenantDto) {
        tenantThreadLocal.set(tenantDto);
    }

    public static void clear() {
        tenantThreadLocal.remove();
    }
}
