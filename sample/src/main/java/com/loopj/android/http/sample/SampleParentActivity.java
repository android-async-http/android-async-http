package com.loopj.android.http.sample;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public abstract class SampleParentActivity extends Activity {

    private LinearLayout headers; // Sample header, inputs and buttons
    private LinearLayout contents; // Sample output, states, errors, ...
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private EditText urlEditText;
    private Button executeButton;
    private static final LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        urlEditText.setText(getDefaultURL());
        urlEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        urlEditText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        urlLayout.addView(urlEditText);
        executeButton = new Button(this);
        executeButton.setText("Run");
        executeButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        urlLayout.addView(executeButton);
        headers.addView(urlLayout);
        if (isRequestHeadersAllowed()) {
            LinearLayout headersLayout = new LinearLayout(this);
            headersLayout.setOrientation(LinearLayout.VERTICAL);
            headersLayout.setLayoutParams(lParams);
        }
        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSample();
            }
        });
    }

    protected abstract void executeSample();

    protected final void debugHeaders(String TAG, Header[] headers) {
        if (headers != null) {
            Log.d(TAG, "Return Headers:");
            for (Header h : headers) {
                Log.d(TAG, String.format("%s : %s", h.getName(), h.getValue()));
            }
        }
    }

    protected final void debugThrowable(String TAG, Throwable t) {
        if (t != null) {
            Log.e(TAG, "AsyncHttpClient returned error", t);
        }
    }

    protected final void debugStatusCode(String TAG, int statusCode) {
        Log.d(TAG, String.format("Return Status Code: %d", statusCode));
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

    protected abstract String getDefaultURL();

    protected abstract AsyncHttpResponseHandler getResponseHandler();

    protected AsyncHttpClient getAsyncHttpClient() {
        return this.asyncHttpClient;
    }
}
