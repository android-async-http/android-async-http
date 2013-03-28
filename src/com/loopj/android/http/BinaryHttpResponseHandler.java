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

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Message;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}. Receives response body as byte array with a 
 * content-type whitelist. (e.g. checks Content-Type against allowed list, 
 * Content-length).
 * <p>
 * For example:
 * <p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * String[] allowedTypes = new String[] { "image/png" };
 * client.get("http://www.example.com/image.png", new BinaryHttpResponseHandler(allowedTypes) {
 *     &#064;Override
 *     public void onSuccess(byte[] imageData) {
 *         // Successfully got a response
 *     }
 *
 *     &#064;Override
 *     public void onFailure(Throwable e, byte[] imageData) {
 *         // Response failed :(
 *     }
 * });
 * </pre>
 */
public class BinaryHttpResponseHandler extends AsyncHttpResponseHandler {
    // Allow images by default
    private static String[] mAllowedContentTypes = new String[] {
        "image/jpeg",
        "image/png"
    };

    /**
     * Creates a new BinaryHttpResponseHandler
     */
    public BinaryHttpResponseHandler() {
        super();
    }

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed
     * content types with passed String array (hopefully) of content types.
     */
    public BinaryHttpResponseHandler(String[] allowedContentTypes) {
        this();
        mAllowedContentTypes = allowedContentTypes;
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param binaryData the body of the HTTP response from the server
     */
    public void onSuccess(byte[] binaryData) {}

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param statusCode the status code of the response
     * @param binaryData the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, byte[] binaryData) {
        onSuccess(binaryData);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param error the underlying cause of the failure
     * @param binaryData the response body, if any
     * @deprecated
     */
    @Deprecated
    public void onFailure(Throwable error, byte[] binaryData) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error);
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    protected void sendSuccessMessage(int statusCode, byte[] responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, responseBody}));
    }

    @Override
    protected void sendFailureMessage(Throwable e, byte[] responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(int statusCode, byte[] responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(Throwable e, byte[] responseBody) {
        onFailure(e, responseBody);
    }

    // Methods which emulate android's Handler and Message methods
    @Override
    protected void handleMessage(Message msg) {
        Object[] response;
        switch(msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue() , (byte[]) response[1]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
                handleFailureMessage((Throwable)response[0], response[1].toString());
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    // Interface to AsyncHttpRequest
    @Override
    void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        Header[] contentTypeHeaders = response.getHeaders("Content-Type");
        byte[] responseBody = null;
        if(contentTypeHeaders.length != 1) {
            //malformed/ambiguous HTTP Header, ABORT!
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!"), responseBody);
            return;
        }
        Header contentTypeHeader = contentTypeHeaders[0];
        boolean foundAllowedContentType = false;
        for(String anAllowedContentType : mAllowedContentTypes) {
            if(Pattern.matches(anAllowedContentType, contentTypeHeader.getValue())) {
                foundAllowedContentType = true;
            }
        }
        if(!foundAllowedContentType) {
            //Content-Type not in allowed list, ABORT!
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"), responseBody);
            return;
        }
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if(temp != null) {
                entity = new BufferedHttpEntity(temp);
            }
            responseBody = EntityUtils.toByteArray(entity);
        } catch(IOException e) {
            sendFailureMessage(e, (byte[]) null);
        }

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
        } else {
            sendSuccessMessage(status.getStatusCode(), responseBody);
        }
    }
}