package com.loopj.android.http.sample;

public class CancelAllRequests extends ThreadingTimeoutSample {

    @Override
    protected void onCancelButtonPressed() {
        getAsyncHttpClient().cancelAllRequests(true);
    }
}
