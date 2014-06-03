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

import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class SynchronousClientSample extends GetSample {
    private static final String LOG_TAG = "SyncSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAsyncHttpClient(new SyncHttpClient());
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_synchronous;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public String getDefaultURL() {
        return "https://httpbin.org/delay/6";
    }

    @Override
    public RequestHandle executeSample(final AsyncHttpClient client, final String URL, final Header[] headers, HttpEntity entity, final ResponseHandlerInterface responseHandler) {
        if (client instanceof SyncHttpClient) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "Before Request");
                    client.get(SynchronousClientSample.this, URL, headers, null, responseHandler);
                    Log.d(LOG_TAG, "After Request");
                }
            }).start();
        } else {
            Log.e(LOG_TAG, "Error, not using SyncHttpClient");
        }
        /**
         * SyncHttpClient does not return RequestHandle,
         * it executes each request directly,
         * therefore those requests are not in cancelable threads
         * */
        return null;
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearOutputs();
                    }
                });
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final byte[] response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        debugHeaders(LOG_TAG, headers);
                        debugStatusCode(LOG_TAG, statusCode);
                        debugResponse(LOG_TAG, new String(response));
                    }
                });
            }

            @Override
            public void onFailure(final int statusCode, final Header[] headers, final byte[] errorResponse, final Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        debugHeaders(LOG_TAG, headers);
                        debugStatusCode(LOG_TAG, statusCode);
                        debugThrowable(LOG_TAG, e);
                        if (errorResponse != null) {
                            debugResponse(LOG_TAG, new String(errorResponse));
                        }
                    }
                });
            }
        };
    }
}
