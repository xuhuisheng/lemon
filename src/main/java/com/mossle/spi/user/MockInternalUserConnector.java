package com.mossle.spi.user;

import java.io.InputStream;

import com.mossle.api.avatar.AvatarDTO;
import com.mossle.api.user.AccountStatus;

import com.mossle.spi.device.DeviceDTO;

public class MockInternalUserConnector implements InternalUserConnector {
    // 别名
    /**
     * 根据username或email或mobile获取username.
     */
    public String findUsernameByAlias(String alias) {
        return alias;
    }

    // 密码
    /**
     * 根据username查询默认的加密后的密码.
     */
    public String findPassword(String username, String tenantId) {
        return username;
    }

    // 设备
    public DeviceDTO findDevice(String code) {
        return null;
    }

    public void saveDevice(DeviceDTO deviceDto) {
    }

    // 头像
    public AvatarDTO findAvatar(String userId) {
        return null;
    }

    public void saveAvatar(String userId, String code) {
    }

    public InputStream findAvatarInputStream(String userId) {
        return null;
    }

    public String findAvatarBase64(String userId) {
        return null;
    }

    // 账号状态
    public boolean isLocked(String username, String application) {
        return false;
    }

    public String getAccountStatus(String username, String application) {
        return AccountStatus.ENABLED;
    }
}
