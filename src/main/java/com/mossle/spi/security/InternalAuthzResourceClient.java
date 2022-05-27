package com.mossle.spi.security;

import java.util.List;

import com.mossle.api.userauth.ResourceDTO;

public interface InternalAuthzResourceClient {
    List<ResourceDTO> findResourceByType(String type, String sysCode);
}
