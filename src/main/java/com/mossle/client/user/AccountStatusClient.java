package com.mossle.client.user;

public interface AccountStatusClient {
    boolean findEnabled(String userId);

    boolean findCredentialsExpired(String userId);

    boolean findAccountLocked(String userId);

    boolean findAccountExpired(String userId);
}
