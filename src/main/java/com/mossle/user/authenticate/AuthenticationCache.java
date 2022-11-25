package com.mossle.user.authenticate;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationCache {
    // 10 minutes
    private long expireTime = 10 * 60 * 1000;
    private Map<String, Item> credentialsInvalidMap = new HashMap<String, Item>();

    public int getCredentialsInvalidCount(String username) {
        Item item = credentialsInvalidMap.get(username);

        if (item == null) {
            return 0;
        }

        if ((System.currentTimeMillis() - item.updateTime) > expireTime) {
            credentialsInvalidMap.remove(username);

            return 0;
        }

        return item.count;
    }

    public void addCredentialsInvalidCount(String username) {
        Item item = credentialsInvalidMap.get(username);

        if (item == null) {
            item = new Item();
            item.count = 1;
            item.updateTime = System.currentTimeMillis();
            credentialsInvalidMap.put(username, item);

            return;
        }

        if ((System.currentTimeMillis() - item.updateTime) > expireTime) {
            item.count = 1;
            item.updateTime = System.currentTimeMillis();

            return;
        }

        item.count += 1;
        item.updateTime = System.currentTimeMillis();
    }

    public void clearCredentialsInvalidCount(String username) {
        credentialsInvalidMap.remove(username);
    }

    private class Item {
        int count;
        long updateTime;
    }
}
