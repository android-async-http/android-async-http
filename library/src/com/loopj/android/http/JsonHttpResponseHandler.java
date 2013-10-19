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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}, with automatic parsing into a {@link JSONObject}
 * or {@link JSONArray}.
 * <p>&nbsp;</p>
 * This class is designed to be passed to get, post, put and delete requests
 * with the {@link #onSuccess(JSONObject)} or {@link #onSuccess(JSONArray)}
 * methods anonymously overridden.
 * <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the
 * parent class.
 */
public class JsonHttpResponseHandler extends TextHttpResponseHandler {
    private static final String LOG_TAG = "JsonHttpResponseHandler";

    /**
     * Creates a new JsonHttpResponseHandler
     */

    public JsonHttpResponseHandler() {
        super(DEFAULT_CHARSET);
    }

    public JsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param response the parsed json object found in the server response (if any)
     */
    public void onSuccess(JSONObject response) {
    }


    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param response the parsed json array found in the server response (if any)
     */
    public void onSuccess(JSONArray response) {
    }

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param statusCode the status code of the response
     * @param headers    the headers of the HTTP response
     * @param response   the parsed json object found in the server response (if any)
     */
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        onSuccess(statusCode, response);
    }

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param statusCode the status code of the response
     * @param response   the parsed json object found in the server response (if any)
     */
    public void onSuccess(int statusCode, JSONObject response) {
        onSuccess(response);
    }

    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param statusCode the status code of the response
     * @param headers    the headers of the HTTP response
     * @param response   the parsed json array found in the server response (if any)
     */
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        onSuccess(statusCode, response);
    }

    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param statusCode the status code of the response
     * @param response   the parsed json array found in the server response (if any)
     */
    public void onSuccess(int statusCode, JSONArray response) {
        onSuccess(response);
    }

    public void onFailure(Throwable e, JSONObject errorResponse) {
        onFailure(e);
    }

    public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
        onFailure(e, errorResponse);
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
        onFailure(statusCode, e, errorResponse);
    }

    public void onFailure(Throwable e, JSONArray errorResponse) {
        onFailure(e);
    }

    public void onFailure(int statusCode, Throwable e, JSONArray errorResponse) {
        onFailure(e, errorResponse);
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
        onFailure(statusCode, e, errorResponse);
    }

    @Override
    public void onSuccess(final int statusCode, final Header[] headers, final String responseBody) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBody);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (jsonResponse instanceof JSONObject) {
                                    onSuccess(statusCode, headers, (JSONObject) jsonResponse);
                                } else if (jsonResponse instanceof JSONArray) {
                                    onSuccess(statusCode, headers, (JSONArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    onSuccess(statusCode, headers, (String) jsonResponse);
                                } else {
                                    onFailure(new JSONException("Unexpected type " + jsonResponse.getClass().getName()), (JSONObject) null);
                                }

                            }
                        });
                    } catch (final JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(ex, (JSONObject) null);
                            }
                        });
                    }
                }
            }).start();
        } else {
            onSuccess(statusCode, headers, new JSONObject());
        }
    }

    @Override
    public void onFailure(final int statusCode, final Header[] headers, final String responseBody, final Throwable e) {
        if (responseBody != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBody);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (jsonResponse instanceof JSONObject) {
                                    onFailure(statusCode, headers, e, (JSONObject) jsonResponse);
                                } else if (jsonResponse instanceof JSONArray) {
                                    onFailure(statusCode, headers, e, (JSONArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    onFailure(statusCode, headers, e, (String) jsonResponse);
                                } else {
                                    onFailure(new JSONException("Unexpected type " + jsonResponse.getClass().getName()), (JSONObject) null);
                                }
                            }
                        });

                    } catch (final JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(ex, (JSONObject) null);
                            }
                        });

                    }
                }
            }).start();
        } else {
            Log.v(LOG_TAG, "response body is null, calling onFailure(Throwable, JSONObject)");
            onFailure(e, (JSONObject) null);
        }
    }

    protected Object parseResponse(String responseBody) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
        String jsonString = responseBody.trim();
        if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
            result = new JSONTokener(jsonString).nextValue();
        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }
}
