package com.mossle.client.authz;

import java.util.List;

import com.mossle.api.userauth.ResourceDTO;

public interface AuthzResourceClient {
    List<ResourceDTO> findResource(String sysCode);
}
