/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
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

package com.loopj.android.http;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

class AsyncHttpRequest implements Runnable {
    private final HttpURLConnection client;
    private final HttpUriRequest request;
    private final ResponseHandlerInterface responseHandler;

    public AsyncHttpRequest(HttpURLConnection client, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
        this.client = client;
        this.request = request;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        if (responseHandler != null) {
            responseHandler.sendStartMessage();
        }

        try {
        	makeRequest();
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            }
        }

        if (responseHandler != null) {
            responseHandler.sendFinishMessage();
        }
    }

    private void makeRequest() throws IOException {
        if (!Thread.currentThread().isInterrupted()) {

        	InputStream response = client.getInputStream();

            if (!Thread.currentThread().isInterrupted()) {
                if (responseHandler != null) {
                    responseHandler.sendResponseMessage(response);
                }
            }
        }
    }

    /**
     * @deprecated OKHttp do this out of box
     */
    private void makeRequestWithRetries() throws IOException { 
//        boolean retry = true;
//        IOException cause = null;
//        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
//        try {
//            while (retry) {
//                try {
//                    makeRequest();
//                    return;
//                } catch (UnknownHostException e) {
//                    // switching between WI-FI and mobile data networks can cause a retry which then results in an UnknownHostException
//                    // while the WI-FI is initialising. The retry logic will be invoked here, if this is NOT the first retry
//                    // (to assist in genuine cases of unknown host) which seems better than outright failure
//                    cause = new IOException("UnknownHostException exception: " + e.getMessage());
//                    retry = (executionCount > 0) && retryHandler.retryRequest(cause, ++executionCount, context);
//                } catch (NullPointerException e) {
//                    // there's a bug in HttpClient 4.0.x that on some occasions causes
//                    // DefaultRequestExecutor to throw an NPE, see
//                    // http://code.google.com/p/android/issues/detail?id=5255
//                    cause = new IOException("NPE in HttpClient: " + e.getMessage());
//                    retry = retryHandler.retryRequest(cause, ++executionCount, context);
//                } catch (IOException e) {
//                    cause = e;
//                    retry = retryHandler.retryRequest(cause, ++executionCount, context);
//                }
//                if (retry && (responseHandler != null)) {
//                    responseHandler.sendRetryMessage();
//                }
//            }
//        } catch (Exception e) {
//            // catch anything else to ensure failure message is propagated
//            Log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
//            cause = new IOException("Unhandled exception: " + e.getMessage());
//        }
//
//        // cleaned up to throw IOException
//        throw (cause);
    }
}
