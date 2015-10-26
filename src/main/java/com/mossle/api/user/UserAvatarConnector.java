package com.mossle.api.user;

import java.io.InputStream;

public interface UserAvatarConnector {
    InputStream findAvatarInputStream(String userId);

    String findAvatarBase64(String userId);
}
