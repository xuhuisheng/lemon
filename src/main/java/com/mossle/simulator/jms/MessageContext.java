package com.mossle.simulator.jms;

import java.util.HashMap;
import java.util.Map;

public class MessageContext {
    private Map<String, Object> attributes = new HashMap<String, Object>();

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return this.attributes.containsKey(key);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public void clear() {
        this.attributes.clear();
    }
}
