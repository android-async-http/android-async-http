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

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.File;
import java.io.IOException;

/**
 * This sample demonstrates use of {@link RangeFileAsyncHttpResponseHandler} to
 * download a remote file in multiple requests. While this response handler
 * class handles file storage, it's up to the app itself to request all chunks
 * of the file.
 *
 * Also demonstrated a method to query the remote file's size prior to sending
 * the actual GET requests. This ensures that the remote server is actually
 * capable of supporting the "Range" header, necessary to make this sample work.
 *
 * @author Noor Dawod <github@fineswap.com>
 */
public class RangeResponseSample extends GetSample {

    public static final String LOG_TAG = "RangeResponseSample";

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String ACCEPT_RANGES = "Accept-Ranges";
    private static final int CHUNK_SIZE = 10240;

    private File file;
    private long fileSize = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Temporary file to host the URL's downloaded contents.
            file = File.createTempFile("temp_", "_handled", getCacheDir());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot create temporary file", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove temporary file.
        if (file != null) {
            if (!file.delete()) {
                Log.e(LOG_TAG, String.format("Couldn't remove temporary file in path: %s", file.getAbsolutePath()));
            }
            file = null;
        }
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public String getDefaultURL() {
        return "http://upload.wikimedia.org/wikipedia/commons/f/fa/Geysers_on_Mars.jpg";
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_range_sample;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        if (fileSize > 0) {
            // Send a GET query when we know the size of the remote file.
            return client.get(this, URL, headers, null, responseHandler);
        } else {
            // Send a HEAD query to know the size of the remote file.
            return client.head(this, URL, headers, null, responseHandler);
        }
    }

    public void sendNextRangeRequest() {
        if (file.length() < fileSize) {
            // File is still smaller than remote file; send a new request.
            onRunButtonPressed();
        }
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new RangeFileAsyncHttpResponseHandler(file) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);

                if (fileSize < 1) {
                    boolean supportsRange = false;
                    // Cycle through the headers and look for the Content-Length header.
                    for (Header header : headers) {
                        String headerName = header.getName();
                        if (CONTENT_LENGTH.equals(headerName)) {
                            fileSize = Long.parseLong(header.getValue());
                        } else if (ACCEPT_RANGES.equals(headerName)) {
                            supportsRange = true;
                        }
                    }

                    // Is the content length known?
                    if (!supportsRange || fileSize < 1) {
                        Toast.makeText(
                                RangeResponseSample.this,
                                "Unable to determine remote file's size, or\nremote server doesn't support ranges",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

                // If remote file size is known, request next portion.
                if (fileSize > 0) {
                    debugFileResponse(file);
                    // Send a new request for the same resource.
                    sendNextRangeRequest();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, File file) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, e);
                debugFileResponse(file);
            }

            @Override
            public void updateRequestHeaders(HttpUriRequest uriRequest) {
                // Call super so appending could work.
                super.updateRequestHeaders(uriRequest);

                // Length of the downloaded content thus far.
                long length = file.length();

                // Request the next portion of the file to be downloaded.
                uriRequest.setHeader("Range", "bytes=" + length + "-" + (length + CHUNK_SIZE - 1));
            }

            void debugFileResponse(File file) {
                debugResponse(LOG_TAG, "File size thus far: " + file.length() + " bytes");
            }
        };
    }
}
