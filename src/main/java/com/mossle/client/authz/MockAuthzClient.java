package com.mossle.client.authz;

import com.mossle.api.userauth.UserAuthDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockAuthzClient implements AuthzClient {
    private static Logger logger = LoggerFactory
            .getLogger(MockAuthzClient.class);

    public UserAuthDTO findByUsername(String username, String tenantId) {
        logger.info("findByUsername : {}", username);

        return null;
    }

    public UserAuthDTO findById(String id, String tenantId) {
        logger.info("findById : {}", id);

        return null;
    }
}
