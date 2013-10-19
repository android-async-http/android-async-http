package com.loopj.android.http.sample;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.loopj.android.http.AsyncHttpClient;

public abstract class SampleParentActivity extends Activity {

    private LinearLayout headers; // Sample header, inputs and buttons
    private LinearLayout contents; // Sample output, states, errors, ...
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private EditText urlEditText;
    private Button executeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout content_wrapper = new LinearLayout(this);
        content_wrapper.setOrientation(LinearLayout.VERTICAL);
        content_wrapper.setLayoutParams(lParams);
        contents = new LinearLayout(this);
        contents.setLayoutParams(lParams);
        contents.setOrientation(LinearLayout.VERTICAL);
        headers = new LinearLayout(this);
        headers.setLayoutParams(lParams);
        headers.setOrientation(LinearLayout.VERTICAL);
        ScrollView contents_scroll = new ScrollView(this);
        contents_scroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        contents_scroll.setFillViewport(true);
        content_wrapper.addView(headers);
        content_wrapper.addView(contents);
        contents_scroll.addView(content_wrapper);
        setContentView(contents_scroll);
        setTitle(getSampleTitle());
        setupHeaders();
    }

    private void setupHeaders() {
        LinearLayout urlLayout = new LinearLayout(this);
        urlLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        urlLayout.setOrientation(LinearLayout.HORIZONTAL);
        urlEditText = new EditText(this);
        urlEditText.setHint("URL for request");
        urlEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        urlEditText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        urlLayout.addView(urlEditText);
        executeButton = new Button(this);
        executeButton.setText("Run");
        executeButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        urlLayout.addView(executeButton);
        headers.addView(urlLayout);
    }

    protected final void addView(View v) {
        contents.addView(v);
    }

    protected final void clearOutputs() {
        contents.removeAllViews();
    }

    protected abstract int getSampleTitle();

    protected abstract boolean isRequestBodyAllowed();

    protected abstract boolean isRequestHeadersAllowed();

    protected AsyncHttpClient getAsyncHttpClient() {
        return this.asyncHttpClient;
    }
}
