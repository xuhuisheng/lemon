package com.mossle.core.util;

import java.net.URL;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SslUtils {
    public static void checkAndIgnoreHttps(String url) {
        try {
            checkAndIgnoreHttps(new URL(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void checkAndIgnoreHttps(URL url) {
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            try {
                ignoreSsl();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        trustAllCerts[0] = new MockTrustManager();

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static void ignoreSsl() throws Exception {
        System.setProperty("jsse.enableSNIExtension", "false");
        trustAllHttpsCertificates();
        HttpsURLConnection
                .setDefaultHostnameVerifier(new MockHostnameVerifier());
    }
}
