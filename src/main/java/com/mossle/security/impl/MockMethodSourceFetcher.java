package com.mossle.security.impl;

import java.util.Collections;
import java.util.Map;

import com.mossle.security.api.MethodSourceFetcher;

public class MockMethodSourceFetcher implements MethodSourceFetcher {
    public Map<String, String> getSource(String type) {
        return Collections.emptyMap();
    }
}
