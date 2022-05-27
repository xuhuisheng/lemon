package com.mossle.client.lock;

public interface DistributedLock {
    boolean tryLock(String lockId);

    void unlock(String lockId);

    boolean renewLock(String lockId);
}
