package com.mossle.api.user;

import java.util.Map;

import com.mossle.core.page.Page;

public interface UserSyncConnector {
    void updateUser(UserDTO userDto);
}
