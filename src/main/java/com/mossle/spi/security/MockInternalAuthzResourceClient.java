package com.mossle.spi.security;

import java.util.Collections;
import java.util.List;

import com.mossle.api.userauth.ResourceDTO;

public class MockInternalAuthzResourceClient implements
        InternalAuthzResourceClient {
    public List<ResourceDTO> findResourceByType(String type, String sysCode) {
        return Collections.emptyList();
    }
}
