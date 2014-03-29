package com.loopj.android.http.sample;

import com.loopj.android.http.RequestHandle;

public class CancelRequestHandle extends ThreadingTimeoutSample {

    @Override
    protected void onCancelButtonPressed() {
        for (RequestHandle handle : getRequestHandles()) {
            if (!handle.isCancelled() && !handle.isFinished()) {
                handle.cancel(true);
            }
        }
    }
}
