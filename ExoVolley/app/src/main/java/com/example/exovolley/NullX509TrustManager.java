package com.example.exovolley;

import android.annotation.SuppressLint;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Trust manager that does not perform any checks.
 */
public class NullX509TrustManager implements X509TrustManager {
    /**
     * Does nothing.
     *
     * @param chain
     *            certificate chain
     * @param authType
     *            authentication type
     */
    @SuppressLint("TrustAllX509TrustManager")
    @Override
    public void checkClientTrusted(final X509Certificate[] chain,
                                   final String authType) {
        // Does nothing
    }

    /**
     * Does nothing.
     *
     * @param chain
     *            certificate chain
     * @param authType
     *            authentication type
     */
    @SuppressLint("TrustAllX509TrustManager")
    @Override
    public void checkServerTrusted(final X509Certificate[] chain,
                                   final String authType) {
        // Does nothing
    }

    /**
     * Gets a list of accepted issuers.
     *
     * @return empty array
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
        // Does nothing
    }
}
