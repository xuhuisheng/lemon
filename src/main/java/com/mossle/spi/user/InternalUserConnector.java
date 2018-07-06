package com.mossle.spi.user;

import java.io.InputStream;

import com.mossle.api.avatar.AvatarDTO;

import com.mossle.spi.device.DeviceDTO;

public interface InternalUserConnector {
    /**
     * 根据username或email或mobile获取username.
     */
    String findUsernameByAlias(String alias);

    /**
     * 根据username查询默认的加密后的密码.
     */
    String findPassword(String username, String tenantId);

    /**
     * 根据设备编码查询.
     */
    DeviceDTO findDevice(String code);

    /**
     * 保存设备信息.
     */
    void saveDevice(DeviceDTO deviceDto);

    /**
     * 根据userId获得AvatarDTO.
     */
    AvatarDTO findAvatar(String userId);

    /** 保存. */
    void saveAvatar(String userId, String code);

    /**
     * 根据userId获取inputStream.
     */
    InputStream findAvatarInputStream(String userId);

    /**
     * 根据userId获取base64.
     */
    String findAvatarBase64(String userId);

    /**
     * 账号是否被锁定.
     */
    boolean isLocked(String username, String application);

    /**
     * 获取账号状态.
     */
    String getAccountStatus(String username, String application);
}
