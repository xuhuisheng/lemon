package com.mossle.client.user;

public class MockAccountStatusClient implements AccountStatusClient {
    public boolean findEnabled(String userId) {
        return true;
    }

    public boolean findCredentialsExpired(String userId) {
        return false;
    }

    public boolean findAccountLocked(String userId) {
        return false;
    }

    public boolean findAccountExpired(String userId) {
        return false;
    }
}
