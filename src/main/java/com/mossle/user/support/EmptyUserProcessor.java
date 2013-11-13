package com.mossle.user.support;

import com.mossle.api.UserProcessor;

public class EmptyUserProcessor implements UserProcessor {
    public void insertUser(String id, String username) {
    }

    public void updateUser(String id, String username) {
    }

    public void removeUser(String id) {
    }
}
