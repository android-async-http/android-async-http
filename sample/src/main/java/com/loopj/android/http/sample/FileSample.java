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

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.sample.util.FileUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;

public class FileSample extends SampleParentActivity {
    private static final String LOG_TAG = "FileSample";

    @Override
    public int getSampleTitle() {
        return R.string.title_file_sample;
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
        return "https://httpbin.org/robots.txt";
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onStart() {
                clearOutputs();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugFile(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, throwable);
                debugFile(file);
            }

            private void debugFile(File file) {
                if (file == null || !file.exists()) {
                    debugResponse(LOG_TAG, "Response is null");
                    return;
                }
                try {
                    debugResponse(LOG_TAG, file.getAbsolutePath() + "\r\n\r\n" + FileUtil.getStringFromFile(file));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "Cannot debug file contents", t);
                }
                if (!deleteTargetFile()) {
                    Log.d(LOG_TAG, "Could not delete response file " + file.getAbsolutePath());
                }
            }
        };
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }
}
