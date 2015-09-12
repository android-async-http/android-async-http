package com.loopj.android.http;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * Blank implementation of ResponseHandlerInterface, which ignores all contents returned by
 * remote HTTP endpoint, and discards all various log messages
 * <p>&nbsp;</p>
 * Use this implementation, if you deliberately want to ignore all response, because you cannot
 * pass null ResponseHandlerInterface into AsyncHttpClient implementation
 */
public class BlackholeHttpResponseHandler extends AsyncHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }

    @Override
    public void onProgress(long bytesWritten, long totalSize) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

    }

    @Override
    public void onRetry(int retryNo) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onUserException(Throwable error) {

    }
}
