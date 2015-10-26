package com.mossle.spi.userauth;

import javax.servlet.http.HttpSession;

import com.mossle.api.userauth.UserAuthDTO;

public class MockInternalUserAuthConnector implements InternalUserAuthConnector {
    public UserAuthDTO findFromSession(HttpSession session) {
        return null;
    }
}
