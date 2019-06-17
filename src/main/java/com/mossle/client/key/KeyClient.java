package com.mossle.client.key;

import com.mossle.core.util.BaseDTO;

public interface KeyClient {
    BaseDTO encrypt(String text);

    BaseDTO decrypt(String text);
}
