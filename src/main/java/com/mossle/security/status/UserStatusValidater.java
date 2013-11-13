package com.mossle.security.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserStatusValidater {
    public static final int STATUS_ENABLE = 0;
    public static final int STATUS_ACCOUNT_EXPIRED = 1;
    public static final int STATUS_ACCOUNT_LOCKED = 1 << 1;
    public static final int STATUS_CREDENTAIL_EXPIRED = 1 << 2;
    private Logger logger = LoggerFactory.getLogger(UserStatusValidater.class);

    public void validate(String username, int status) {
        if (status == STATUS_ENABLE) {
            return;
        } else if (this.isMatch(status, STATUS_ACCOUNT_EXPIRED)) {
            throw new AccountExpiredException(username + " is expired.");
        } else if (this.isMatch(status, STATUS_ACCOUNT_LOCKED)) {
            throw new AccountLockedException(username + " is locked.");
        } else if (this.isMatch(status, STATUS_CREDENTAIL_EXPIRED)) {
            throw new CredentialExpiredException(username
                    + "'s credential is expired.");
        } else {
            logger.warn("unkown status : {}", status);
            throw new UserStatusException(username + "'s status is invalid.");
        }
    }

    private boolean isMatch(int status, int mask) {
        return (status & mask) != 0;
    }
}
