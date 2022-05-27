package com.mossle.client.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestDistributedLock implements DistributedLock {
    private Lock lock = new ReentrantLock();

    public boolean tryLock(String lockId) {
        return lock.tryLock();
    }

    public void unlock(String lockId) {
        lock.unlock();
    }

    public boolean renewLock(String lockId) {
        return false;
    }
}
