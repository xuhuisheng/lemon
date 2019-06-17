package com.mossle.client.authz;

import com.mossle.api.userauth.UserAuthDTO;

public interface AuthzClient {
    UserAuthDTO findByUsername(String username, String tenantId);

    UserAuthDTO findById(String id, String tenantId);
}
