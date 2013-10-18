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

import android.os.Message;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
    protected static final int SUCCESS_JSON_MESSAGE = 100;

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


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    @Override
    protected void sendSuccessMessage(final int statusCode, final Header[] headers, final String responseBody) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBody);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{statusCode, headers, jsonResponse}));
                            }
                        });
                    } catch (final JSONException e) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                sendFailureMessage(statusCode, headers, e, responseBody);
                            }
                        });
                    }
                }
            }).start();
        } else {
            sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{statusCode, headers, new JSONObject()}));
        }
    }


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case SUCCESS_JSON_MESSAGE:
                Object[] response = (Object[]) msg.obj;
                handleSuccessJsonMessage((Integer) response[0], (Header[]) response[1], response[2]);
                break;
            default:
                super.handleMessage(msg);
        }
    }

    protected void handleSuccessJsonMessage(int statusCode, Header[] headers, Object jsonResponse) {
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

    protected Object parseResponse(String responseBody) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
        responseBody = responseBody.trim();
        if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
            result = new JSONTokener(responseBody).nextValue();
        }
        if (result == null) {
            result = responseBody;
        }
        return result;
    }

    @Override
    protected void handleFailureMessage(final int statusCode, final Header[] headers, final Throwable e, final String responseBody) {
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
                                    onFailure(statusCode, headers, e, responseBody);
                                }
                            }
                        });

                    } catch (JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, e, responseBody);
                            }
                        });

                    }
                }
            }).start();
        } else {
            onFailure(e, "");
        }

    }
}
