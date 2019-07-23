/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
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

package com.loopj.android.http.sample;

import android.util.SparseArray;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class ThreadingTimeoutSample extends SampleParentActivity {

    private static final String LOG_TAG = "ThreadingTimeoutSample";
    protected final SparseArray<String> states = new SparseArray<String>();
    protected int counter = 0;

    @Override
    public int getSampleTitle() {
        return R.string.title_threading_timeout;
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
    public boolean isCancelButtonAllowed() {
        return true;
    }

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "httpbin.org/delay/6";
    }

    protected synchronized void setStatus(int id, String status) {
        String current = states.get(id, null);
        states.put(id, current == null ? status : current + "," + status);
        clearOutputs();
        for (int i = 0; i < states.size(); i++) {
            debugResponse(LOG_TAG, String.format(Locale.US, "%d (from %d): %s", states.keyAt(i), getCounter(), states.get(states.keyAt(i))));
        }
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new AsyncHttpResponseHandler() {

            private final int id = counter++;

            @Override
            public void onStart() {
                setStatus(id, "START");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setStatus(id, "SUCCESS");
            }

            @Override
            public void onFinish() {
                setStatus(id, "FINISH");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                setStatus(id, "FAILURE");
            }

            @Override
            public void onCancel() {
                setStatus(id, "CANCEL");
            }
        };
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }
}
