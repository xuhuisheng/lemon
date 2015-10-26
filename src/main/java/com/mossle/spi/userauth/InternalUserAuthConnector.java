package com.mossle.spi.userauth;

import javax.servlet.http.HttpSession;

import com.mossle.api.userauth.UserAuthDTO;

public interface InternalUserAuthConnector {
    UserAuthDTO findFromSession(HttpSession session);
}
