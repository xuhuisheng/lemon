package com.mossle.security.status;

public class AccountLockedException extends UserStatusException {
    public AccountLockedException(String message) {
        super(message);
    }
}
