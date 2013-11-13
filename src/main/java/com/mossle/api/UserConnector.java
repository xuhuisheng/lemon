package com.mossle.api;

import java.util.Map;

import com.mossle.core.page.Page;

public interface UserConnector {
    UserDTO findById(Object id);

    UserDTO findByUsername(String username, Long globalId);

    Page pagedQuery(Page page, Map<String, Object> parameters);
}
