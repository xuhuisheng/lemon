package com.mossle.api.userrepo;

import java.util.List;

public interface UserRepoConnector {
    UserRepoDTO findById(String id);

    UserRepoDTO findByCode(String code);

    List<UserRepoDTO> findAll();
}
