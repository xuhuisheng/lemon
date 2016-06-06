package com.mossle.api.avatar;

import java.io.InputStream;

public class MockAvatarConnector implements AvatarConnector {
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
}
