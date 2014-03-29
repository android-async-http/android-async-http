package com.loopj.android.http.sample;

import android.util.Log;

import com.loopj.android.http.RequestHandle;

public class CancelRequestHandleSample extends ThreadingTimeoutSample {

    private static final String LOG_TAG = "ThreadingTimeoutSample";

    @Override
    public int getSampleTitle() {
        return R.string.title_cancel_handle;
    }

    @Override
    public void onCancelButtonPressed() {
        Log.d(LOG_TAG, String.format("Number of handles found: %d", getRequestHandles().size()));
        int counter = 0;
        for (RequestHandle handle : getRequestHandles()) {
            if (!handle.isCancelled() && !handle.isFinished()) {
                Log.d(LOG_TAG, String.format("Cancelling handle %d", counter));
                Log.d(LOG_TAG, String.format("Handle %d cancel", counter) + (handle.cancel(true) ? " succeeded" : " failed"));
            } else {
                Log.d(LOG_TAG, String.format("Handle %d already non-cancellable", counter));
            }
            counter++;
        }
    }
}
