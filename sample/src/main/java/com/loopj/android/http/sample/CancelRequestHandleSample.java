package com.loopj.android.http.sample;

import com.loopj.android.http.RequestHandle;

public class CancelRequestHandleSample extends ThreadingTimeoutSample {

    @Override
    protected int getSampleTitle() {
        return R.string.title_cancel_handle;
    }

    @Override
    protected void onCancelButtonPressed() {
        for (RequestHandle handle : getRequestHandles()) {
            if (!handle.isCancelled() && !handle.isFinished()) {
                handle.cancel(true);
            }
        }
    }
}
