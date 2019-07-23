/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://github.com/android-async-http/android-async-http

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * This file is introduced to fix HTTPS Post bug on API &lt; ICS see
 * https://code.google.com/p/android/issues/detail?id=13117#c14 <p>&nbsp;</p> Warning! This omits SSL
 * certificate validation on every device, use with caution
 */
public class MySSLSocketFactory extends SSLSocketFactory {
    final SSLContext sslContext = SSLContext.getInstance("TLS");

    /**
     * Creates a new SSL Socket Factory with the given KeyStore.
     *
     * @param truststore A KeyStore to create the SSL Socket Factory in context of
     * @throws NoSuchAlgorithmException  NoSuchAlgorithmException
     * @throws KeyManagementException    KeyManagementException
     * @throws KeyStoreException         KeyStoreException
     * @throws UnrecoverableKeyException UnrecoverableKeyException
     */
    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    /**
     * Gets a KeyStore containing the Certificate
     *
     * @param cert InputStream of the Certificate
     * @return KeyStore
     */
    public static KeyStore getKeystoreOfCA(InputStream cert) {

        // Load CAs from an InputStream
        InputStream caInput = null;
        Certificate ca = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(cert);
            ca = cf.generateCertificate(caInput);
        } catch (CertificateException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (caInput != null) {
                    caInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    /**
     * Gets a Default KeyStore
     *
     * @return KeyStore
     */
    public static KeyStore getKeystore() {
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return trustStore;
    }

    /**
     * Returns a SSlSocketFactory which trusts all certificates
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getFixedSocketFactory() {
        SSLSocketFactory socketFactory;
        try {
            socketFactory = new MySSLSocketFactory(getKeystore());
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Throwable t) {
            t.printStackTrace();
            socketFactory = SSLSocketFactory.getSocketFactory();
        }
        return socketFactory;
    }

    /**
     * Gets a DefaultHttpClient which trusts a set of certificates specified by the KeyStore
     *
     * @param keyStore custom provided KeyStore instance
     * @return DefaultHttpClient
     */
    public static DefaultHttpClient getNewHttpClient(KeyStore keyStore) {

        try {
            SSLSocketFactory sf = new MySSLSocketFactory(keyStore);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        Socket localSocket = sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        enableSecureProtocols(localSocket);
        return localSocket;
    }

    @Override
    public Socket createSocket() throws IOException {
        Socket socket = sslContext.getSocketFactory().createSocket();
        enableSecureProtocols(socket);
        return socket;
    }

    /**
     * Activate supported protocols on the socket.
     *
     * @param socket    The socket on which to activate secure protocols.
     */
    private void enableSecureProtocols(Socket socket) {
        // set all supported protocols
        SSLParameters params = sslContext.getSupportedSSLParameters();
        ((SSLSocket) socket).setEnabledProtocols(params.getProtocols());
    }

    /**
     * Makes HttpsURLConnection trusts a set of certificates specified by the KeyStore
     */
    public void fixHttpsURLConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
}