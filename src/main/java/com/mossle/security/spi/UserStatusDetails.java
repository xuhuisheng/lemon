package com.mossle.security.spi;

import java.util.Collection;

public interface UserStatusDetails {
    Collection<String> getAttributes();

    Collection<String> getAttributes(String type);
}
