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
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class CancelRequestByTagSample extends ThreadingTimeoutSample {

    private static final String LOG_TAG = "CancelRequestByTagSample";
    private static final Integer REQUEST_TAG = 132435;

    @Override
    public int getSampleTitle() {
        return R.string.title_cancel_tag;
    }

    @Override
    public void onCancelButtonPressed() {
        Log.d(LOG_TAG, "Canceling requests by TAG: " + REQUEST_TAG);
        getAsyncHttpClient().cancelRequestsByTAG(REQUEST_TAG, false);
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler).setTag(REQUEST_TAG);
    }
}
