package com.mossle.client.authn;

import com.mossle.core.util.BaseDTO;

public interface AuthnClient {
    BaseDTO authenticate(String username, String password, String tenantId);
}
