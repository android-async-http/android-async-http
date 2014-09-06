package com.loopj.android.http.sample;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.IOException;

public class ResumeDownloadSample extends SampleParentActivity {

    private File downloadTarget;
    private static final String LOG_TAG = "ResumeDownloadSample";

    private File getDownloadTarget() {
        try {
            if (downloadTarget == null) {
                downloadTarget = File.createTempFile("download_", "_resume", getCacheDir());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Couldn't create cache file to download to");
        }
        return downloadTarget;
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new RangeFileAsyncHttpResponseHandler(getDownloadTarget()) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                debugStatusCode(LOG_TAG, statusCode);
                debugHeaders(LOG_TAG, headers);
                debugThrowable(LOG_TAG, throwable);
                if (file != null) {
                    addView(getColoredView(LIGHTGREEN, "Download interrupted (" + statusCode + "): (bytes=" + file.length() + "), path: " + file.getAbsolutePath()));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                debugStatusCode(LOG_TAG, statusCode);
                debugHeaders(LOG_TAG, headers);
                if (file != null) {
                    addView(getColoredView(LIGHTGREEN, "Request succeeded (" + statusCode + "): (bytes=" + file.length() + "), path: " + file.getAbsolutePath()));
                }
            }
        };
    }

    @Override
    public String getDefaultHeaders() {
        return "Range=bytes=10-20";
    }

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "www.google.com/images/srpr/logo11w.png";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_resume_download;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }
}
