package com.loopj.android.http.sample;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.http.Header;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class SampleParentActivity extends Activity {

    private LinearLayout headers; // Sample header, inputs and buttons
    private LinearLayout contents; // Sample output, states, errors, ...
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private EditText urlEditText;
    private static final LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private static final int LIGHTGREEN = Color.parseColor("#00FF66");
    private static final int LIGHTRED = Color.parseColor("#FF3300");
    private static final int YELLOW = Color.parseColor("#FFFF00");
    private static final int LIGHTBLUE = Color.parseColor("#99CCFF");

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
        Button executeButton = new Button(this);
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
                executeSample(getAsyncHttpClient(), (urlEditText == null || urlEditText.getText() == null) ? getDefaultURL() : urlEditText.getText().toString(), getResponseHandler());
            }
        });
    }

    protected final void debugHeaders(String TAG, Header[] headers) {
        if (headers != null) {
            Log.d(TAG, "Return Headers:");
            StringBuilder builder = new StringBuilder();
            for (Header h : headers) {
                String _h = String.format("%s : %s", h.getName(), h.getValue());
                Log.d(TAG, _h);
                builder.append(_h);
                builder.append("\n");
            }
            addView(getColoredView(YELLOW, builder.toString()));
        }
    }

    protected static String throwableToString(Throwable t) {
        if (t == null)
            return null;
        
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    protected final void debugThrowable(String TAG, Throwable t) {
        if (t != null) {
            Log.e(TAG, "AsyncHttpClient returned error", t);
            addView(getColoredView(LIGHTRED, throwableToString(t)));
        }
    }

    protected final void debugResponse(String TAG, String response) {
        if (response != null) {
            Log.d(TAG, "Response data:");
            Log.d(TAG, response);
            addView(getColoredView(LIGHTGREEN, response));
        }
    }

    protected final void debugStatusCode(String TAG, int statusCode) {
        String msg = String.format("Return Status Code: %d", statusCode);
        Log.d(TAG, msg);
        addView(getColoredView(LIGHTBLUE, msg));
    }

    public static int getContrastColor(int color) {
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    private View getColoredView(int bgColor, String msg) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(lParams);
        tv.setText(msg);
        tv.setBackgroundColor(bgColor);
        tv.setPadding(10, 10, 10, 10);
        tv.setTextColor(getContrastColor(bgColor));
        return tv;
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

    protected abstract void executeSample(AsyncHttpClient client, String URL, AsyncHttpResponseHandler responseHandler);
}
