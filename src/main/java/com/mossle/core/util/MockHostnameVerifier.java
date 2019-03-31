package com.mossle.core.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MockHostnameVerifier implements HostnameVerifier {
    public boolean verify(String urlHostName, SSLSession session) {
        return true;
    }
}
