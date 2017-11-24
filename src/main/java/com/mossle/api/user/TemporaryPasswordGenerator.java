package com.mossle.api.user;

public interface TemporaryPasswordGenerator {
    String generate(String userId, int minute);
}
