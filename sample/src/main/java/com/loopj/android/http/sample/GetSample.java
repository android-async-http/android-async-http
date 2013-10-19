package com.loopj.android.http.sample;

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
}
