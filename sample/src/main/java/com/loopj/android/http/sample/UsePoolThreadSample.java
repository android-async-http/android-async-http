package com.loopj.android.http.sample;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;

import java.io.File;

public class UsePoolThreadSample extends GetSample {

    private static final String LOG_TAG = "UsePoolThreadSample";

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "httpbin.org/bytes/1024000";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_use_pool_thread;
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new UsePoolThreadResponseHandler();
    }

    private class UsePoolThreadResponseHandler extends AsyncHttpResponseHandler {

        private final File destFile;

        public UsePoolThreadResponseHandler() {
            super();

            // Destination file to save the downloaded bytes to.
            destFile = getRandomCacheFile();
            Log.d(LOG_TAG, "Bytes will be saved in file: " + destFile.getAbsolutePath());

            // We wish to use the same pool thread to run the response.
            setUsePoolThread(true);
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBody) {
            // Response body includes 1MB of data, and it might take few
            // milliseconds, maybe a second or two on old devices, to save it in
            // the filesystem. However, since this callback method is running
            // within the pool thread's execution scope, the UI thread will be
            // relaxed to continue its work of updating the UI while this
            // handler saves the bytes on disk.

            // Save the response body's bytes on disk.
            saveBytesOnDisk(destFile, responseBody);

            // This callback is now running within the pool thread execution
            // scope and not within Android's UI thread, so if we must update
            // the UI, we'll have to dispatch a runnable to the UI thread.
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    debugStatusCode(LOG_TAG, statusCode);
                    debugHeaders(LOG_TAG, headers);
                    if (responseBody != null) {
                        addView(getColoredView(LIGHTGREEN, "Request succeeded (" + statusCode + "): (bytes=" + destFile.length() + "), path: " + destFile.getAbsolutePath()));
                    }
                }
            });
        }

        @Override
        public void onFailure(final int statusCode, final Header[] headers, final byte[] responseBody, final Throwable error) {
            // This callback is now running within the pool thread execution
            // scope and not within Android's UI thread, so if we must update
            // the UI, we'll have to dispatch a runnable to the UI thread.
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    debugStatusCode(LOG_TAG, statusCode);
                    debugHeaders(LOG_TAG, headers);
                    debugThrowable(LOG_TAG, error);
                    if (responseBody != null) {
                        addView(getColoredView(LIGHTGREEN, "Download interrupted (" + statusCode + "): (bytes=" + responseBody.length + "), path: " + destFile.getAbsolutePath()));
                    }
                }
            });
        }

        private File getRandomCacheFile() {
            File dir = getCacheDir();
            if (dir == null) {
                dir = getFilesDir();
            }

            return new File(dir, "sample-" + System.currentTimeMillis() + ".bin");
        }

        private void saveBytesOnDisk(File destination, byte[] bytes) {
            // TODO: Spin your own implementation to save the bytes on disk/SD card.
            if (bytes != null && destination != null) {
                Log.d(LOG_TAG, "Saved " + bytes.length + " bytes into file: " + destination.getAbsolutePath());
            }
        }
    }
}
