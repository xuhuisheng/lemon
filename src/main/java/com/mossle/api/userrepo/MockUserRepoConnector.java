package com.mossle.api.userrepo;

import java.util.List;

public class MockUserRepoConnector implements UserRepoConnector {
    public UserRepoDTO findById(String id) {
        return null;
    }

    public UserRepoDTO findByCode(String code) {
        return null;
    }

    public List<UserRepoDTO> findAll() {
        return null;
    }
}
