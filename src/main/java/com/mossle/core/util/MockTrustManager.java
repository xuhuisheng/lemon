package com.mossle.core.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MockTrustManager implements TrustManager, X509TrustManager {
    public X509Certificate[] getAcceptedIssuers() {
        // skip
        return null;
    }

    public boolean isServerTrusted(X509Certificate[] certs) {
        // skip
        return true;
    }

    public boolean isClientTrusted(X509Certificate[] certs) {
        // skip
        return true;
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType)
            throws CertificateException {
        // skip
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType)
            throws CertificateException {
        // skip
    }
}
