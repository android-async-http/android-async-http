package com.loopj.android.http.sample;

import android.os.Bundle;

public class GetSample extends SampleParentActivity {

    @Override
    protected int getSampleTitle() {
        return R.string.get_sample;
    }

    @Override
    protected boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    protected boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
