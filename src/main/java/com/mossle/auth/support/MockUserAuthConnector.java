package com.mossle.auth.support;

import java.util.Collections;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.user.UserConnector;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class MockUserAuthConnector implements UserAuthConnector {
    private static Logger logger = LoggerFactory
            .getLogger(MockUserAuthConnector.class);
    private JdbcTemplate jdbcTemplate;
    private TenantConnector tenantConnector;
    private UserConnector userConnector;
    private UserAuthDTO userAuthDto;

    public MockUserAuthConnector() {
        userAuthDto = new UserAuthDTO();
        userAuthDto.setId("1");
        userAuthDto.setTenantId("1");
        userAuthDto.setUsername("lingo");
        userAuthDto.setRef("1");
        userAuthDto.setDisplayName("lingo");
        userAuthDto.setStatus("1");

        userAuthDto.setPermissions(Collections.singletonList("*"));
        userAuthDto.setRoles(Collections.singletonList("ROLE_ADMIN"));
    }

    public UserAuthDTO findByUsername(String username, String tenantId) {
        return userAuthDto;
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        return userAuthDto;
    }

    public UserAuthDTO findById(String id, String tenantId) {
        return userAuthDto;
    }
}
