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

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}, with
 * automatic parsing into a {@link JSONObject} or {@link JSONArray}. <p>&nbsp;</p> This class is
 * designed to be passed to get, post, put and delete requests with the {@link #onSuccess(int,
 * org.apache.http.Header[], org.json.JSONArray)} or {@link #onSuccess(int,
 * org.apache.http.Header[], org.json.JSONObject)} methods anonymously overridden. <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the parent class.
 */
public class JsonHttpResponseHandler extends TextHttpResponseHandler {

    private static final String LOG_TAG = "JsonHttpResponseHandler";

    /**
     * Creates new JsonHttpResponseHandler, with JSON String encoding UTF-8
     */
    public JsonHttpResponseHandler() {
        super(DEFAULT_CHARSET);
    }

    /**
     * Creates new JsonHttpRespnseHandler with given JSON String encoding
     *
     * @param encoding String encoding to be used when parsing JSON
     */
    public JsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.w(LOG_TAG, "onSuccess(int, Header[], JSONObject) was not overriden, but callback was received");
    }

    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.w(LOG_TAG, "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

    /**
     * Returns when request failed
     *
     * @param statusCode    http response status line
     * @param headers       response headers if any
     * @param throwable     throwable describing the way request failed
     * @param errorResponse parsed response if any
     */
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONObject) was not overriden, but callback was received", throwable);
    }

    /**
     * Returns when request failed
     *
     * @param statusCode    http response status line
     * @param headers       response headers if any
     * @param throwable     throwable describing the way request failed
     * @param errorResponse parsed response if any
     */
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONArray) was not overriden, but callback was received", throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.w(LOG_TAG, "onFailure(int, Header[], String, Throwable) was not overriden, but callback was received", throwable);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        Log.w(LOG_TAG, "onSuccess(int, Header[], String) was not overriden, but callback was received");
    }

    @Override
    public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            Runnable parser = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBytes);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (jsonResponse instanceof JSONObject) {
                                    onSuccess(statusCode, headers, (JSONObject) jsonResponse);
                                } else if (jsonResponse instanceof JSONArray) {
                                    onSuccess(statusCode, headers, (JSONArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    onFailure(statusCode, headers, (String) jsonResponse, new JSONException("Response cannot be parsed as JSON data"));
                                } else {
                                    onFailure(statusCode, headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject) null);
                                }

                            }
                        });
                    } catch (final JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, ex, (JSONObject) null);
                            }
                        });
                    }
                }
            };
            if (!getUseSynchronousMode()) {
                new Thread(parser).start();
            } else {
                // In synchronous mode everything should be run on one thread
                parser.run();
            }
        } else {
            onSuccess(statusCode, headers, new JSONObject());
        }
    }

    @Override
    public final void onFailure(final int statusCode, final Header[] headers, final byte[] responseBytes, final Throwable throwable) {
        if (responseBytes != null) {
            Runnable parser = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBytes);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (jsonResponse instanceof JSONObject) {
                                    onFailure(statusCode, headers, throwable, (JSONObject) jsonResponse);
                                } else if (jsonResponse instanceof JSONArray) {
                                    onFailure(statusCode, headers, throwable, (JSONArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    onFailure(statusCode, headers, (String) jsonResponse, throwable);
                                } else {
                                    onFailure(statusCode, headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject) null);
                                }
                            }
                        });

                    } catch (final JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, ex, (JSONObject) null);
                            }
                        });

                    }
                }
            };
            if (!getUseSynchronousMode()) {
                new Thread(parser).start();
            } else {
                // In synchronous mode everything should be run on one thread
                parser.run();
            }
        } else {
            Log.v(LOG_TAG, "response body is null, calling onFailure(Throwable, JSONObject)");
            onFailure(statusCode, headers, throwable, (JSONObject) null);
        }
    }

    /**
     * Returns Object of type {@link JSONObject}, {@link JSONArray}, String, Boolean, Integer, Long,
     * Double or {@link JSONObject#NULL}, see {@link org.json.JSONTokener#nextValue()}
     *
     * @param responseBody response bytes to be assembled in String and parsed as JSON
     * @return Object parsedResponse
     * @throws org.json.JSONException exception if thrown while parsing JSON
     */
    protected Object parseResponse(byte[] responseBody) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If JSON is not valid this will return null
        String jsonString = getResponseString(responseBody, getCharset());
        if (jsonString != null) {
            jsonString = jsonString.trim();
            if (jsonString.startsWith(UTF8_BOM)) {
                jsonString = jsonString.substring(1);
            }
            if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                result = new JSONTokener(jsonString).nextValue();
            }
        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }
}
