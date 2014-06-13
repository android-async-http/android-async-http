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

package com.loopj.android.http.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.sample.util.SampleJSON;
import com.loopj.android.http.sample.util.SecureSocketFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * This sample demonstrates the implementation of self-signed CA's and connection to servers with
 * such certificates. Be sure to read 'res/raw/custom_ca.txt' for how-to instructions on how to
 * generate a BKS file necessary for this sample.
 *
 * @author Noor Dawod <github@fineswap.com>
 */
public class CustomCASample extends SampleParentActivity {

    private static final String LOG_TAG = "CustomCASample";

    // This is A TEST URL for use with AsyncHttpClient LIBRARY ONLY!
    // It is provided courtesy of Fineswap (http://fineswap.com) and must never
    // be used in ANY program except when running this sample (CustomCASample).
    private static final String SERVER_TEST_URL = "https://api.fineswap.io/ahc";

    // The certificate's alias.
    private static final String STORE_ALIAS = "rootca";

    // The certificate's password.
    private static final String STORE_PASS = "Fineswap";

    // Instruct the library to retry connection when this exception is raised.
    static {
        AsyncHttpClient.allowRetryExceptionClass(javax.net.ssl.SSLException.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            InputStream is = null;
            try {
                // Configure the library to use a custom 'bks' file to perform
                // SSL negotiation.
                KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
                is = getResources().openRawResource(R.raw.store);
                store.load(is, STORE_PASS.toCharArray());
                getAsyncHttpClient().setSSLSocketFactory(new SecureSocketFactory(store, STORE_ALIAS));
            } catch (IOException e) {
                throw new KeyStoreException(e);
            } catch (CertificateException e) {
                throw new KeyStoreException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new KeyStoreException(e);
            } catch (KeyManagementException e) {
                throw new KeyStoreException(e);
            } catch (UnrecoverableKeyException e) {
                throw new KeyStoreException(e);
            } finally {
                AsyncHttpClient.silentCloseInputStream(is);
            }
        } catch (KeyStoreException e) {
            Log.e(LOG_TAG, "Unable to initialize key store", e);
            showCustomCAHelp();
        }
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_custom_ca;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public String getDefaultURL() {
        return SERVER_TEST_URL;
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new BaseJsonHttpResponseHandler<SampleJSON>() {

            @Override
            public void onStart() {
                clearOutputs();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, SampleJSON response) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                if (response != null) {
                    debugResponse(LOG_TAG, rawJsonResponse);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, SampleJSON errorResponse) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, throwable);
                if (errorResponse != null) {
                    debugResponse(LOG_TAG, rawJsonData);
                }
            }

            @Override
            protected SampleJSON parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), SampleJSON.class).next();
            }
        };
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }

    /**
     * Returns contents of `custom_ca.txt` file from assets as CharSequence.
     *
     * @return contents of custom_ca.txt file
     */
    private CharSequence getReadmeText() {
        String rtn = "";
        try {
            InputStream stream = getResources().openRawResource(R.raw.custom_ca);
            java.util.Scanner s = new java.util.Scanner(stream)
                    .useDelimiter("\\A");
            rtn = s.hasNext() ? s.next() : "";
        } catch (Resources.NotFoundException e) {
            Log.e(LOG_TAG, "License couldn't be retrieved", e);
        }
        return rtn;
    }

    /**
     * Displays a dialog showing contents of `custom_ca.txt` file from assets.
     * This is needed to avoid Lint's strict mode.
     */
    private void showCustomCAHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_custom_ca);
        builder.setMessage(getReadmeText());
        builder.setNeutralButton(android.R.string.cancel,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }
        );
        builder.show();
    }
}
