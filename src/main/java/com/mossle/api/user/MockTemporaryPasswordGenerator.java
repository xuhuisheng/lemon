package com.mossle.api.user;

public class MockTemporaryPasswordGenerator implements
        TemporaryPasswordGenerator {
    public String generate(String userId, int minute) {
        return "111111";
    }
}
