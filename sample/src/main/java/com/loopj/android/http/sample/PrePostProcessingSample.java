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

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class PrePostProcessingSample extends SampleParentActivity {

    private static final String LOG_TAG = "PrePostProcessingSample";

    protected static final int LIGHTGREY = Color.parseColor("#E0E0E0");
    protected static final int DARKGREY = Color.parseColor("#888888");

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.post(this, URL, headers, entity, null, responseHandler);
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_pre_post_processing;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return true;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "httpbin.org/post";
    }

    @Override
    public AsyncHttpRequest getHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
        return new PrePostProcessRequest(client, httpContext, uriRequest, responseHandler);
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new AsyncHttpResponseHandler() {

            @Override
            public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                debugProcessing(LOG_TAG, "Pre",
                    "Response is about to be pre-processed", LIGHTGREY);
            }

          @Override
            public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                debugProcessing(LOG_TAG, "Post",
                    "Response is about to be post-processed", DARKGREY);
            }

            @Override
            public void onStart() {
                clearOutputs();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugResponse(LOG_TAG, new String(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, e);
                if (errorResponse != null) {
                    debugResponse(LOG_TAG, new String(errorResponse));
                }
            }
        };
    }

    protected void debugProcessing(String TAG, String state, String message, final int color) {
        final String debugMessage = String.format(Locale.US, "%s-processing: %s", state, message);
        Log.d(TAG, debugMessage);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              addView(getColoredView(color, debugMessage));
            }
        });
    }

    private class PrePostProcessRequest extends AsyncHttpRequest {

        public PrePostProcessRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
            super(client, context, request, responseHandler);
        }

        @Override
        public void onPreProcessRequest(AsyncHttpRequest request) {
            debugProcessing(LOG_TAG, "Pre",
                "Request is about to be pre-processed", LIGHTGREY);
        }

        @Override
        public void onPostProcessRequest(AsyncHttpRequest request) {
            debugProcessing(LOG_TAG, "Post",
                "Request is about to be post-processed", DARKGREY);
        }
    }
}
