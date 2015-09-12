/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://loopj.com

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

package com.loopj.android.http;

import android.os.Looper;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}.
 * Receives response body as byte array with a content-type whitelist. (e.g. checks Content-Type
 * against allowed list, Content-length). <p>&nbsp;</p> For example: <p>&nbsp;</p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * String[] allowedTypes = new String[] { "image/png" };
 * client.get("https://www.example.com/image.png", new BinaryHttpResponseHandler(allowedTypes) {
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
public abstract class BinaryHttpResponseHandler extends AsyncHttpResponseHandler {

    private static final String LOG_TAG = "BinaryHttpRH";

    private String[] mAllowedContentTypes = new String[]{
            RequestParams.APPLICATION_OCTET_STREAM,
            "image/jpeg",
            "image/png",
            "image/gif"
    };

    /**
     * Creates a new BinaryHttpResponseHandler
     */
    public BinaryHttpResponseHandler() {
        super();
    }

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed content types with
     * passed String array (hopefully) of content types.
     *
     * @param allowedContentTypes content types array, eg. 'image/jpeg' or pattern '.*'
     */
    public BinaryHttpResponseHandler(String[] allowedContentTypes) {
        super();
        if (allowedContentTypes != null) {
            mAllowedContentTypes = allowedContentTypes;
        } else {
            AsyncHttpClient.log.e(LOG_TAG, "Constructor passed allowedContentTypes was null !");
        }
    }

    /**
     * Creates a new BinaryHttpResponseHandler with a user-supplied looper, and overrides the default allowed content types with
     * passed String array (hopefully) of content types.
     *
     * @param allowedContentTypes content types array, eg. 'image/jpeg' or pattern '.*'
     * @param looper              The looper to work with
     */
    public BinaryHttpResponseHandler(String[] allowedContentTypes, Looper looper) {
        super(looper);
        if (allowedContentTypes != null) {
            mAllowedContentTypes = allowedContentTypes;
        } else {
            AsyncHttpClient.log.e(LOG_TAG, "Constructor passed allowedContentTypes was null !");
        }
    }

    /**
     * Method can be overriden to return allowed content types, can be sometimes better than passing
     * data in constructor
     *
     * @return array of content-types or Pattern string templates (eg. '.*' to match every response)
     */
    public String[] getAllowedContentTypes() {
        return mAllowedContentTypes;
    }

    @Override
    public abstract void onSuccess(int statusCode, Header[] headers, byte[] binaryData);

    @Override
    public abstract void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error);

    @Override
    public final void sendResponseMessage(HttpResponse response) throws IOException {
        StatusLine status = response.getStatusLine();
        Header[] contentTypeHeaders = response.getHeaders(AsyncHttpClient.HEADER_CONTENT_TYPE);
        if (contentTypeHeaders.length != 1) {
            //malformed/ambiguous HTTP Header, ABORT!
            sendFailureMessage(
                    status.getStatusCode(),
                    response.getAllHeaders(),
                    null,
                    new HttpResponseException(
                            status.getStatusCode(),
                            "None, or more than one, Content-Type Header found!"
                    )
            );
            return;
        }
        Header contentTypeHeader = contentTypeHeaders[0];
        boolean foundAllowedContentType = false;
        for (String anAllowedContentType : getAllowedContentTypes()) {
            try {
                if (Pattern.matches(anAllowedContentType, contentTypeHeader.getValue())) {
                    foundAllowedContentType = true;
                }
            } catch (PatternSyntaxException e) {
                AsyncHttpClient.log.e(LOG_TAG, "Given pattern is not valid: " + anAllowedContentType, e);
            }
        }
        if (!foundAllowedContentType) {
            //Content-Type not in allowed list, ABORT!
            sendFailureMessage(
                    status.getStatusCode(),
                    response.getAllHeaders(),
                    null,
                    new HttpResponseException(
                            status.getStatusCode(),
                            "Content-Type (" + contentTypeHeader.getValue() + ") not allowed!"
                    )
            );
            return;
        }
        super.sendResponseMessage(response);
    }
}
