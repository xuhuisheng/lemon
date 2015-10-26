package com.mossle.api.user;

import java.io.InputStream;

public class MockUserAvatarConnector implements UserAvatarConnector {
    public InputStream findAvatarInputStream(String userId) {
        return null;
    }

    public String findAvatarBase64(String userId) {
        return null;
    }
}
