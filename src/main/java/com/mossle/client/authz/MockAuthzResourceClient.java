package com.mossle.client.authz;

import java.util.Collections;
import java.util.List;

import com.mossle.api.userauth.ResourceDTO;

public class MockAuthzResourceClient implements AuthzResourceClient {
    public List<ResourceDTO> findResource(String sysCode) {
        return Collections.emptyList();
    }

    public List<ResourceDTO> findResourceByType(String type, String sysCode) {
        return Collections.emptyList();
    }
}
