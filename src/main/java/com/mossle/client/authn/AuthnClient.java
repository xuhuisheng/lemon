package com.mossle.client.authn;

import com.mossle.core.util.BaseDTO;

import com.mossle.spi.device.DeviceDTO;

public interface AuthnClient {
    /**
     * 账号密码认证.
     */
    String authenticate(String username, String password, String tenantId);

    /**
     * 根据设备编码查询.
     */
    DeviceDTO findDevice(String code);

    /**
     * 保存设备信息.
     */
    void saveDevice(DeviceDTO deviceDto);
}
