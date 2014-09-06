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

import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class AsyncBackgroundThreadSample extends SampleParentActivity {
    private static final String LOG_TAG = "AsyncBackgroundThreadSample";

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public RequestHandle executeSample(final AsyncHttpClient client, final String URL, final Header[] headers, HttpEntity entity, final ResponseHandlerInterface responseHandler) {

        final Activity ctx = this;
        FutureTask<RequestHandle> future = new FutureTask<>(new Callable<RequestHandle>() {
            public RequestHandle call() {
                Log.d(LOG_TAG, "Executing GET request on background thread");
                return client.get(ctx, URL, headers, null, responseHandler);
            }
        });

        executor.execute(future);

        RequestHandle handle = null;
        try {
            handle = future.get(5, TimeUnit.SECONDS);
            Log.d(LOG_TAG, "Background thread for GET request has finished");
        } catch (Exception e) {
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return handle;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_async_background_thread;
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
        return PROTOCOL + "httpbin.org/get";
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {

        FutureTask<ResponseHandlerInterface> future = new FutureTask<>(new Callable<ResponseHandlerInterface>() {

            @Override
            public ResponseHandlerInterface call() throws Exception {
                Log.d(LOG_TAG, "Creating AsyncHttpResponseHandler on background thread");
                return new AsyncHttpResponseHandler(Looper.getMainLooper()) {

                    @Override
                    public void onStart() {
                        clearOutputs();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        Log.d(LOG_TAG, String.format("onSuccess executing on main thread : %B", Looper.myLooper() == Looper.getMainLooper()));
                        debugHeaders(LOG_TAG, headers);
                        debugStatusCode(LOG_TAG, statusCode);
                        debugResponse(LOG_TAG, new String(response));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.d(LOG_TAG, String.format("onFailure executing on main thread : %B", Looper.myLooper() == Looper.getMainLooper()));
                        debugHeaders(LOG_TAG, headers);
                        debugStatusCode(LOG_TAG, statusCode);
                        debugThrowable(LOG_TAG, e);
                        if (errorResponse != null) {
                            debugResponse(LOG_TAG, new String(errorResponse));
                        }
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        Toast.makeText(AsyncBackgroundThreadSample.this,
                                String.format("Request is retried, retry no. %d", retryNo),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                };
            }
        });

        executor.execute(future);

        ResponseHandlerInterface responseHandler = null;
        try {
            responseHandler = future.get();
            Log.d(LOG_TAG, "Background thread for AsyncHttpResponseHandler has finished");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseHandler;
    }
}
