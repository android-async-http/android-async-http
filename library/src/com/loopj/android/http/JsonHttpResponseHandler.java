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

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Message;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}, with automatic parsing into a {@link JSONObject}
 * or {@link JSONArray}.
 * <p>&nbsp;</p>
 * This class is designed to be passed to get, post, put and delete requests
 * with the {@link #onSuccess(int, Object)}
 * method anonymously overridden.
 * <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the
 * parent class.
 */
public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
    // chain message values, lets not just make them up.
    protected static final int SUCCESS_JSON_MESSAGE = AsyncHttpResponseHandler.LAST_MESSAGE;

    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your own code.
     *
     * @param statusCode the HTTP Status code for the response
     * @param response the parsed json object found in the server response.
     *                 Check the type of the object to determine if it is one
     *                 of the valid types created by {@link JSONTokener#nextValue()}
     */
    public void onSuccess(int statusCode, Object response) {
    }

    /**
     * onSuccess is overridden here to perform background processing of the JSON packet
     */
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        final int _statusCode = statusCode;
        final Header[] _headers = headers;
        final byte[] _responseBody = responseBody;

        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object jsonResponse = parseResponse(_responseBody);
                        sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{_statusCode, _headers, jsonResponse}));
                    } catch (JSONException e) {
                        // have to do this via sendFailureMessage so that onFailure will finally be called on the main / UI thread
                        sendFailureMessage(_statusCode, _headers, _responseBody, e);
                    }
                }
            }).start();
        } else {
            // already on the main / UI thread so lets just call onSuccess
            onSuccess( statusCode, new JSONObject());
        }
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleMessage(Message msg) {
        Object[] response;
        switch(msg.what){
            case SUCCESS_JSON_MESSAGE:
                response = (Object[]) msg.obj;
                handleSuccessJsonMessage((Integer) response[0], (Header[]) response[1], response[2]);
                break;
            default:
                super.handleMessage(msg);
        }
    }

    protected void handleSuccessJsonMessage(int statusCode, Header[] headers, Object jsonResponse) {
        onSuccess(statusCode, jsonResponse);
    }

    protected Object parseResponse(byte[] responseBody) throws JSONException {
        Object result = null;
        String responseBodyText = null;
        try {
            responseBodyText = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new JSONException("Unable to convert response to UTF-8 string");
        }
        
        // trim the string to prevent start with blank, and test if the string
        // is valid JSON, because the parser don't do this :(. If Json is not
        // valid this will return null
        responseBodyText = responseBodyText.trim();
        if (responseBodyText.startsWith("{") || responseBodyText.startsWith("[")) {
            result = new JSONTokener(responseBodyText).nextValue();
        }
        if (result == null) {
            result = responseBodyText;
        }
        return result;
    }

}
