/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http.sample.util;

import android.os.Build;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A class to authenticate a secured connection against a custom CA using a BKS store.
 *
 * @author Noor Dawod <github@fineswap.com>
 */
public class SecureSocketFactory extends SSLSocketFactory {

    private static final String LOG_TAG = "SecureSocketFactory";

    private final SSLContext sslCtx;
    private final X509Certificate[] acceptedIssuers;

    /**
     * Instantiate a new secured factory pertaining to the passed store. Be sure to initialize the
     * store with the password using {@link java.security.KeyStore#load(java.io.InputStream,
     * char[])} method.
     *
     * @param store The key store holding the certificate details
     * @param alias The alias of the certificate to use
     */
    public SecureSocketFactory(KeyStore store, String alias)
            throws
            CertificateException,
            NoSuchAlgorithmException,
            KeyManagementException,
            KeyStoreException,
            UnrecoverableKeyException {

        super(store);

        // Loading the CA certificate from store.
        final Certificate rootca = store.getCertificate(alias);

        // Turn it to X509 format.
        InputStream is = new ByteArrayInputStream(rootca.getEncoded());
        X509Certificate x509ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
        AsyncHttpClient.silentCloseInputStream(is);

        if (null == x509ca) {
            throw new CertificateException("Embedded SSL certificate has expired.");
        }

        // Check the CA's validity.
        x509ca.checkValidity();

        // Accepted CA is only the one installed in the store.
        acceptedIssuers = new X509Certificate[]{x509ca};

        sslCtx = SSLContext.getInstance("TLS");
        sslCtx.init(
                null,
                new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                Exception error = null;

                                if (null == chain || 0 == chain.length) {
                                    error = new CertificateException("Certificate chain is invalid.");
                                } else if (null == authType || 0 == authType.length()) {
                                    error = new CertificateException("Authentication type is invalid.");
                                } else {
                                    Log.i(LOG_TAG, "Chain includes " + chain.length + " certificates.");
                                    try {
                                        for (X509Certificate cert : chain) {
                                            Log.i(LOG_TAG, "Server Certificate Details:");
                                            Log.i(LOG_TAG, "---------------------------");
                                            Log.i(LOG_TAG, "IssuerDN: " + cert.getIssuerDN().toString());
                                            Log.i(LOG_TAG, "SubjectDN: " + cert.getSubjectDN().toString());
                                            Log.i(LOG_TAG, "Serial Number: " + cert.getSerialNumber());
                                            Log.i(LOG_TAG, "Version: " + cert.getVersion());
                                            Log.i(LOG_TAG, "Not before: " + cert.getNotBefore().toString());
                                            Log.i(LOG_TAG, "Not after: " + cert.getNotAfter().toString());
                                            Log.i(LOG_TAG, "---------------------------");

                                            // Make sure that it hasn't expired.
                                            cert.checkValidity();

                                            // Verify the certificate's public key chain.
                                            cert.verify(rootca.getPublicKey());
                                        }
                                    } catch (InvalidKeyException e) {
                                        error = e;
                                    } catch (NoSuchAlgorithmException e) {
                                        error = e;
                                    } catch (NoSuchProviderException e) {
                                        error = e;
                                    } catch (SignatureException e) {
                                        error = e;
                                    }
                                }
                                if (null != error) {
                                    Log.e(LOG_TAG, "Certificate error", error);
                                    throw new CertificateException(error);
                                }
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return acceptedIssuers;
                            }
                        }
                },
                null
        );

        setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException {

        injectHostname(socket, host);
        return sslCtx.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslCtx.getSocketFactory().createSocket();
    }

    /**
     * Pre-ICS Android had a bug resolving HTTPS addresses. This workaround fixes that bug.
     *
     * @param socket The socket to alter
     * @param host   Hostname to connect to
     * @see <a href="https://code.google.com/p/android/issues/detail?id=13117#c14">https://code.google.com/p/android/issues/detail?id=13117#c14</a>
     */
    private void injectHostname(Socket socket, String host) {
        try {
            if (Integer.valueOf(Build.VERSION.SDK) >= 4) {
                Field field = InetAddress.class.getDeclaredField("hostName");
                field.setAccessible(true);
                field.set(socket.getInetAddress(), host);
            }
        } catch (Exception ignored) {
        }
    }
}
