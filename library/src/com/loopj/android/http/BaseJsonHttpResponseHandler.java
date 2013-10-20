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

import org.apache.http.Header;
import org.apache.http.HttpStatus;

public abstract class BaseJsonHttpResponseHandler<JSON_TYPE> extends TextHttpResponseHandler {
    private static final String LOG_TAG = "BaseJsonHttpResponseHandler";

    /**
     * Creates a new JsonHttpResponseHandler
     */

    public BaseJsonHttpResponseHandler() {
        super(DEFAULT_CHARSET);
    }

    public BaseJsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    public void onSuccess(int statusCode, Header[] headers, JSON_TYPE response) {
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSON_TYPE errorResponse) {
    }

    @Override
    public void onSuccess(final int statusCode, final Header[] headers, final String responseBody) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSON_TYPE jsonResponse = parseResponse(responseBody);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onSuccess(statusCode, headers, jsonResponse);
                            }
                        });
                    } catch (final Throwable t) {
                        Log.d(LOG_TAG, "parseResponse thrown an problem", t);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, t, (JSON_TYPE) null);
                            }
                        });
                    }
                }
            }).start();
        } else {
            onSuccess(statusCode, headers, (JSON_TYPE) null);
        }
    }

    @Override
    public void onFailure(final int statusCode, final Header[] headers, final String responseBody, final Throwable e) {
        if (responseBody != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSON_TYPE jsonResponse = parseResponse(responseBody);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, e, jsonResponse);
                            }
                        });
                    } catch (Throwable t) {
                        Log.d(LOG_TAG, "parseResponse thrown an problem", t);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, e, (JSON_TYPE) null);
                            }
                        });
                    }
                }
            }).start();
        } else {
            onFailure(statusCode, headers, e, (JSON_TYPE) null);
        }
    }

    protected abstract JSON_TYPE parseResponse(String responseBody) throws Throwable;
}
