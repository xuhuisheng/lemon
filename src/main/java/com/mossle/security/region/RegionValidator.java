package com.mossle.security.region;

public interface RegionValidator {
    boolean validate(Object instance, String regionKeyText);
}
