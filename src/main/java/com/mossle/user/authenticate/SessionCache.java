package com.mossle.user.authenticate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionCache {
    private Map<String, List<String>> sessionMap = new HashMap<String, List<String>>();

    public void updateSession(String username, String sessionId) {
        List<String> sessionIds = sessionMap.get(username);

        if (sessionIds == null) {
            sessionIds = new ArrayList<String>();
            sessionMap.put(username, sessionIds);
        }

        sessionIds.add(sessionId);
    }

    public void kickSessionsByUsername(String username) {
        List<String> sessionIds = sessionMap.get(username);

        if (sessionIds == null) {
            return;
        }

        for (String sessionId : sessionIds) {
            this.invalidateSession(sessionId);
        }

        this.sessionMap.remove(username);
    }

    public void invalidateSession(String sessionId) {
        // TODO: invalidate session
    }

    private class Item {
        String sessionId;
        long updateTime;
    }
}
