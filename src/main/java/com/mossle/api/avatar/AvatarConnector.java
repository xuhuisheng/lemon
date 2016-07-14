package com.mossle.api.avatar;

import java.io.InputStream;

/**
 * 头像.
 */
public interface AvatarConnector {
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
}
