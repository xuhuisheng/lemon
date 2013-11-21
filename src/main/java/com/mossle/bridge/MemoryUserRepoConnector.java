package com.mossle.bridge;

import java.util.Collections;
import java.util.List;

import com.mossle.api.UserRepoConnector;
import com.mossle.api.UserRepoDTO;

public class MemoryUserRepoConnector implements UserRepoConnector {
    public UserRepoDTO findById(String id) {
        return null;
    }

    public UserRepoDTO findByCode(String code) {
        return null;
    }

    public List<UserRepoDTO> findAll() {
        return Collections.EMPTY_LIST;
    }
}
